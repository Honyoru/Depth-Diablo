package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.entity.stats.Stat;
import com.epitech.depthdiablo.entity.stats.StatSource;
import java.util.List;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1, 1.4: a stat ring adds a flat amount to one stat of its holder. */
class StatRingTest {
  private static final StatSource TENS = stat -> 10f;

  @Test
  void should_add_its_amount_to_its_stat() {
    StatRing ring = new StatRing("iron_band", "n", "s", 0, Stat.ATTACK, 2);

    assertEquals(12f, ring.decorateStats(TENS).get(Stat.ATTACK));
  }

  @Test
  void should_keep_the_other_stats() {
    StatRing ring = new StatRing("iron_band", "n", "s", 0, Stat.ATTACK, 2);

    StatSource stats = ring.decorateStats(TENS);

    assertEquals(10f, stats.get(Stat.DEFENSE));
    assertEquals(10f, stats.get(Stat.SPEED));
  }

  @Test
  void should_be_a_passive_item() {
    assertEquals(Rarity.PASSIVE, new StatRing("r", "n", "s", 0, Stat.SPEED, 40).getRarity());
  }

  @Test
  void should_show_its_stat_and_amount() {
    StatRing ring = new StatRing("amethyst_ring", "n", "s", 0, Stat.MAX_HP, 200);

    assertEquals(List.of(new ItemEffect("ring.max_hp", 200)), ring.getEffects());
  }

  @Test
  void should_throw_when_the_amount_is_not_a_finite_number_above_0() {
    assertThrows(
        IllegalArgumentException.class, () -> new StatRing("r", "n", "s", 0, Stat.ATTACK, 0));
    assertThrows(
        IllegalArgumentException.class,
        () -> new StatRing("r", "n", "s", 0, Stat.ATTACK, Float.NaN));
    assertThrows(
        IllegalArgumentException.class,
        () -> new StatRing("r", "n", "s", 0, Stat.ATTACK, Float.POSITIVE_INFINITY));
  }

  @Test
  void should_throw_when_the_stat_is_null() {
    assertThrows(NullPointerException.class, () -> new StatRing("r", "n", "s", 0, null, 1));
  }
}
