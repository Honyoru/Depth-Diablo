package com.epitech.depthdiablo.entity.character;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Q40: the dash is fast and can be hit, the dodge roll is slower and invincible. */
class EvadeTest {

  @Test
  void should_dash_for_twelve_ticks_three_times_faster_then_wait_thirty() {
    assertEquals(12, Evade.DASH.getTicks());
    assertEquals(3f, Evade.DASH.getSpeedFactor());
    assertEquals(30, Evade.DASH.getCooldownTicks());
  }

  @Test
  void should_roll_for_twenty_four_ticks_a_bit_faster_then_wait_forty_eight() {
    assertEquals(24, Evade.DODGE_ROLL.getTicks());
    assertEquals(1.5f, Evade.DODGE_ROLL.getSpeedFactor());
    assertEquals(48, Evade.DODGE_ROLL.getCooldownTicks());
  }

  @Test
  void should_leave_the_dasher_open_to_hits() {
    Character mover = aCharacter().build();

    Evade.DASH.onStart(mover, 12);

    assertFalse(mover.isInvincible());
  }

  @Test
  void should_keep_the_roller_invincible_for_the_whole_roll_and_no_longer() {
    Character mover = aCharacter().build();

    Evade.DODGE_ROLL.onStart(mover, 24);
    for (int tick = 0; tick < 23; tick++) {
      mover.update(tick);
    }
    assertTrue(mover.isInvincible());
    mover.update(23);

    assertFalse(mover.isInvincible());
  }
}
