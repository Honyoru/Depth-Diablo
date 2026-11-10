package com.epitech.depthdiablo.combat;

/**
 * Computes damage per hit using the formula in GDD 1.6, Q26: {@code damage = max(1, ((attack +
 * attackBonus) x attackBuff) / ((resistance + resistanceBonus) x resistanceBuff))}, rounded down.
 * Enemies have no buffs or bonuses (bonus 0, buff 1); the player gets them from the weapon, armor
 * and items.
 */
public final class DamageFormula {

  private DamageFormula() {
    // This utility class has no instance state.
  }

  /**
   * Computes the damage of one hit.
   *
   * @param attack the attacker's base attack
   * @param attackBonus the flat bonus added to the attack, e.g. from the weapon
   * @param attackBuff the multiplier applied to the attack side; 0 or more
   * @param resistance the target's base resistance; at least 1
   * @param resistanceBonus the flat bonus added to the resistance, e.g. from armor; 0 or more
   * @param resistanceBuff the multiplier applied to the resistance side; more than 0
   * @return the damage, never below 1
   * @throws IllegalArgumentException if the resistance is below 1, the resistance bonus is
   *     negative, the attack buff is negative, or a buff is not a finite number or the resistance
   *     buff is not above 0
   */
  public static int compute(
      int attack,
      int attackBonus,
      double attackBuff,
      int resistance,
      int resistanceBonus,
      double resistanceBuff) {
    if (resistance < 1) {
      throw new IllegalArgumentException("Resistance must be at least 1: " + resistance);
    }
    if (resistanceBonus < 0) {
      throw new IllegalArgumentException(
          "Resistance bonus must not be negative: " + resistanceBonus);
    }
    if (attackBuff < 0 || Double.isNaN(attackBuff) || Double.isInfinite(attackBuff)) {
      throw new IllegalArgumentException("Invalid attack buff: " + attackBuff);
    }
    if (!(resistanceBuff > 0) || Double.isInfinite(resistanceBuff)) {
      throw new IllegalArgumentException("Invalid resistance buff: " + resistanceBuff);
    }

    double damage =
        (attack + attackBonus) * attackBuff / ((resistance + resistanceBonus) * resistanceBuff);

    return (int) Math.max(1, Math.floor(damage));
  }
}
