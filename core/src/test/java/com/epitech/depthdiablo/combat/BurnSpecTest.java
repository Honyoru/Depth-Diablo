package com.epitech.depthdiablo.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** GDD 1.3.1: the values of a burn, read from data/items.json and counted in ticks. */
class BurnSpecTest {

  @Test
  void should_keep_its_values_when_created() {
    BurnSpec spec = new BurnSpec(2, 30, 120);

    assertEquals(2, spec.damage());
    assertEquals(30, spec.intervalTicks());
    assertEquals(120, spec.durationTicks());
  }

  @Test
  void should_be_equal_to_a_spec_with_the_same_values() {
    assertEquals(new BurnSpec(2, 30, 120), new BurnSpec(2, 30, 120));
  }

  @Test
  void should_throw_when_the_damage_is_0() {
    assertThrows(IllegalArgumentException.class, () -> new BurnSpec(0, 30, 120));
  }

  @Test
  void should_throw_when_the_damage_is_negative() {
    assertThrows(IllegalArgumentException.class, () -> new BurnSpec(-1, 30, 120));
  }

  @Test
  void should_throw_when_the_interval_is_0() {
    assertThrows(IllegalArgumentException.class, () -> new BurnSpec(2, 0, 120));
  }

  @Test
  void should_throw_when_the_interval_is_negative() {
    assertThrows(IllegalArgumentException.class, () -> new BurnSpec(2, -1, 120));
  }

  @Test
  void should_throw_when_the_duration_is_0() {
    assertThrows(IllegalArgumentException.class, () -> new BurnSpec(2, 30, 0));
  }

  @Test
  void should_throw_when_the_duration_is_negative() {
    assertThrows(IllegalArgumentException.class, () -> new BurnSpec(2, 30, -1));
  }
}
