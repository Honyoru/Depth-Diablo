package com.epitech.depthdiablo.combat;

/**
 * The damage formula of GDD 1.6: {@code max(1, (attack + bonus) * buff / resistance)}, rounded
 * down. Used for every hit, by the player and by enemies (enemies use buff = 1).
 */
public final class DamageFormula {
  private DamageFormula() {}

  /**
   * Computes the damage of one hit.
   *
   * @param attack the attacker's Attack stat
   * @param bonus the weapon's damage bonus
   * @param buff the multiplier from passive items, 1 when there is none
   * @param resistance the target's resistance, at least 1
   * @return the damage, never below 1
   * @throws IllegalArgumentException if the resistance is below 1 or the buff is negative
   */
  public static int compute(int attack, int bonus, double buff, int resistance) {
    if (resistance < 1) {
      throw new IllegalArgumentException("Resistance must be at least 1: " + resistance);
    }
    if (!(buff >= 0) || Double.isInfinite(buff)) {
      throw new IllegalArgumentException("Buff must be a finite number >= 0: " + buff);
    }
    double raw = (double) (attack + bonus) * buff / resistance;
    return (int) Math.max(1, Math.floor(raw));
  }
}
