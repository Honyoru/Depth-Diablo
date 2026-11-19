package com.epitech.depthdiablo.arena;

import com.epitech.depthdiablo.GameConstants;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

/**
 * Arena mode (GDD 1.6, DECISIONS): cycles of waves, boss wave, break between waves, shop, score.
 */
class ArenaCycleTest {

  private static final int WAVES_PER_CYCLE = 5;
  private static final int BREAK_TICKS = 15 * GameConstants.TICKS_PER_SECOND;

  private static void clearWaves(ArenaCycle arena, int count) {
    for (int i = 0; i < count; i++) {
      arena.waveCleared();
      if ((i + 1) % WAVES_PER_CYCLE == 0) {
        arena.startNextCycle();
      } else {
        for (int tick = 0; tick < BREAK_TICKS; tick++) {
          arena.update();
        }
      }
    }
  }

  // ---------- start ----------

  @Test
  void should_start_at_wave_1_in_wave_phase_with_score_0_when_created() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);

    org.junit.jupiter.api.Assertions.assertEquals(1, arena.getWaveNumber());
    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.WAVE, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(0, arena.getScore());
  }

  // ---------- boss waves ----------

  @Test
  void should_not_be_a_boss_wave_when_the_wave_is_1_to_4() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);

    for (int wave = 1; wave <= 4; wave++) {
      org.junit.jupiter.api.Assertions.assertEquals(wave, arena.getWaveNumber());
      org.junit.jupiter.api.Assertions.assertFalse(arena.isBossWave());
      if (wave < 4) {
        arena.waveCleared();
        for (int tick = 0; tick < BREAK_TICKS; tick++) {
          arena.update();
        }
      }
    }
  }

  @Test
  void should_be_a_boss_wave_when_the_wave_is_5() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
    clearWaves(arena, 4);

    org.junit.jupiter.api.Assertions.assertEquals(5, arena.getWaveNumber());
    org.junit.jupiter.api.Assertions.assertTrue(arena.isBossWave());
  }

  @Test
  void should_be_a_boss_wave_when_the_wave_is_10() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
    clearWaves(arena, 9);

    org.junit.jupiter.api.Assertions.assertEquals(10, arena.getWaveNumber());
    org.junit.jupiter.api.Assertions.assertTrue(arena.isBossWave());
  }

  // ---------- normal wave cleared -> BREAK ----------

  @Test
  void should_start_a_break_when_a_normal_wave_is_cleared() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);

    arena.waveCleared();

    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.BREAK, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(1, arena.getScore());
    org.junit.jupiter.api.Assertions.assertEquals(1, arena.getWaveNumber());
  }

  @Test
  void should_stay_in_break_when_the_timer_has_not_reached_0() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
    arena.waveCleared();

    for (int tick = 0; tick < BREAK_TICKS - 1; tick++) {
      arena.update();
    }

    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.BREAK, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(1, arena.getWaveNumber());
  }

  @Test
  void should_start_the_next_wave_when_the_break_timer_reaches_0() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
    arena.waveCleared();

    for (int tick = 0; tick < BREAK_TICKS; tick++) {
      arena.update();
    }

    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.WAVE, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(2, arena.getWaveNumber());
  }

  @Test
  void should_ignore_update_when_in_wave_phase() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);

    for (int tick = 0; tick < BREAK_TICKS; tick++) {
      arena.update();
    }

    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.WAVE, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(1, arena.getWaveNumber());
    org.junit.jupiter.api.Assertions.assertEquals(0, arena.getScore());
  }

  // ---------- boss wave cleared -> SHOP ----------

  @Test
  void should_open_the_shop_when_a_boss_wave_is_cleared() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
    clearWaves(arena, 4);

    arena.waveCleared();

    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.SHOP, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(5, arena.getScore());
    org.junit.jupiter.api.Assertions.assertEquals(5, arena.getWaveNumber());
  }

  @Test
  void should_stay_in_the_shop_when_time_passes() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
    clearWaves(arena, 4);
    arena.waveCleared();

    for (int tick = 0; tick < BREAK_TICKS; tick++) {
      arena.update();
    }

    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.SHOP, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(5, arena.getWaveNumber());
    org.junit.jupiter.api.Assertions.assertEquals(5, arena.getScore());
  }

  // ---------- startNextCycle ----------

  @Test
  void should_start_the_next_cycle_when_the_shop_is_open() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
    clearWaves(arena, 4);
    arena.waveCleared();

    org.junit.jupiter.api.Assertions.assertTrue(arena.startNextCycle());
    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.WAVE, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(6, arena.getWaveNumber());
  }

  @Test
  void should_not_start_the_next_cycle_when_in_wave_phase() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);

    org.junit.jupiter.api.Assertions.assertFalse(arena.startNextCycle());
    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.WAVE, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(1, arena.getWaveNumber());
    org.junit.jupiter.api.Assertions.assertEquals(0, arena.getScore());
  }

  @Test
  void should_not_start_the_next_cycle_when_in_break_phase() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
    arena.waveCleared();

    org.junit.jupiter.api.Assertions.assertFalse(arena.startNextCycle());
    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.BREAK, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(1, arena.getWaveNumber());
    org.junit.jupiter.api.Assertions.assertEquals(1, arena.getScore());
  }

  // ---------- no shop ----------

  @Test
  void should_start_a_break_instead_of_the_shop_when_the_shop_is_disabled() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS, false);
    clearWaves(arena, 4);

    arena.waveCleared();

    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.BREAK, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(5, arena.getScore());
  }

  @Test
  void should_start_wave_6_after_the_break_when_the_shop_is_disabled() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS, false);
    clearWaves(arena, 4);
    arena.waveCleared();

    for (int tick = 0; tick < BREAK_TICKS; tick++) {
      arena.update();
    }

    org.junit.jupiter.api.Assertions.assertEquals(ArenaCycle.Phase.WAVE, arena.getPhase());
    org.junit.jupiter.api.Assertions.assertEquals(6, arena.getWaveNumber());
  }

  @Test
  void should_not_start_a_next_cycle_when_the_shop_is_disabled() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS, false);
    clearWaves(arena, 4);
    arena.waveCleared();

    org.junit.jupiter.api.Assertions.assertFalse(arena.startNextCycle());
  }

  @Test
  void should_reject_waves_per_cycle_below_1_when_the_shop_is_disabled() {
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> new ArenaCycle(0, BREAK_TICKS, false));
  }

  // ---------- errors ----------

  @Test
  void should_reject_wave_cleared_when_in_break_phase() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
    arena.waveCleared();

    org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, arena::waveCleared);
  }

  @Test
  void should_reject_wave_cleared_when_in_shop_phase() {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
    clearWaves(arena, 4);
    arena.waveCleared();

    org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, arena::waveCleared);
  }

  @Test
  void should_reject_waves_per_cycle_below_1() {
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> new ArenaCycle(0, BREAK_TICKS));
  }

  @Test
  void should_reject_break_ticks_below_1() {
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> new ArenaCycle(WAVES_PER_CYCLE, 0));
  }

  // ---------- score ----------

  @Property
  void should_score_the_number_of_waves_defeated(@ForAll @IntRange(min = 0, max = 30) int count) {
    ArenaCycle arena = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);

    for (int i = 0; i < count; i++) {
      if (arena.getPhase() == ArenaCycle.Phase.BREAK) {
        for (int tick = 0; tick < BREAK_TICKS; tick++) {
          arena.update();
        }
      }
      if (arena.getPhase() == ArenaCycle.Phase.SHOP) {
        arena.startNextCycle();
      }
      arena.waveCleared();
    }

    org.junit.jupiter.api.Assertions.assertEquals(count, arena.getScore());
  }
}
