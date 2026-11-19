package com.epitech.depthdiablo.arena;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Arena mode (GDD 1.1): each wave is spawned once, cleared once no enemy is left, then a break. */
class WaveDirectorTest {

  private static final int WAVES_PER_CYCLE = 5;
  private static final int BREAK_TICKS = 3;

  private final ArenaCycle cycle = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS, false);
  private final WavePlan plan = new WavePlan();

  /** Every enemy spawned, in order. */
  private final List<String> spawned = new ArrayList<>();

  /** The ids spawned as a boss. */
  private final List<String> bosses = new ArrayList<>();

  private final WaveDirector director =
      new WaveDirector(
          cycle,
          plan,
          (id, boss) -> {
            spawned.add(id);
            if (boss) {
              bosses.add(id);
            }
          });

  /** Spawns the current wave, kills it all, and runs the break until the next wave. */
  private void playOneWave() {
    director.onTick(0); // spawn
    director.onTick(0); // nobody left: cleared
    while (cycle.getPhase() == ArenaCycle.Phase.BREAK) {
      director.onTick(0);
    }
  }

  @Test
  void should_spawn_wave_1_on_the_first_tick() {
    director.onTick(0);

    assertEquals(plan.enemiesOf(1, false), spawned);
  }

  @Test
  void should_not_spawn_the_same_wave_twice() {
    director.onTick(0);
    int afterSpawn = spawned.size();

    director.onTick(3);
    director.onTick(3);

    assertEquals(afterSpawn, spawned.size());
  }

  @Test
  void should_not_clear_the_wave_on_the_tick_it_spawns() {
    director.onTick(0); // the new enemies are not counted yet

    assertEquals(ArenaCycle.Phase.WAVE, cycle.getPhase());
    assertEquals(0, cycle.getScore());
  }

  @Test
  void should_keep_the_wave_while_an_enemy_is_alive() {
    director.onTick(0);

    director.onTick(1);

    assertEquals(ArenaCycle.Phase.WAVE, cycle.getPhase());
    assertEquals(0, cycle.getScore());
  }

  @Test
  void should_clear_the_wave_once_no_enemy_is_left() {
    director.onTick(0);

    director.onTick(0);

    assertEquals(ArenaCycle.Phase.BREAK, cycle.getPhase());
    assertEquals(1, cycle.getScore());
  }

  @Test
  void should_spawn_nothing_and_clear_nothing_during_the_break() {
    director.onTick(0);
    director.onTick(0);
    int afterWave1 = spawned.size();

    while (cycle.getPhase() == ArenaCycle.Phase.BREAK) {
      director.onTick(0);
      assertEquals(afterWave1, spawned.size());
    }

    assertEquals(1, cycle.getScore());
  }

  @Test
  void should_spawn_wave_2_once_the_break_is_over() {
    playOneWave();
    int afterWave1 = spawned.size();

    director.onTick(0);

    assertEquals(2, cycle.getWaveNumber());
    assertEquals(plan.enemiesOf(2, false), spawned.subList(afterWave1, spawned.size()));
  }

  @Test
  void should_spawn_the_king_as_a_boss_on_wave_5() {
    for (int i = 1; i < WAVES_PER_CYCLE; i++) {
      playOneWave();
    }
    int beforeBoss = spawned.size();

    director.onTick(0);

    assertEquals(plan.enemiesOf(5, true), spawned.subList(beforeBoss, spawned.size()));
    assertEquals(List.of(WavePlan.KING_ID), bosses);
  }

  @Test
  void should_go_on_to_wave_6_after_the_boss_wave() {
    for (int i = 0; i < WAVES_PER_CYCLE; i++) {
      playOneWave();
    }

    director.onTick(0);

    assertEquals(6, cycle.getWaveNumber());
    assertEquals(5, cycle.getScore());
  }

  @Test
  void should_reject_a_negative_number_of_enemies_alive() {
    assertThrows(IllegalArgumentException.class, () -> director.onTick(-1));
  }

  @Test
  void should_reject_a_null_cycle_plan_or_spawner() {
    EnemySpawner spawner = (id, boss) -> {};

    assertThrows(NullPointerException.class, () -> new WaveDirector(null, plan, spawner));
    assertThrows(NullPointerException.class, () -> new WaveDirector(cycle, null, spawner));
    assertThrows(NullPointerException.class, () -> new WaveDirector(cycle, plan, null));
  }
}
