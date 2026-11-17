package com.epitech.depthdiablo.combat;

public record BurnSpec(int damage, int intervalTicks, int durationTicks) {
  /**
   * Creates a BurnSpec and checks it.
   *
   * @throws IllegalArgumentException if {@code damage} is negative, or the hit killed without
   *     dealing damage
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
