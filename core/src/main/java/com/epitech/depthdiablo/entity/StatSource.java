package com.epitech.depthdiablo.entity;

/**
 * Gives a value to each {@link Stat}. A character's base stats are the innermost source; passive
 * items wrap it to change one or more stats (Decorator, GDD 1.3.1).
 */
@FunctionalInterface
public interface StatSource {

  /**
   * Returns the value of a stat.
   *
   * @param stat the stat to read
   * @return its value
   * @throws NullPointerException if {@code stat} is {@code null}
   */
  float get(Stat stat);
}
