package com.epitech.depthdiablo.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.arena.ArenaCycle;
import com.epitech.depthdiablo.arena.ArenaCycle.Phase;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

/**
 * GDD 1.4.1, "Levels", the Arena mode rules (1.2). Still to add when their classes land: floor
 * reachability, loop count and seed (BspLoopGenerator), keys behind their doors, teleport zone.
 */
class LevelRulesTest {
  private static final int WAVES_PER_CYCLE = 5;
  private static final int BREAK_TICKS = 15 * GameConstants.TICKS_PER_SECOND;

  private static ArenaCycle newCycle() {
    return new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
  }

  private static void clearWaves(ArenaCycle cycle, int count) {
    for (int i = 0; i < count; i++) {
      if (cycle.phase() == Phase.SHOP) {
        cycle.startNextCycle();
      }
      cycle.waveCleared();
      for (int t = 0; t < BREAK_TICKS && cycle.phase() == Phase.BREAK; t++) {
        cycle.update(t);
      }
    }
  }

  @Test
  void should_make_every_5th_wave_a_boss_wave() {
    ArenaCycle cycle = newCycle();
    StringBuilder bossWaves = new StringBuilder();

    for (int wave = 1; wave <= 10; wave++) {
      if (cycle.phase() == Phase.SHOP) {
        cycle.startNextCycle();
      }
      if (cycle.isBossWave()) {
        bossWaves.append(cycle.wave()).append(' ');
      }
      clearWaves(cycle, 1);
    }

    assertEquals("5 10 ", bossWaves.toString());
  }

  @Test
  void should_open_the_shop_when_a_boss_wave_is_cleared() {
    ArenaCycle cycle = newCycle();
    clearWaves(cycle, WAVES_PER_CYCLE - 1);

    cycle.waveCleared();

    assertEquals(Phase.SHOP, cycle.phase());
  }

  @Test
  void should_not_open_the_shop_when_a_normal_wave_is_cleared() {
    ArenaCycle cycle = newCycle();

    cycle.waveCleared();

    assertEquals(Phase.BREAK, cycle.phase());
  }

  @Test
  void should_wait_15_seconds_before_the_next_wave_when_a_normal_wave_is_cleared() {
    ArenaCycle cycle = newCycle();
    cycle.waveCleared();

    for (int t = 0; t < BREAK_TICKS - 1; t++) {
      cycle.update(t);
    }
    Phase justBefore = cycle.phase();
    cycle.update(BREAK_TICKS);

    assertEquals(Phase.BREAK, justBefore);
    assertEquals(Phase.WAVE, cycle.phase());
    assertEquals(2, cycle.wave());
  }

  @Test
  void should_wait_for_the_player_when_the_shop_is_open() {
    ArenaCycle cycle = newCycle();
    clearWaves(cycle, WAVES_PER_CYCLE);

    for (int t = 0; t < 10 * BREAK_TICKS; t++) {
      cycle.update(t);
    }

    assertEquals(Phase.SHOP, cycle.phase());
  }

  @Test
  void should_start_the_next_cycle_when_the_player_activates_it() {
    ArenaCycle cycle = newCycle();
    clearWaves(cycle, WAVES_PER_CYCLE);

    boolean started = cycle.startNextCycle();

    assertTrue(started);
    assertEquals(Phase.WAVE, cycle.phase());
    assertEquals(WAVES_PER_CYCLE + 1, cycle.wave());
    assertFalse(cycle.isBossWave());
  }

  @Property
  void should_score_the_number_of_waves_defeated(@ForAll @IntRange(min = 0, max = 30) int count) {
    ArenaCycle cycle = newCycle();

    clearWaves(cycle, count);

    assertEquals(count, cycle.score());
  }
}
