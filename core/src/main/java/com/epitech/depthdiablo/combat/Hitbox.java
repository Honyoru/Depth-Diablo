package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.entity.Updatable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;

/**
 * The area of an attack that deals damage (GDD 1.5): a melee swing, a projectile, a trap or a
 * contact attack. It hits a Damageable whose hurtbox it overlaps, never its owner, and each target
 * only once during its lifetime. It only answers whether it can hit; the owner applies the damage.
 *
 * <p>Its shape is always an {@link Obb}: one that turns for a melee swing, {@link Obb#around} a box
 * for the attacks that never turn.
 */
public final class Hitbox implements Updatable {

  private Obb shape;

  private final Entity owner;

  private final Faction faction;

  private int ticksLeft;

  private final Set<Damageable> alreadyHit = Collections.newSetFromMap(new IdentityHashMap<>());

  /**
   * Creates a hitbox.
   *
   * @param shape the area it covers in the world
   * @param owner the entity whose attack it is; it never hits its owner
   * @param faction the side the attack fights for, taken when the attack starts; a target of a side
   *     it cannot hurt is never hit
   * @param lifetimeTicks how many logic ticks it lasts (60 per second, Q42); above 0
   * @throws NullPointerException if {@code shape}, {@code owner} or {@code faction} is {@code null}
   * @throws IllegalArgumentException if {@code lifetimeTicks} is 0 or less
   */
  public Hitbox(Obb shape, Entity owner, Faction faction, int lifetimeTicks) {
    this.shape = Objects.requireNonNull(shape, "The shape of a hitbox must not be null");
    this.owner = Objects.requireNonNull(owner, "The owner of a hitbox must not be null");
    this.faction = Objects.requireNonNull(faction, "The faction of a hitbox must not be null");
    if (lifetimeTicks <= 0) {
      throw new IllegalArgumentException("Lifetime must be above 0 ticks: " + lifetimeTicks);
    }
    this.ticksLeft = lifetimeTicks;
  }

  /**
   * Returns the area this hitbox covers now.
   *
   * @return its shape
   */
  public Obb getShape() {
    return shape;
  }

  /**
   * Returns the entity whose attack this is.
   *
   * @return its owner, never hit by this hitbox
   */
  public Entity getOwner() {
    return owner;
  }

  /**
   * Returns the side this attack fights for.
   *
   * @return its faction, fixed when the attack started
   */
  public Faction getFaction() {
    return faction;
  }

  /**
   * Returns how many logic ticks this hitbox still lasts.
   *
   * @return the ticks left; 0 once it is expired
   */
  public int getTicksLeft() {
    return ticksLeft;
  }

  /**
   * Counts one logic tick of this hitbox's life down. Does nothing once it is expired, so the ticks
   * left never go below 0.
   *
   * @param tick the number of the current logic tick
   */
  @Override
  public void update(long tick) {
    if (ticksLeft > 0) {
      ticksLeft--;
    }
  }

  /**
   * Tells whether this hitbox's life is over. Its owner then drops it; it can no longer hit.
   *
   * @return true once the ticks left reach 0
   */
  public boolean isExpired() {
    return ticksLeft == 0;
  }

  /**
   * Tells whether this hitbox can hit the target now (GDD 1.5): it is not expired, the target is
   * not its owner, its faction can hurt the target's, it did not land a hit on the target yet, the
   * target is alive, and its hurtbox overlaps this hitbox's shape. It changes nothing: the owner
   * hits, then calls {@link #recordHit}.
   *
   * @param target the one that could be hit
   * @return true if the target can be hit now
   * @throws NullPointerException if {@code target} is {@code null}
   */
  public boolean canHit(Damageable target) {
    Objects.requireNonNull(target, "target is null");
    return !isExpired()
        && !owner.equals(target)
        && faction.canHurt(target.getFaction())
        && !alreadyHit.contains(target)
        && !target.isDead()
        && shape.overlaps(target.getHurtbox());
  }

  /**
   * Records the result of a hit on the target. A hit that landed marks the target, so this hitbox
   * never hits it again during its lifetime. A hit that did not land (the target was invincible)
   * does not count: the same attack can still hit it once its invincibility ends. A new attack
   * creates a new hitbox, which can hit the same target again.
   *
   * @param target the one that was hit
   * @param result what the hit did
   * @throws NullPointerException if {@code target} or {@code result} is {@code null}
   */
  public void recordHit(Damageable target, HitResult result) {
    Objects.requireNonNull(target, "target is null");
    Objects.requireNonNull(result, "result is null");
    if (result.landed()) {
      alreadyHit.add(target);
    }
  }

  /**
   * Replaces the area this hitbox covers, for an attack that moves or turns: a melee swing sweeping
   * towards the end of its arc, or a projectile in flight. The targets it already hit and its ticks
   * left stay the same, so a swing never hits the same target twice.
   *
   * @param newShape the area it covers from now on
   * @throws NullPointerException if {@code newShape} is {@code null}
   */
  public void reshape(Obb newShape) {
    this.shape = Objects.requireNonNull(newShape, "newShape is null");
  }
}
