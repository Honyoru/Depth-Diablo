package com.epitech.depthdiablo.level;

import java.util.ArrayList;
import java.util.List;

/**
 * What an Endless floor holds (Q47, Q17): the deeper the floor, the more enemies, of more kinds,
 * with better stats, and one king on the stele. The plan is fixed: the same floor always gives the
 * same enemies.
 */
public final class FloorPlan {

  /** The king of every floor, on the stele's spawn_boss tile. */
  public static final String BOSS_ID = "king";

  /** What each floor after the first adds to an enemy's HP and Attack: 10 % of its base. */
  public static final float STAT_GROWTH_PER_FLOOR = 0.10f;

  /** Enemies each floor after the first adds to the one per enemy tile. */
  public static final int EXTRA_ENEMIES_PER_FLOOR = 2;

  /** Never more enemies than this many per enemy tile. */
  public static final int MAX_ENEMIES_PER_TILE = 2;

  private static final String RAT_ID = "rat";
  private static final String ZOMBIE_ID = "zombie";
  private static final String SKELETON_ID = "skeleton_archer";
  private static final String NEST_ID = "rat_nest";
  private static final int FIRST_ZOMBIE_FLOOR = 2;
  private static final int FIRST_SKELETON_FLOOR = 3;
  private static final int FIRST_NEST_FLOOR = 4;

  /** One rat nest in every this many enemies, from {@link #FIRST_NEST_FLOOR}. */
  private static final int ENEMIES_PER_NEST = 8;

  private FloorPlan() {}

  /**
   * Returns what an enemy's base HP and Attack are multiplied by on a floor (GDD 1.6): 1 on the
   * first floor, then {@link #STAT_GROWTH_PER_FLOOR} more for each floor.
   *
   * @param floor the floor, 1 or more
   * @return 1 + 0.1 x (floor - 1)
   * @throws IllegalArgumentException if {@code floor} is below 1
   */
  public static float statMultiplier(int floor) {
    checkFloor(floor);
    return 1f + STAT_GROWTH_PER_FLOOR * (floor - 1);
  }

  /**
   * Returns the ids of the enemies of a floor, put on its enemy tiles in turn. There is one per
   * tile, plus {@link #EXTRA_ENEMIES_PER_FLOOR} for each floor after the first, up to {@link
   * #MAX_ENEMIES_PER_TILE} per tile. Rats come from floor 1, zombies from floor 2, skeleton archers
   * from floor 3, in turn; from floor 4 every 8th enemy is a rat nest. The king is not in the list.
   *
   * @param floor the floor, 1 or more
   * @param spawnTiles how many enemy tiles the floor's map has, 0 or more
   * @return the enemy ids, empty when the map has no enemy tile
   * @throws IllegalArgumentException if {@code floor} is below 1 or {@code spawnTiles} negative
   */
  public static List<String> enemiesOf(int floor, int spawnTiles) {
    checkFloor(floor);
    if (spawnTiles < 0) {
      throw new IllegalArgumentException("The enemy tiles must be 0 or more, was " + spawnTiles);
    }
    int count =
        Math.min(
            spawnTiles + EXTRA_ENEMIES_PER_FLOOR * (floor - 1), MAX_ENEMIES_PER_TILE * spawnTiles);
    List<String> kinds = new ArrayList<>(List.of(RAT_ID));
    if (floor >= FIRST_ZOMBIE_FLOOR) {
      kinds.add(ZOMBIE_ID);
    }
    if (floor >= FIRST_SKELETON_FLOOR) {
      kinds.add(SKELETON_ID);
    }
    boolean nests = floor >= FIRST_NEST_FLOOR;
    List<String> enemies = new ArrayList<>();
    int next = 0;
    for (int i = 1; i <= count; i++) {
      if (nests && i % ENEMIES_PER_NEST == 0) {
        enemies.add(NEST_ID);
      } else {
        enemies.add(kinds.get(next % kinds.size()));
        next++;
      }
    }
    return List.copyOf(enemies);
  }

  private static void checkFloor(int floor) {
    if (floor < 1) {
      throw new IllegalArgumentException("The floor must be 1 or more, was " + floor);
    }
  }
}
