package com.epitech.depthdiablo.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.GameConstants;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import org.junit.jupiter.api.Test;

class FixedTimestepTest {
  private static final double ONE_TICK = 1.0 / GameConstants.TICKS_PER_SECOND;

  @Test
  void should_run_no_tick_when_less_than_one_tick_has_elapsed() {
    FixedTimestep timestep = new FixedTimestep();

    int ticks = timestep.advance(ONE_TICK / 2);

    assertEquals(0, ticks);
  }

  @Test
  void should_run_one_tick_when_exactly_one_tick_has_elapsed() {
    FixedTimestep timestep = new FixedTimestep();

    int ticks = timestep.advance(ONE_TICK);

    assertEquals(1, ticks);
  }

  @Test
  void should_carry_the_remainder_when_a_frame_is_shorter_than_a_tick() {
    FixedTimestep timestep = new FixedTimestep();

    int first = timestep.advance(ONE_TICK * 0.6);
    int second = timestep.advance(ONE_TICK * 0.6);

    assertEquals(0, first);
    assertEquals(1, second);
  }

  @Test
  void should_cap_the_ticks_when_a_frame_is_very_long() {
    FixedTimestep timestep = new FixedTimestep();

    int ticks = timestep.advance(60.0);

    assertEquals(FixedTimestep.MAX_TICKS_PER_FRAME, ticks);
  }

  @Test
  void should_drop_the_backlog_when_the_cap_was_hit() {
    FixedTimestep timestep = new FixedTimestep();
    timestep.advance(60.0);

    int ticks = timestep.advance(0.0);

    assertEquals(0, ticks);
  }

  @Test
  void should_reject_a_negative_delta() {
    FixedTimestep timestep = new FixedTimestep();

    assertThrows(IllegalArgumentException.class, () -> timestep.advance(-0.1));
  }

  @Test
  void should_reject_a_delta_that_is_not_a_number() {
    FixedTimestep timestep = new FixedTimestep();

    assertThrows(IllegalArgumentException.class, () -> timestep.advance(Double.NaN));
  }

  @Test
  void should_have_an_alpha_of_0_when_nothing_was_advanced() {
    FixedTimestep timestep = new FixedTimestep();

    assertEquals(0f, timestep.getAlpha(), 0.001f);
  }

  @Test
  void should_have_an_alpha_of_one_half_when_half_a_tick_is_left_over() {
    FixedTimestep timestep = new FixedTimestep();

    timestep.advance(ONE_TICK * 1.5);

    assertEquals(0.5f, timestep.getAlpha(), 0.001f);
  }

  @Test
  void should_have_an_alpha_of_0_when_the_cap_dropped_the_backlog() {
    FixedTimestep timestep = new FixedTimestep();

    timestep.advance(60.0);

    assertEquals(0f, timestep.getAlpha(), 0.001f);
  }

  @Property
  void should_stay_within_the_frame_cap_when_any_delta_is_given(
      @ForAll @DoubleRange(min = 0.0, max = 100.0) double delta) {
    FixedTimestep timestep = new FixedTimestep();

    int ticks = timestep.advance(delta);

    assertTrue(ticks >= 0 && ticks <= FixedTimestep.MAX_TICKS_PER_FRAME);
  }
}
