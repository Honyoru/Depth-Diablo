package com.epitech.depthdiablo.arena;

/**
 * Puts one enemy in the world for the {@link WaveDirector}. The desktop implements it with the
 * {@code CharacterFactory} and the spawn tiles of the map, so the arena logic needs no libGDX.
 */
@FunctionalInterface
public interface EnemySpawner {

  /**
   * Spawns one enemy.
   *
   * @param characterId its id in {@code data/characters.json}, e.g. {@code "rat"}
   * @param boss true for the king of a boss wave, which goes on a boss spawn tile
   */
  void spawn(String characterId, boolean boss);
}
