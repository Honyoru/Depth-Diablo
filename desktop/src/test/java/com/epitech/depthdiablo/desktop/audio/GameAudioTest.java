package com.epitech.depthdiablo.desktop.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** GDD 4.2: every effect plays at its own volume times the volume of all effects. */
class GameAudioTest {

  @Test
  void should_play_an_effect_at_its_own_volume_times_the_volume_of_all_effects() {
    assertEquals(0.21f, GameAudio.volumeOf(SoundEffect.ATTACK_STAFF), 0.0001f);
    assertEquals(0.24f, GameAudio.volumeOf(SoundEffect.MENU_MOVE), 0.0001f);
    assertEquals(0.3f, GameAudio.volumeOf(SoundEffect.DAMAGE), 0.0001f);
    assertEquals(0.45f, GameAudio.volumeOf(SoundEffect.ATTACK_BOW), 0.0001f);
  }

  @Test
  void should_never_play_an_effect_above_full_volume() {
    for (SoundEffect effect : SoundEffect.values()) {
      assertTrue(GameAudio.volumeOf(effect) <= 1f, effect.name());
    }
  }
}
