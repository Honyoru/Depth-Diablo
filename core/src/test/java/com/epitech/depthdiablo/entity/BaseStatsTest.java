package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** GDD 1.6, Q44, Q45: a character's base stats, before items. */
class BaseStatsTest {

  @Test
  void should_return_each_value_it_was_built_with() {
    BaseStats stats = new BaseStats(validValues());

    assertEquals(10f, stats.get(Stat.MAX_HP));
    assertEquals(3f, stats.get(Stat.ATTACK));
    assertEquals(2f, stats.get(Stat.DEFENSE));
    assertEquals(120f, stats.get(Stat.SPEED));
    assertEquals(1.5f, stats.get(Stat.ATTACK_SPEED));
    assertEquals(0f, stats.get(Stat.LUCK));
  }

  @Test
  void should_keep_its_values_when_the_map_changes_afterwards() {
    Map<Stat, Float> values = validValues();
    BaseStats stats = new BaseStats(values);

    values.put(Stat.ATTACK, 99f);

    assertEquals(3f, stats.get(Stat.ATTACK));
  }

  @ParameterizedTest
  @EnumSource(Stat.class)
  void should_throw_when_a_stat_is_missing(Stat missing) {
    Map<Stat, Float> values = validValues();
    values.remove(missing);

    assertThrows(IllegalArgumentException.class, () -> new BaseStats(values));
  }

  @ParameterizedTest
  @EnumSource(Stat.class)
  void should_throw_when_a_stat_is_null(Stat stat) {
    Map<Stat, Float> values = validValues();
    values.put(stat, null);

    assertThrows(NullPointerException.class, () -> new BaseStats(values));
  }

  @ParameterizedTest
  @EnumSource(Stat.class)
  void should_throw_when_a_stat_is_not_a_number(Stat stat) {
    Map<Stat, Float> values = validValues();
    values.put(stat, Float.NaN);

    assertThrows(IllegalArgumentException.class, () -> new BaseStats(values));
  }

  @ParameterizedTest
  @EnumSource(Stat.class)
  void should_throw_when_a_stat_is_infinite(Stat stat) {
    Map<Stat, Float> values = validValues();
    values.put(stat, Float.POSITIVE_INFINITY);

    assertThrows(IllegalArgumentException.class, () -> new BaseStats(values));
  }

  @ParameterizedTest
  @EnumSource(Stat.class)
  void should_throw_when_a_stat_is_negative(Stat stat) {
    Map<Stat, Float> values = validValues();
    values.put(stat, -1f);

    assertThrows(IllegalArgumentException.class, () -> new BaseStats(values));
  }

  @ParameterizedTest
  @EnumSource(
      value = Stat.class,
      names = {"MAX_HP", "DEFENSE"})
  void should_throw_when_a_stat_that_needs_1_is_below_1(Stat stat) {
    Map<Stat, Float> values = validValues();
    values.put(stat, 0.5f);

    assertThrows(IllegalArgumentException.class, () -> new BaseStats(values));
  }

  @ParameterizedTest
  @EnumSource(
      value = Stat.class,
      names = {"ATTACK", "SPEED", "ATTACK_SPEED", "LUCK"})
  void should_accept_0_when_the_stat_allows_it(Stat stat) {
    Map<Stat, Float> values = validValues();
    values.put(stat, 0f);

    BaseStats stats = new BaseStats(values);

    assertEquals(0f, stats.get(stat));
  }

  @Test
  void should_throw_when_the_map_is_null() {
    assertThrows(NullPointerException.class, () -> new BaseStats(null));
  }

  @Test
  void should_throw_when_the_stat_read_is_null() {
    BaseStats stats = new BaseStats(validValues());

    assertThrows(NullPointerException.class, () -> stats.get(null));
  }

  private static Map<Stat, Float> validValues() {
    Map<Stat, Float> values = new EnumMap<>(Stat.class);
    values.put(Stat.MAX_HP, 10f);
    values.put(Stat.ATTACK, 3f);
    values.put(Stat.DEFENSE, 2f);
    values.put(Stat.SPEED, 120f);
    values.put(Stat.ATTACK_SPEED, 1.5f);
    values.put(Stat.LUCK, 0f);
    return values;
  }
}
