package com.epitech.depthdiablo.desktop.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.EnumMap;
import java.util.Map;

/**
 * Plays the sound effects and the musics of the game (GDD 4). Made once by the application and
 * handed to each screen, so a music keeps playing when the screen changes (Q103: no singleton).
 */
public final class GameAudio implements Disposable {
  private final Map<SoundEffect, Sound> sounds = new EnumMap<>(SoundEffect.class);
  private final MusicPlayer music;
  private final SoundOverlapGuard overlapGuard = new SoundOverlapGuard();

  /** Loads every sound effect and opens every music; no music plays yet. */
  public GameAudio() {
    for (SoundEffect effect : SoundEffect.values()) {
      sounds.put(effect, Gdx.audio.newSound(Gdx.files.internal(effect.getPath())));
    }
    music = new MusicPlayer();
  }

  /**
   * Plays a sound effect for a source, unless that source still plays the same effect.
   *
   * @param effect the sound to play
   * @param source what makes it: a character, the menu
   * @throws NullPointerException if the effect or the source is null
   */
  public void play(SoundEffect effect, Object source) {
    if (overlapGuard.tryStart(effect, source, TimeUtils.millis())) {
      sounds.get(effect).play(effect.getVolume());
    }
  }

  /**
   * Asks for a music; the one playing fades into it (GDD 4.3). Asking for the music already playing
   * does nothing, so it never restarts when the screen changes.
   *
   * @param track the music wanted
   */
  public void playMusic(MusicTrack track) {
    music.play(track);
  }

  /**
   * Moves the music fades on; called once per frame by the application.
   *
   * @param delta the frame time, in seconds
   */
  public void updateMusic(float delta) {
    music.update(delta);
  }

  /** Frees every sound and the music; called once, when the application closes. */
  @Override
  public void dispose() {
    sounds.values().forEach(Sound::dispose);
    music.dispose();
  }
}
