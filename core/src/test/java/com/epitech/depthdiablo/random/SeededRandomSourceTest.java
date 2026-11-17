package com.epitech.depthdiablo.random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

class SeededRandomSourceTest {
  private static final int SAMPLE_SIZE = 20;
  private static final int SAMPLE_BOUND = 1000;

  @Property
  void should_replay_the_same_sequence_when_the_seed_is_the_same(@ForAll long seed) {
    RandomSource first = new SeededRandomSource(seed);
    RandomSource second = new SeededRandomSource(seed);

    for (int i = 0; i < SAMPLE_SIZE; i++) {
      assertEquals(first.nextInt(SAMPLE_BOUND), second.nextInt(SAMPLE_BOUND));
    }
  }

  @Property
  void should_stay_below_the_bound_when_an_int_is_drawn(
      @ForAll long seed, @ForAll @IntRange(min = 1, max = SAMPLE_BOUND) int bound) {
    RandomSource source = new SeededRandomSource(seed);

    int value = source.nextInt(bound);

    assertTrue(value >= 0 && value < bound);
  }

  @Property
  void should_stay_in_the_unit_interval_when_a_double_is_drawn(@ForAll long seed) {
    RandomSource source = new SeededRandomSource(seed);

    double value = source.nextDouble();

    assertTrue(value >= 0.0 && value < 1.0);
  }

  @Test
  void should_expose_its_seed_when_asked() {
    RandomSource source = new SeededRandomSource(42L);

    assertEquals(42L, source.getSeed());
  }

  @Test
  void should_reject_a_bound_that_is_not_positive() {
    RandomSource source = new SeededRandomSource(1L);

    assertThrows(IllegalArgumentException.class, () -> source.nextInt(0));
  }
}
