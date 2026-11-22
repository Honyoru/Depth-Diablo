package com.epitech.depthdiablo.desktop.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import org.junit.jupiter.api.Test;

/** GDD 4.2: each sound effect plays at its own volume; the loud ones are turned down. */
class SoundEffectTest {

  @Test
  void should_play_the_staff_quieter_than_the_other_attacks() {
    assertEquals(0.7f, SoundEffect.ATTACK_STAFF.getVolume());
  }

  @Test
  void should_play_the_menu_sounds_a_bit_quieter() {
    assertEquals(0.8f, SoundEffect.MENU_MOVE.getVolume());
    assertEquals(0.8f, SoundEffect.MENU_CONFIRM.getVolume());
  }

  @Test
  void should_play_every_other_effect_at_full_volume() {
    EnumSet<SoundEffect> others =
        EnumSet.complementOf(
            EnumSet.of(
                SoundEffect.ATTACK_STAFF,
                SoundEffect.ATTACK_BOW,
                SoundEffect.MENU_MOVE,
                SoundEffect.MENU_CONFIRM));

    others.forEach(effect -> assertEquals(1f, effect.getVolume(), effect.name()));
  }

  @Test
  void should_play_the_bow_louder_than_the_other_attacks() {
    assertEquals(1.5f, SoundEffect.ATTACK_BOW.getVolume());
  }

  @Test
  void should_keep_every_volume_above_0() {
    for (SoundEffect effect : SoundEffect.values()) {
      assertTrue(effect.getVolume() > 0f, effect.name());
    }
  }
}
