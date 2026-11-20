package com.epitech.depthdiablo.arena;

import java.util.ArrayList;
import java.util.List;

/**
 * Says which enemies a wave holds (GDD 1.1, Arena mode). Pure data, no world: the {@link
 * WaveDirector} hands each id to its {@link EnemySpawner}.
 */
public final class WavePlan {

  /** Id of the boss of every 5th wave in {@code data/characters.json}. */
  public static final String KING_ID = "king";

  private static final String RAT_ID = "rat";
  private static final String ZOMBIE_ID = "zombie";
  private static final String SKELLETON_ID = "skeleton_archer";
  private static final String RAT_NEST_ID = "rat_nest";
  private static final int FIRST_WAVE_NEST = 2;
  private static final int FIRST_WAVE_SKELETON = 4;
  private static final int FIRST_WAVE_RATS = 6;
  private static final int FIRST_ZOMBIE_WAVE = 2;
  private static final int KING_ESCORT = 6;

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
      for (int i = 0; i < KING_ESCORT; i++) {
        enemies.add(RAT_ID);
      }
      return List.copyOf(enemies);
    }
    int rats = FIRST_WAVE_RATS + wave - 1;
    for (int i = 0; i < rats; i++) {
      enemies.add(RAT_ID);
    }
    int zombies = Math.max(0, wave - FIRST_ZOMBIE_WAVE + 1);
    for (int i = 0; i < zombies; i++) {
      enemies.add(ZOMBIE_ID);
    }
    int skeletons = Math.max(0, wave - FIRST_WAVE_SKELETON + 1);
    for (int i = 0; i < skeletons; i++) {
      enemies.add(SKELLETON_ID);
    }
    int nests = Math.max(0, wave - FIRST_WAVE_NEST + 1);
    for (int i = 0; i < nests; i++) {
      enemies.add(RAT_NEST_ID);
    }
    return List.copyOf(enemies);
  }
}
