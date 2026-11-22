package com.epitech.depthdiablo.desktop.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.EnumMap;
import java.util.Map;

/**
 * Plays the sound effects and the one ambient music loop of the game (GDD 4). Made once by the
 * application and handed to each screen, so the loop keeps playing when the screen changes (Q103:
 * no singleton).
 */
public final class GameAudio implements Disposable {
  /** The music loop, streamed from disk rather than loaded whole (GDD 4.3). */
  static final String MUSIC_PATH = "music/ambient_loop.ogg";

  /** Music volume while playing, from 0 to 1. */
  static final float GAME_MUSIC_VOLUME = 0.5f;

  /** Music volume on the menus, lower than in game (GDD 4.3.2). */
  static final float MENU_MUSIC_VOLUME = 0.25f;

  private final Map<SoundEffect, Sound> sounds = new EnumMap<>(SoundEffect.class);
  private final Music music;
  private final SoundOverlapGuard overlapGuard = new SoundOverlapGuard();

  /** Loads every sound effect and the music loop, which starts silent and stopped. */
  public GameAudio() {
    for (SoundEffect effect : SoundEffect.values()) {
      sounds.put(effect, Gdx.audio.newSound(Gdx.files.internal(effect.getPath())));
    }
    music = Gdx.audio.newMusic(Gdx.files.internal(MUSIC_PATH));
    music.setLooping(true);
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
   * Plays the ambient loop, quieter on the menus (GDD 4.3.2). The first call starts it; later calls
   * only change its volume, so the loop never restarts when the screen changes.
   *
   * @param onMenu true on a menu screen, false in game
   */
  public void playMusic(boolean onMenu) {
    music.setVolume(onMenu ? MENU_MUSIC_VOLUME : GAME_MUSIC_VOLUME);
    if (!music.isPlaying()) {
      music.play();
    }
  }

  /** Frees every sound and the music; called once, when the application closes. */
  @Override
  public void dispose() {
    sounds.values().forEach(Sound::dispose);
    music.dispose();
  }
}
