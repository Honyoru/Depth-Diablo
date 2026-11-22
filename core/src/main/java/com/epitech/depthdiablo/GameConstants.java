package com.epitech.depthdiablo;

/** Fixed, non-gameplay constants. Tunable gameplay values live in JSON data (Q93). */
public final class GameConstants {
  /** Logic updates per second, independent of rendering (Q42). */
  public static final int TICKS_PER_SECOND = 60;

  /** Width of the virtual screen in pixels, scaled x2 to 1080p (Q60). */
  public static final int VIRTUAL_WIDTH = 960;

  /** Height of the virtual screen in pixels, scaled x2 to 1080p (Q60). */
  public static final int VIRTUAL_HEIGHT = 540;

  /** Edge length of one tile in pixels (Q60). */
  public static final int TILE_SIZE = 32;

  /** How far an Endless enemy sees the player before it engages, in pixels: 10 tiles. */
  public static final float ENEMY_DETECTION_RANGE = (float) 10 * TILE_SIZE;

  private GameConstants() {}
}
