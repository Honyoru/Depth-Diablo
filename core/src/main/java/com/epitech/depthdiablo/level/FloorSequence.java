package com.epitech.depthdiablo.level;

/**
 * Which Tiled map each floor of an Endless run is played on (Q77). The maps come in order: the
 * hand-made Floor1 to Floor6, then the floors written by tools/tiled/generate-floors.js; after the
 * last one the run loops back to the first.
 */
public final class FloorSequence {

  /** The maps {@code Floor1.tmx} to {@code Floor206.tmx}. */
  public static final int MAP_COUNT = 206;

  private FloorSequence() {}

  /**
   * Returns the map of a floor: floors 1 to {@link #MAP_COUNT} have their own map, the next floor
   * is played on the first one again, and so on.
   *
   * @param floor the floor, 1 or more
   * @return its path in the assets, e.g. {@code "Floor1.tmx"}
   * @throws IllegalArgumentException if {@code floor} is below 1
   */
  public static String mapOf(int floor) {
    if (floor < 1) {
      throw new IllegalArgumentException("The floor must be 1 or more, was " + floor);
    }
    return "Floor" + ((floor - 1) % MAP_COUNT + 1) + ".tmx";
  }
}
