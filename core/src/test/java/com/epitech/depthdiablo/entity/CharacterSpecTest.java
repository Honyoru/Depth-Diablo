package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** The values of one character type, and a copy of them with other stats for F5. */
class CharacterSpecTest {
  private static final StatSource ONES = stat -> 1f;

  private final CharacterSpec rat =
      new CharacterSpec("rat", EnemyKind.RAT, "enemies/rat", ONES, 16f, 20f, 18);

  @Test
  void should_change_only_the_stats_when_given_other_stats() {
    StatSource sevens = stat -> 7f;

    CharacterSpec changed = rat.withStats(sevens);

    assertSame(sevens, changed.stats());
    assertEquals(
        new CharacterSpec("rat", EnemyKind.RAT, "enemies/rat", sevens, 16f, 20f, 18), changed);
  }

  @Test
  void should_keep_its_own_stats_when_copied_with_other_stats() {
    rat.withStats(stat -> 7f);

    assertSame(ONES, rat.stats());
  }

  @Test
  void should_reject_missing_stats_when_copied() {
    assertThrows(NullPointerException.class, () -> rat.withStats(null));
  }
}
