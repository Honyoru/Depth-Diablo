package com.epitech.depthdiablo.desktop.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
