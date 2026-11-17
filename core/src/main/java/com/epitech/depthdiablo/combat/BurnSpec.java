package com.epitech.depthdiablo.combat;

/**
 * The values of a burn, read from {@code data/items.json} (Q93), all counted in logic ticks (60 per
 * second).
 *
 * <p>Pattern: Value Object.
 *
 * @param damage the HP lost at the end of each interval; at least 1
 * @param intervalTicks the ticks between two damages; at least 1
 * @param durationTicks how many ticks the burn lasts; at least 1
 */
public record BurnSpec(int damage, int intervalTicks, int durationTicks) {
  /**
   * Checks the values.
   *
   * @throws IllegalArgumentException if a value is less than 1
   */
  public BurnSpec {
    if (damage < 1) {
      throw new IllegalArgumentException("Damage must not be negative: " + damage);
    }
    if (intervalTicks <= 0) {
      throw new IllegalArgumentException("intervalTicks must be positive" + intervalTicks);
    }
    if (durationTicks <= 0) {
      throw new IllegalArgumentException("durationTicks must be positive" + durationTicks);
    }
  }
}
