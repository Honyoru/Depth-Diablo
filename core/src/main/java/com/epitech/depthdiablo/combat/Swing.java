package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.EventBus;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.entity.Updatable;
import com.epitech.depthdiablo.item.MeleeWeapon;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * One melee attack in progress (GDD 1.6): the blade of a {@link MeleeWeapon} sweeps its arc around
 * the holder, one step per tick, and hits each enemy it touches once.
 *
 * <p>The holder owns its swing and updates it each tick. The blade turns around the holder's
 * centre, so it follows the holder if it moves during the swing.
 */
public final class Swing implements Updatable {

  private final MeleeWeapon weapon;

  private final Character holder;

  private final float aimDegrees;

  private final Supplier<List<Entity>> targets;

  private final EventBus events;

  private final Hitbox hitbox;

  /**
   * Starts a swing. The blade begins half an arc before the aim.
   *
   * @param weapon the weapon that swings
   * @param holder the character who swings it; the blade turns around its centre
   * @param aimDegrees the direction of the attack, in degrees (0 = right, 90 = up); it stays the
   *     same for the whole swing
   * @param targets gives the entities the blade may hit; it is read again at each tick
   * @param events where the swing publishes the damage it deals and the deaths it causes
   * @throws NullPointerException if an argument is {@code null}
   * @throws IllegalArgumentException if the aim is not a finite number
   */
  public Swing(
      MeleeWeapon weapon,
      Character holder,
      float aimDegrees,
      Supplier<List<Entity>> targets,
      EventBus events) {
    this.weapon = Objects.requireNonNull(weapon, "weapon is null");
    this.holder = Objects.requireNonNull(holder, "holder is null");
    this.targets = Objects.requireNonNull(targets, "targets is null");
    this.events = Objects.requireNonNull(events, "events is null");
    if (!Float.isFinite(aimDegrees)) {
      throw new IllegalArgumentException("Aim must be a finite number: " + aimDegrees);
    }
    this.aimDegrees = aimDegrees;
    this.hitbox = new Hitbox(bladeAt(0), holder, holder.getFaction(), weapon.getSwingTicks());
  }

  /**
   * Returns where the blade is now.
   *
   * @return the blade as a rotated rectangle
   */
  public Obb getShape() {
    return hitbox.getShape();
  }

  /**
   * Returns the character who swings.
   *
   * @return the holder
   */
  public Character getHolder() {
    return holder;
  }

  /**
   * Returns the direction of the attack, chosen when the swing started.
   *
   * @return the aim in degrees
   */
  public float getAimDegrees() {
    return aimDegrees;
  }

  /**
   * Returns how many ticks of the swing already ran.
   *
   * @return from 0 to the weapon's swing ticks
   */
  public int getTicksElapsed() {
    return weapon.getSwingTicks() - hitbox.getTicksLeft();
  }

  /**
   * Tells how far the swing is, for the renderer to pick the frame of the attack animation.
   *
   * @return from 0 when it starts to 1 once it is over
   */
  public float getProgress() {
    return (float) getTicksElapsed() / weapon.getSwingTicks();
  }

  /**
   * Runs one tick of the swing: the blade moves to its next step around the holder, then hits each
   * target it touches, once per swing. A hit that lands publishes a DamageDealt event, and an
   * EntityDied event if it kills. A dead holder hits nothing, but its swing still runs to the end.
   * Does nothing once the swing is over.
   *
   * @param tick the number of the current logic tick
   */
  @Override
  public void update(long tick) {
    if (isOver()) {
      return;
    }
    hitbox.reshape(bladeAt(getTicksElapsed()));
    if (!holder.isDead()) {
      for (Entity entity : targets.get()) {
        if (entity instanceof Character target && hitbox.canHit(target)) {
          strike(target);
        }
      }
    }
    hitbox.update(tick);
  }

  /**
   * Tells whether every tick of the swing ran. The holder then drops it.
   *
   * @return true once the swing is over
   */
  public boolean isOver() {
    return hitbox.isExpired();
  }

  private void strike(Character target) {
    HitResult result = holder.hit(target);
    hitbox.recordHit(target, result);
    if (result.landed()) {
      events.publish(new DamageDealt(holder, target, result.damage()));
      if (result.killed()) {
        events.publish(new EntityDied(target, Optional.of(holder)));
      }
    }
  }

  private Obb bladeAt(int tickIndex) {
    Aabb body = holder.getBodyBox();
    float pivotX = body.x() + body.width() / 2;
    float pivotY = body.y() + body.height() / 2;
    return weapon.swingShape(pivotX, pivotY, aimDegrees, progressAt(tickIndex));
  }

  /** Q3: tick i of n is at i / (n - 1), so the blade reaches both edges; 1 tick stabs the aim. */
  private float progressAt(int tickIndex) {
    int last = weapon.getSwingTicks() - 1;
    return last == 0 ? 0.5f : (float) tickIndex / last;
  }
}
