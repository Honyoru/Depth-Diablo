package com.epitech.depthdiablo.entity;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * A character's stats before any item, read from its class or enemy data (GDD 1.6, Q93). It is the
 * innermost {@link StatSource}: passive items wrap it.
 */
public final class BaseStats implements StatSource {
  private final Map<Stat, Float> values;

  /**
   * Creates base stats from one value per stat.
   *
   * @param values a value for every {@link Stat}; copied, so later changes to the map are ignored
   * @throws NullPointerException if the map or one of its values is {@code null}
   * @throws IllegalArgumentException if a stat is missing, not a finite number, negative, or below
   *     1 for MAX_HP and DEFENSE
   */
  public BaseStats(Map<Stat, Float> values) {
    Objects.requireNonNull(values, "values");
    this.values = new EnumMap<>(Stat.class);
    for (Stat stat : Stat.values()) {
      if (!values.containsKey(stat)) {
        throw new IllegalArgumentException("Missing stat: " + stat);
      }
      float value = Objects.requireNonNull(values.get(stat), stat.name());
      if (Float.isNaN(value) || Float.isInfinite(value) || value < minimum(stat)) {
        throw new IllegalArgumentException(
            stat + " must be a finite number of " + minimum(stat) + " or more: " + value);
      }
      this.values.put(stat, value);
    }
  }

  @Override
  public float get(Stat stat) {
    return values.get(Objects.requireNonNull(stat, "stat"));
  }

  private static float minimum(Stat stat) {
    return stat == Stat.MAX_HP || stat == Stat.DEFENSE ? 1f : 0f;
  }
}
