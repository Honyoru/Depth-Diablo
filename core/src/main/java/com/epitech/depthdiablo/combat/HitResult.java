package com.epitech.depthdiablo.combat;

/**
 * What one hit did (GDD 1.6): the damage it dealt and whether it killed the target. A hit that
 * dealt no damage (the target was dead or invincible) did not land.
 *
 * @param damage the damage dealt; 0 if the hit did not land
 * @param killed whether this hit brought the target's HP to 0
 */
public record HitResult(int damage, boolean killed) {

  /** A hit that did not land: the target was dead or invincible. */
  public static final HitResult NONE = new HitResult(0, false);

  /**
   * Creates a hit result and checks it.
   *
   * @throws IllegalArgumentException if {@code damage} is negative, or the hit killed without
   *     dealing damage
   */
  public HitResult {
    if (damage < 0) {
      throw new IllegalArgumentException("Damage must not be negative: " + damage);
    }
    if (killed && damage == 0) {
      throw new IllegalArgumentException("A hit cannot kill without dealing damage");
    }
  }

  /**
   * Tells whether this hit landed: it dealt damage. Only a hit that landed counts for a hitbox,
   * fires a DamageDealt event and starts the target's invincibility.
   *
   * @return true if the damage is above 0
   */
  public boolean landed() {
    return damage > 0;
  }
}
