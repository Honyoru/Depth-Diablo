package com.epitech.depthdiablo.desktop.audio;

import java.util.Optional;

/**
 * The musics of the game (GDD 4.3), each streamed from {@code music/<file>.ogg}: an intro played
 * once, then a loop that repeats until another music takes over. The menu music has no intro.
 */
public enum MusicTrack {
  /** The title and the other menus: the ambient loop, with no intro. */
  MENU("ambient_loop"),
  /** The waves, between two bosses. */
  MAIN("main_intro", "main_loop"),
  /** The boss wave. */
  BOSS("boss_intro", "boss_loop"),
  /** After the boss is beaten, until the next cycle. */
  VICTORY("victory_intro", "victory_loop");

  /** What {@link #introPath} holds for a music that starts with its loop. */
  private static final String NO_INTRO = "";

  private final String introPath;
  private final String loopPath;

  MusicTrack(String loop) {
    this.introPath = NO_INTRO;
    this.loopPath = pathOf(loop);
  }

  MusicTrack(String intro, String loop) {
    this.introPath = pathOf(intro);
    this.loopPath = pathOf(loop);
  }

  /**
   * Returns the part played once when the music starts.
   *
   * @return the internal path of its intro, or nothing if it starts with its loop
   */
  public Optional<String> getIntroPath() {
    return introPath.isEmpty() ? Optional.empty() : Optional.of(introPath);
  }

  /**
   * Returns the part that repeats after the intro.
   *
   * @return the internal path of its loop
   */
  public String getLoopPath() {
    return loopPath;
  }

  private static String pathOf(String file) {
    return "music/" + file + ".ogg";
  }
}
