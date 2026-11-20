package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;

/**
 * Builds the enemies a summoner spawns (GDD 1.3.1: "summon through the EnemyFactory"). The {@link
 * CharacterFactory} is the real one; tests use a fake.
 */
@FunctionalInterface
public interface Summoner {

  /**
   * Builds a summoned enemy: idle, with no target, not yet in the world. The summoner alerts it and
   * adds it to the world.
   *
   * @param characterId the id of its entry in {@code data/characters.json}, e.g. {@code "rat"}
   * @param centre the centre of its body box, in pixels
   * @return a new enemy whose {@link Enemy#isSummoned()} is true
   * @throws IllegalArgumentException if the id is unknown or is not an enemy
   */
  Enemy summon(String characterId, Vector2 centre);
}
