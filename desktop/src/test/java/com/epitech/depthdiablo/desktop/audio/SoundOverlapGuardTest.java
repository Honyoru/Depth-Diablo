package com.epitech.depthdiablo.desktop.audio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** GDD 4.2: one source never plays the same sound twice at once. */
class SoundOverlapGuardTest {
  private final SoundOverlapGuard guard = new SoundOverlapGuard();
  private final Object rat = new Object();

  @Test
  void should_block_the_same_effect_from_the_same_source_while_it_still_plays() {
    guard.tryStart(SoundEffect.DAMAGE, rat, 0);

    assertFalse(guard.tryStart(SoundEffect.DAMAGE, rat, 100));
  }

  @Test
  void should_play_again_once_the_last_one_has_ended() {
    guard.tryStart(SoundEffect.DAMAGE, rat, 0);

    assertTrue(guard.tryStart(SoundEffect.DAMAGE, rat, 313));
  }

  @Test
  void should_play_the_same_effect_from_another_source() {
    guard.tryStart(SoundEffect.DAMAGE, rat, 0);

    assertTrue(guard.tryStart(SoundEffect.DAMAGE, new Object(), 100));
  }

  @Test
  void should_play_another_effect_from_the_same_source() {
    guard.tryStart(SoundEffect.DAMAGE, rat, 0);

    assertTrue(guard.tryStart(SoundEffect.DEATH, rat, 100));
  }

  @Test
  void should_throw_when_the_source_is_null() {
    assertThrows(NullPointerException.class, () -> guard.tryStart(SoundEffect.DAMAGE, null, 0));
  }
}
