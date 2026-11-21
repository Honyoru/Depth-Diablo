package com.epitech.depthdiablo.desktop.audio;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Decides whether a sound may start: one source never plays the same effect twice at once, so a rat
 * hit twice within 313 ms makes one damage sound, not two over each other. Other sources, and other
 * effects of the same source, still play. Holds only the sounds still ringing.
 */
final class SoundOverlapGuard {

  /** When each source's last sound of each effect ends, in milliseconds. */
  private final Map<Playing, Long> endsAt = new HashMap<>();

  /**
   * Tells whether the effect may start now for this source, and if so, books it until it ends.
   *
   * @param effect the sound to start
   * @param source what makes it: a character, the menu
   * @param nowMillis the current time, in milliseconds
   * @return true if the sound should play, false if the same one from the same source still plays
   * @throws NullPointerException if the effect or the source is null
   */
  boolean tryStart(SoundEffect effect, Object source, long nowMillis) {
    Objects.requireNonNull(effect, "effect is null");
    Objects.requireNonNull(source, "source is null");
    endsAt.values().removeIf(end -> end <= nowMillis);
    Playing playing = new Playing(source, effect);
    if (endsAt.containsKey(playing)) {
      return false;
    }
    endsAt.put(playing, nowMillis + effect.getDurationMillis());
    return true;
  }

  /** One source playing one effect. */
  private record Playing(Object source, SoundEffect effect) {}
}
