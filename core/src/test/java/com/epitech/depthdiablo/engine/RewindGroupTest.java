package com.epitech.depthdiablo.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.arena.ArenaCycle;
import org.junit.jupiter.api.Test;

/** GDD 1.1.2, R: several objects saved as one photo, read back in the order they were written. */
class RewindGroupTest {

  @Test
  void should_put_every_part_back_in_the_order_it_was_written() {
    ArenaCycle first = new ArenaCycle(5, 10);
    ArenaCycle second = new ArenaCycle(5, 10);
    second.waveCleared(); // the second one is in its break
    RewindGroup group = new RewindGroup(first, second);
    StateBuffer photo = new StateBuffer();
    group.writeState(photo);
    first.waveCleared();
    second.update();

    photo.startReading();
    group.readState(photo);

    assertEquals(ArenaCycle.Phase.WAVE, first.getPhase());
    assertEquals(0, first.getScore());
    assertEquals(ArenaCycle.Phase.BREAK, second.getPhase());
    assertEquals(10, second.getBreakTicksLeft());
  }

  @Test
  void should_throw_when_it_has_no_part() {
    assertThrows(IllegalArgumentException.class, RewindGroup::new);
  }

  @Test
  void should_throw_when_a_part_is_null() {
    assertThrows(NullPointerException.class, () -> new RewindGroup(new ArenaCycle(5, 10), null));
  }
}
