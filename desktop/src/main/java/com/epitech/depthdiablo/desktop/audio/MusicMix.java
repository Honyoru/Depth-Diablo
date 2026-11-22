package com.epitech.depthdiablo.desktop.audio;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Which music plays and how loud (GDD 4.3): a new music fades in over {@link #FADE_SECONDS} while
 * the old one fades out from its current volume, and each intro plays once per run. Pure logic: the
 * {@code MusicPlayer} applies it to the real streams every frame.
 */
final class MusicMix {
  /** How long two musics blend when one takes over, in seconds. */
  static final float FADE_SECONDS = 1.5f;

  /** Music volume on the menus, lower than in game (GDD 4.3.2). */
  static final float MENU_VOLUME = 0.25f;

  /** Music volume in game. */
  static final float GAME_VOLUME = 0.5f;

  private Optional<MusicTrack> current = Optional.empty();
  private Optional<MusicTrack> fadingOut = Optional.empty();
  private float fadingOutFrom;
  private float fadeElapsed;
  private boolean startsWithIntro;
  private final Set<MusicTrack> introsPlayed = EnumSet.noneOf(MusicTrack.class);

  /**
   * Returns the music that plays, or fades in.
   *
   * @return it, or nothing before the first request
   */
  Optional<MusicTrack> getCurrent() {
    return current;
  }

  /**
   * Returns the music that fades out, until its fade is over.
   *
   * @return it, or nothing when no fade runs
   */
  Optional<MusicTrack> getFadingOut() {
    return fadingOut;
  }

  /**
   * Returns how loud the current music plays now.
   *
   * @return its volume, from 0 to its full volume
   */
  float getCurrentVolume() {
    return current.map(track -> fullVolumeOf(track) * progress()).orElse(0f);
  }

  /**
   * Returns how loud the music that fades out plays now.
   *
   * @return its volume, down to 0 at the end of the fade
   */
  float getFadingOutVolume() {
    return fadingOut.isPresent() ? fadingOutFrom * (1f - progress()) : 0f;
  }

  /**
   * Tells whether the current music must start with its intro: the first time in a run.
   *
   * @return true to start its intro, false to start its loop at once
   */
  boolean startsWithIntro() {
    return startsWithIntro;
  }

  /**
   * Asks for a music. The one that played starts fading out; the one that was already fading out is
   * dropped. Asking for the menu music starts a new run: the intros will play again.
   *
   * @param track the music wanted
   * @return true if the music changes, false if it already plays
   * @throws NullPointerException if {@code track} is {@code null}
   */
  boolean request(MusicTrack track) {
    Objects.requireNonNull(track, "track is null");
    if (current.equals(Optional.of(track))) {
      return false;
    }
    fadingOutFrom = getCurrentVolume();
    fadingOut = current;
    current = Optional.of(track);
    fadeElapsed = 0f;
    if (track == MusicTrack.MENU) {
      introsPlayed.clear();
    }
    startsWithIntro = track.getIntroPath().isPresent() && introsPlayed.add(track);
    return true;
  }

  /**
   * Moves the fade on; once it is over, the old music is let go.
   *
   * @param seconds the frame time
   */
  void advance(float seconds) {
    fadeElapsed = Math.min(FADE_SECONDS, fadeElapsed + seconds);
    if (fadeElapsed >= FADE_SECONDS) {
      fadingOut = Optional.empty();
    }
  }

  /** How far the fade went, from 0 to 1. */
  private float progress() {
    return fadeElapsed / FADE_SECONDS;
  }

  private static float fullVolumeOf(MusicTrack track) {
    return track == MusicTrack.MENU ? MENU_VOLUME : GAME_VOLUME;
  }
}
