package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.HitResult;
import com.epitech.depthdiablo.combat.Hitbox;
import com.epitech.depthdiablo.combat.Obb;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;
import java.util.Optional;

/**
 * A shot that flies straight until a wall stops it or its lifetime ends: an arrow, a fireball (GDD
 * 1.3.1). Its hitbox follows it and points where it flies (GDD 1.5). It hurts through its owner's
 * attack, so the owner's decorators, such as a FireTouch, apply to it (GDD 1.4).
 *
 * <p>Pattern: Template Method ({@link #onHit} is the hook each projectile fills).
 */
public abstract class Projectile extends Entity {
  private final Character owner;
  private final Vector2 velocity;
  private Hitbox hitbox;
  private final World world;

  /**
   * Creates a projectile. Its length along its flight is the width of its body box, its thickness
   * the height.
   *
   * @param bodyBox where it starts; must not be {@code null}
   * @param owner the character who shot it; its attack and side are the projectile's
   * @param aimDegrees the direction it flies to, in degrees (0 = right, 90 = up)
   * @param speed how fast it flies, in pixels per second; above 0
   * @param lifetimeTicks how many ticks it flies before it is removed; above 0
   * @param world the world it flies in: its walls stop it, its characters can be hurt
   * @throws NullPointerException if {@code bodyBox}, {@code owner} or {@code world} is {@code null}
   * @throws IllegalArgumentException if the aim or the speed is not a finite number, the speed is 0
   *     or less, or the lifetime is 0 or less
   */
  protected Projectile(
      Aabb bodyBox,
      Character owner,
      float aimDegrees,
      float speed,
      int lifetimeTicks,
      World world) {
    super(bodyBox);
    if (!Float.isFinite(aimDegrees)) {
      throw new IllegalArgumentException("The aim must be a finite number, was " + aimDegrees);
    }
    if (!Float.isFinite(speed) || speed <= 0) {
      throw new IllegalArgumentException("The speed must be above 0, was " + speed);
    }
    this.owner = Objects.requireNonNull(owner, "The owner of a projectile must not be null");
    this.world = Objects.requireNonNull(world, "The world of a projectile must not be null");
    this.velocity = new Vector2(speed / GameConstants.TICKS_PER_SECOND, 0f).setAngleDeg(aimDegrees);
    this.hitbox = new Hitbox(shapeOf(bodyBox, velocity), owner, owner.getFaction(), lifetimeTicks);
  }

    /**
   * Returns the character who shot this projectile.
   *
   * @return its owner, whose attack and side the projectile uses
   */
  protected final Character getOwner() {
    return owner;
  }

  /**
   * Flies one tick: moves by its velocity, then hurts every character its hitbox touches. A wall in
   * the way removes it, and so does the end of its lifetime.
   *
   * @param tick the number of the current logic tick
   */
  @Override
  protected final void onUpdate(long tick) {
    Aabb next = getBodyBox().translated(velocity.x, velocity.y);
    if (!world.isFree(next, this)) {
      remove();
      return;
    }
    moveTo(new Vector2(next.x(), next.y()));
    hitbox.reshape(shapeOf(getBodyBox(), velocity));
    for (Entity entity : world.getEntities()) {
      if (entity instanceof Character target && !isRemoved() && hitbox.canHit(target)) {
        strike(target);
      }
    }
    hitbox.update(tick);
    if (hitbox.isExpired()) {
      remove();
    }
  }

  /**
   * Writes the entity part, then the ticks its hitbox still lasts (GDD 1.1.2). The rest never
   * changes in flight, so it is not saved: the owner, the world and the velocity.
   *
   * @param out the photo being taken
   */
  @Override
  public void writeState(StateBuffer out) {
    super.writeState(out);
    out.writeInt(hitbox.getTicksLeft());
  }

  /**
   * Puts back what {@link #writeState} wrote, in the same order, with a new hitbox over the
   * position it is back to. That hitbox has hit no one yet, which is right for a projectile that
   * stops at its first hit, as the arrow does: one in a photo never landed a hit, since that hit
   * removed it and the world dropped it at the end of the tick.
   *
   * @param in the photo being restored
   */
  @Override
  public void readState(StateBuffer in) {
    super.readState(in);
    hitbox = new Hitbox(shapeOf(getBodyBox(), velocity), owner, owner.getFaction(), in.readInt());
  }

  /**
   * What the projectile does once it hurt a character. A hit on an invincible character, such as
   * one rolling, lands nothing and does not call it.
   *
   * @param target the character it just hurt
   */
  protected abstract void onHit(Character target);

  private void strike(Character target) {
    HitResult result = owner.hit(target);
    hitbox.recordHit(target, result);
    if (result.landed()) {
      world.getEventBus().publish(new DamageDealt(owner, target, result.damage()));
      if (result.killed()) {
        world.getEventBus().publish(new EntityDied(target, Optional.of(owner)));
      }
      onHit(target);
    }
  }

  /** The hitbox over the given body box, its length pointing where the projectile flies. */
  private static Obb shapeOf(Aabb body, Vector2 velocity) {
    return new Obb(
        body.x() + body.width() / 2,
        body.y() + body.height() / 2,
        velocity.angleDeg(),
        body.width(),
        body.height());
  }
}
