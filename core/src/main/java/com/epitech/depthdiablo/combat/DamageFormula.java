package com.epitech.depthdiablo.combat;

/**
 * Computes damage per hit using the formula in GDD 1.6, Q26; used by enemies with buff = 1 and by
 * the player through Weapon.hit().
 */
public final class DamageFormula {

  private DamageFormula() {
    // This utility class has no instance state.
  }

  /**
   * Computes the damage of one hit based on the given parameters.
   *
   * @param attack The base attack power.
   * @param bonus The additional bonus damage.
   * @param buff The multiplier applied to the attack and bonus.
   * @param resistance The resistance value.
   * @return The computed damage (never below 1).
   * @throws IllegalArgumentException if the resistance is below 1 or the buff is invalid.
   */
  public static int compute(int attack, int bonus, double buff, int resistance) {
    if (resistance < 1) {
      throw new IllegalArgumentException("Resistance must be at least 1: " + resistance);
    }

    if (buff < 0 || Double.isNaN(buff) || Double.isInfinite(buff)) {
      throw new IllegalArgumentException("Invalid buff value: " + buff);
    }

    double damage = (attack + bonus) * buff / resistance;

    return (int) Math.max(1, Math.floor(damage));
  }
}
