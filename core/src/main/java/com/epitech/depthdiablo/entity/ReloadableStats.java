package com.epitech.depthdiablo.entity;

import java.util.Objects;

/**
 * The stats of one character type that F5 can swap while the game runs (GDD 1.1.2). The {@link
 * CharacterFactory} gives the same holder to every character of one id, and characters read their
 * stats through it on every call, so after {@link #replace} every live rat uses the new values,
 * with its weapon and passive items still on top (GDD 1.4.1: a reload changes stats on live
 * entities).
 *
 * <p>Package-private: only the {@link CharacterFactory} creates and swaps it.
 *
 * <p>Pattern: Proxy, the innermost {@link StatSource} of a character's build.
 */
final class ReloadableStats implements StatSource {

  private StatSource current;

  /**
   * Creates a holder that gives the values of the given stats until it is replaced.
   *
   * @param initial the stats read from the data file, usually a {@link BaseStats}
   * @throws NullPointerException if {@code initial} is null
   */
  ReloadableStats(StatSource initial) {
    Objects.requireNonNull(initial, "The initial Stat should not be null");
    this.current = initial;
  }

  /**
   * Returns the value of a stat from the current stats.
   *
   * @param stat the stat to read
   * @return its value in the stats given last
   * @throws NullPointerException if {@code stat} is {@code null}
   */
  @Override
  public float get(Stat stat) {
    return current.get(stat);
  }

  /**
   * Swaps the stats: from now on, every character holding this object reads the new values.
   *
   * @param next the stats read again from the data file
   * @throws NullPointerException if {@code next} is null; the old stats stay
   */
  void replace(StatSource next) {
    this.current = Objects.requireNonNull(next, "The new stats must not be null");
  }
}
