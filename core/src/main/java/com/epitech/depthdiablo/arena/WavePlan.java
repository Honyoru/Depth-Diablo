package com.epitech.depthdiablo.arena;

import java.util.ArrayList;
import java.util.List;

/**
 * Says which enemies a wave holds (GDD 1.1, Arena mode). Pure data, no world: the {@link
 * WaveDirector} hands each id to its {@link EnemySpawner}. A normal wave holds rats, zombies from
 * wave 2, rat nests from wave 3 and skeleton archers from wave 4; every 5th wave is the king and
 * his escort.
 */
public final class WavePlan {

  /** Id of the boss of every 5th wave in {@code data/characters.json}. */
  public static final String KING_ID = "king";

  private static final String RAT_ID = "rat";
  private static final String ZOMBIE_ID = "zombie";
  private static final String NEST_ID = "rat_nest";
  private static final String SKELETON_ID = "skeleton_archer";
  private static final int FIRST_WAVE_RATS = 6;
  private static final int FIRST_ZOMBIE_WAVE = 2;
  private static final int FIRST_SKELETON_WAVE = 4;
  private static final int KING_ESCORT = 6;

  /** One more nest every 2 waves: none in waves 1 and 2, then 1, 1, 2, 2, 3... */
  private static final int WAVES_PER_NEST = 2;

  /** Creates the wave plan of the Arena; it holds no state of its own. */
  public WavePlan() {
    // its fields start with their declared values
  }

  /**
   * Returns the ids of every enemy of a wave, the king first on a boss wave.
   *
   * @param wave the wave number, 1 or more
   * @param bossWave true when {@link ArenaCycle#isBossWave()} is true
   * @return the enemy ids of the wave, never empty
   * @throws IllegalArgumentException if {@code wave} is lower than 1
   */
  public List<String> enemiesOf(int wave, boolean bossWave) {
    if (wave < 1) {
      throw new IllegalArgumentException("A wave number must be 1 or more, was " + wave);
    }
    List<String> enemies = new ArrayList<>();
    if (bossWave) {
      enemies.add(KING_ID);
      addCopies(enemies, RAT_ID, KING_ESCORT);
      return List.copyOf(enemies);
    }
    addCopies(enemies, RAT_ID, FIRST_WAVE_RATS + wave - 1);
    addCopies(enemies, ZOMBIE_ID, Math.max(0, wave - FIRST_ZOMBIE_WAVE + 1));
    addCopies(enemies, SKELETON_ID, Math.max(0, wave - FIRST_SKELETON_WAVE + 1));
    addCopies(enemies, NEST_ID, (wave - 1) / WAVES_PER_NEST);
    return List.copyOf(enemies);
  }

  private static void addCopies(List<String> enemies, String id, int count) {
    for (int i = 0; i < count; i++) {
      enemies.add(id);
    }
  }
}
