package com.epitech.depthdiablo.desktop.audio;

import com.epitech.depthdiablo.arena.ArenaCycle;
import java.util.Objects;
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
   * Returns the music of an Arena moment: the boss music while the boss wave fights, the victory
   * music from the boss's death until the next wave (the shop, or the break when there is no shop),
   * and the main music the rest of the time.
   *
   * @param phase the phase of the arena cycle
   * @param bossWave whether the current wave is the boss wave
   * @return the music to play
   * @throws NullPointerException if {@code phase} is {@code null}
   */
  public static MusicTrack forArena(ArenaCycle.Phase phase, boolean bossWave) {
    return switch (Objects.requireNonNull(phase, "phase is null")) {
      case WAVE -> bossWave ? BOSS : MAIN;
      case SHOP -> VICTORY;
      case BREAK -> bossWave ? VICTORY : MAIN; // the break after a boss beaten with no shop
    };
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
