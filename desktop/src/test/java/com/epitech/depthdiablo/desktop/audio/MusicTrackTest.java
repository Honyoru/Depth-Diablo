package com.epitech.depthdiablo.desktop.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.arena.ArenaCycle;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 4.3: each music is an optional intro played once, then a loop. */
class MusicTrackTest {

  @Test
  void should_loop_the_ambient_music_with_no_intro_on_the_menus() {
    assertTrue(MusicTrack.MENU.getIntroPath().isEmpty());
    assertEquals("music/ambient_loop.ogg", MusicTrack.MENU.getLoopPath());
  }

  @Test
  void should_play_the_main_intro_then_the_main_loop() {
    assertEquals(Optional.of("music/main_intro.ogg"), MusicTrack.MAIN.getIntroPath());
    assertEquals("music/main_loop.ogg", MusicTrack.MAIN.getLoopPath());
  }

  @Test
  void should_play_the_boss_intro_then_the_boss_loop() {
    assertEquals(Optional.of("music/boss_intro.ogg"), MusicTrack.BOSS.getIntroPath());
    assertEquals("music/boss_loop.ogg", MusicTrack.BOSS.getLoopPath());
  }

  @Test
  void should_play_the_victory_intro_then_the_victory_loop() {
    assertEquals(Optional.of("music/victory_intro.ogg"), MusicTrack.VICTORY.getIntroPath());
    assertEquals("music/victory_loop.ogg", MusicTrack.VICTORY.getLoopPath());
  }

  @Test
  void should_play_the_main_music_during_the_normal_waves_and_breaks() {
    assertEquals(MusicTrack.MAIN, MusicTrack.forArena(ArenaCycle.Phase.WAVE, false));
    assertEquals(MusicTrack.MAIN, MusicTrack.forArena(ArenaCycle.Phase.BREAK, false));
  }

  @Test
  void should_play_the_boss_music_during_the_boss_wave() {
    assertEquals(MusicTrack.BOSS, MusicTrack.forArena(ArenaCycle.Phase.WAVE, true));
  }

  @Test
  void should_keep_the_victory_music_until_the_next_wave_when_there_is_no_shop() {
    assertEquals(MusicTrack.VICTORY, MusicTrack.forArena(ArenaCycle.Phase.BREAK, true));
  }

  @Test
  void should_follow_a_whole_arena_cycle_with_no_shop() {
    ArenaCycle cycle = new ArenaCycle(2, 1, false); // wave 1, then the boss wave
    assertEquals(MusicTrack.MAIN, musicOf(cycle));

    cycle.waveCleared();
    assertEquals(MusicTrack.MAIN, musicOf(cycle)); // the break before the boss
    cycle.update();
    assertEquals(MusicTrack.BOSS, musicOf(cycle));

    cycle.waveCleared();
    assertEquals(MusicTrack.VICTORY, musicOf(cycle)); // the boss is dead
    cycle.update();
    assertEquals(MusicTrack.MAIN, musicOf(cycle)); // the next wave
  }

  private static MusicTrack musicOf(ArenaCycle cycle) {
    return MusicTrack.forArena(cycle.getPhase(), cycle.isBossWave());
  }

  @Test
  void should_play_the_victory_music_once_the_boss_is_beaten() {
    assertEquals(MusicTrack.VICTORY, MusicTrack.forArena(ArenaCycle.Phase.SHOP, true));
  }
}
