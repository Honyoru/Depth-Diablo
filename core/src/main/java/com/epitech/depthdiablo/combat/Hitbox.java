package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.entity.Updatable;
import java.util.Objects;

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
  private int ticksLeft;

  /**
   * Creates a hitbox.
   *
   * @param shape the area it covers in the world
   * @param owner the entity whose attack it is; it never hits its owner
   * @param lifetimeTicks how many logic ticks it lasts (60 per second, Q42); above 0
   * @throws NullPointerException if {@code shape} or {@code owner} is {@code null}
   * @throws IllegalArgumentException if {@code lifetimeTicks} is 0 or less
   */
  public Hitbox(Obb shape, Entity owner, int lifetimeTicks) {
    this.shape = Objects.requireNonNull(shape, "The shape of a hitbox must not be null");
    this.owner = Objects.requireNonNull(owner, "The owner of a hitbox must not be null");
    if (lifetimeTicks <= 0) {
      throw new IllegalArgumentException("Lifetime must be above 0 ticks: " + lifetimeTicks);
    }
    this.ticksLeft = lifetimeTicks;
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
   * Returns the area this hitbox covers now.
   *
   * @return its shape
   */
  public Obb shape() {
    return shape;
  }

  /**
   * Returns the entity whose attack this is.
   *
   * @return its owner, never hit by this hitbox
   */
  public Entity owner() {
    return owner;
  }

  /**
   * Returns how many logic ticks this hitbox still lasts.
   *
   * @return the ticks left; 0 once it is expired
   */
  public int ticksLeft() {
    return ticksLeft;
  }
}
