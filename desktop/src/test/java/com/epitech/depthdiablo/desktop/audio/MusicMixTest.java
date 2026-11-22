package com.epitech.depthdiablo.desktop.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 4.3: musics fade into each other over 1.5 s; each intro plays once per run. */
class MusicMixTest {
  private static final float DELTA = 0.0001f;
  private final MusicMix mix = new MusicMix();

  @Test
  void should_fade_the_first_music_in_from_silence() {
    mix.request(MusicTrack.MAIN);
    assertEquals(0f, mix.getCurrentVolume(), DELTA);

    mix.advance(0.75f);
    assertEquals(MusicMix.GAME_VOLUME / 2f, mix.getCurrentVolume(), DELTA);

    mix.advance(0.75f);
    assertEquals(MusicMix.GAME_VOLUME, mix.getCurrentVolume(), DELTA);
  }

  @Test
  void should_play_the_menu_music_quieter() {
    mix.request(MusicTrack.MENU);
    mix.advance(MusicMix.FADE_SECONDS);

    assertEquals(MusicMix.MENU_VOLUME, mix.getCurrentVolume(), DELTA);
  }

  @Test
  void should_change_nothing_when_the_music_already_plays() {
    mix.request(MusicTrack.MAIN);
    mix.advance(MusicMix.FADE_SECONDS);

    assertFalse(mix.request(MusicTrack.MAIN));
    assertEquals(MusicMix.GAME_VOLUME, mix.getCurrentVolume(), DELTA);
    assertTrue(mix.getFadingOut().isEmpty());
  }

  @Test
  void should_fade_the_old_music_out_while_the_new_one_fades_in() {
    mix.request(MusicTrack.MAIN);
    mix.advance(MusicMix.FADE_SECONDS);

    assertTrue(mix.request(MusicTrack.BOSS));
    mix.advance(0.75f);

    assertEquals(Optional.of(MusicTrack.BOSS), mix.getCurrent());
    assertEquals(Optional.of(MusicTrack.MAIN), mix.getFadingOut());
    assertEquals(MusicMix.GAME_VOLUME / 2f, mix.getCurrentVolume(), DELTA);
    assertEquals(MusicMix.GAME_VOLUME / 2f, mix.getFadingOutVolume(), DELTA);
  }

  @Test
  void should_let_the_old_music_go_once_the_fade_is_over() {
    mix.request(MusicTrack.MAIN);
    mix.advance(MusicMix.FADE_SECONDS);
    mix.request(MusicTrack.BOSS);

    mix.advance(MusicMix.FADE_SECONDS);

    assertTrue(mix.getFadingOut().isEmpty());
    assertEquals(MusicMix.GAME_VOLUME, mix.getCurrentVolume(), DELTA);
  }

  @Test
  void should_fade_out_from_where_it_was_when_switched_again_mid_fade() {
    mix.request(MusicTrack.MAIN);
    mix.advance(MusicMix.FADE_SECONDS);
    mix.request(MusicTrack.BOSS);
    mix.advance(0.5f); // BOSS is at a third of its volume

    mix.request(MusicTrack.VICTORY);

    assertEquals(Optional.of(MusicTrack.BOSS), mix.getFadingOut()); // MAIN is dropped
    assertEquals(MusicMix.GAME_VOLUME / 3f, mix.getFadingOutVolume(), DELTA); // no jump
  }

  @Test
  void should_start_a_music_with_its_intro_only_the_first_time() {
    mix.request(MusicTrack.MAIN);
    assertTrue(mix.startsWithIntro());
    mix.request(MusicTrack.VICTORY);

    mix.request(MusicTrack.MAIN);

    assertFalse(mix.startsWithIntro());
  }

  @Test
  void should_play_the_intros_again_after_the_menus() {
    mix.request(MusicTrack.MAIN);
    mix.request(MusicTrack.MENU);

    mix.request(MusicTrack.MAIN);

    assertTrue(mix.startsWithIntro());
  }

  @Test
  void should_start_the_menu_music_with_its_loop_as_it_has_no_intro() {
    mix.request(MusicTrack.MENU);

    assertFalse(mix.startsWithIntro());
  }
}
