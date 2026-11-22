package com.epitech.depthdiablo.desktop.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Plays the musics (GDD 4.3) as {@link MusicMix} says: it starts the intro or the loop of the music
 * asked for, turns the loop on when the intro ends, and sets both streams' volumes every frame.
 * Every file streams from disk; all are opened once, when the game starts.
 */
final class MusicPlayer implements Disposable {
  private final MusicMix mix = new MusicMix();
  private final Map<String, Music> streams = new HashMap<>();
  private Optional<Music> playing = Optional.empty();
  private Optional<Music> fadingOut = Optional.empty();

  /** Opens the intro and the loop of every music; none plays yet. */
  MusicPlayer() {
    for (MusicTrack track : MusicTrack.values()) {
      track.getIntroPath().ifPresent(this::open);
      open(track.getLoopPath());
    }
  }

  /**
   * Asks for a music; the one playing fades out over {@link MusicMix#FADE_SECONDS}, and the one
   * that was already fading out stops. Asking for the music already playing does nothing.
   *
   * @param track the music wanted
   */
  void play(MusicTrack track) {
    if (!mix.request(track)) {
      return;
    }
    fadingOut.ifPresent(Music::stop);
    fadingOut = playing;
    playing = Optional.of(start(track, mix.startsWithIntro()));
  }

  /**
   * Moves the fade on and sets the volumes; stops the old music once it is silent.
   *
   * @param delta the frame time, in seconds
   */
  void update(float delta) {
    mix.advance(delta);
    playing.ifPresent(music -> music.setVolume(mix.getCurrentVolume()));
    fadingOut.ifPresent(music -> music.setVolume(mix.getFadingOutVolume()));
    if (mix.getFadingOut().isEmpty()) {
      fadingOut.ifPresent(Music::stop);
      fadingOut = Optional.empty();
    }
  }

  @Override
  public void dispose() {
    streams.values().forEach(Music::dispose);
  }

  /**
   * Starts the intro of a music, which hands over to its loop when it ends, or its loop at once.
   */
  private Music start(MusicTrack track, boolean withIntro) {
    Music music = streams.get(withIntro ? track.getIntroPath().orElseThrow() : track.getLoopPath());
    music.setLooping(!withIntro);
    music.setOnCompletionListener(withIntro ? ended -> loopAfterIntro(track) : null);
    music.setVolume(mix.getCurrentVolume());
    music.play();
    return music;
  }

  /** The intro of a music ended: its loop takes over, if that music is still the one wanted. */
  private void loopAfterIntro(MusicTrack track) {
    if (mix.getCurrent().equals(Optional.of(track))) {
      playing = Optional.of(start(track, false));
    }
  }

  private void open(String path) {
    streams.put(path, Gdx.audio.newMusic(Gdx.files.internal(path)));
  }
}
