package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** F5 (GDD 1.1.2): stats that a hot reload can swap under live characters. */
class ReloadableStatsTest {

  @Test
  void should_give_the_values_of_its_first_stats() {
    ReloadableStats stats = new ReloadableStats(attack(10f));

    assertEquals(10f, stats.get(Stat.ATTACK));
  }

  @Test
  void should_give_the_new_values_when_replaced() {
    ReloadableStats stats = new ReloadableStats(attack(10f));

    stats.replace(attack(25f));

    assertEquals(25f, stats.get(Stat.ATTACK));
  }

  @Test
  void should_reject_missing_first_stats() {
    assertThrows(NullPointerException.class, () -> new ReloadableStats(null));
  }

  @Test
  void should_keep_its_stats_when_replaced_by_nothing() {
    ReloadableStats stats = new ReloadableStats(attack(10f));

    assertThrows(NullPointerException.class, () -> stats.replace(null));

    assertEquals(10f, stats.get(Stat.ATTACK));
  }

  /** Stats with the given attack and 1 everywhere else. */
  private static StatSource attack(float value) {
    return stat -> stat == Stat.ATTACK ? value : 1f;
  }
}
