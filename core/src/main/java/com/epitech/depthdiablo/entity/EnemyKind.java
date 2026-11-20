package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.utils.JsonValue;

/**
 * The enemy kinds of {@code data/characters.json}, built by the {@link EnemyFactory}. Each kind
 * holds how to build its class: from the values every enemy shares, plus the values of its own
 * family read from its entry ({@code "reach"} for a melee enemy, {@code "summons"} and {@code
 * "maxSummons"} for a summoner). Adding an enemy is one line here.
 */
public enum EnemyKind implements CharacterKind {
  /** Built as a {@link Rat}, with the {@code "reach"} of its bite. */
  RAT((setup, entry, summoner) -> new Rat(setup, entry.getFloat("reach"))),
  /** Built as a {@link Zombie}, with the {@code "reach"} of its hit. */
  ZOMBIE((setup, entry, summoner) -> new Zombie(setup, entry.getFloat("reach"))),
  /** Built as a {@link RatNest}: what it {@code "summons"}, and at most {@code "maxSummons"}. */
  RAT_NEST((setup, entry, summoner) ->
      new RatNest(setup, entry.getString("summons"), entry.getInt("maxSummons"), summoner));

  /** How a kind builds its class. */
  @FunctionalInterface
  interface Constructor {
    Enemy create(EnemySetup setup, JsonValue entry, Summoner summoner);
  }

  private final Constructor constructor;

  EnemyKind(Constructor constructor) {
    this.constructor = constructor;
  }

  /**
   * Builds an enemy of this kind.
   *
   * @throws IllegalArgumentException if a value of its family is missing or invalid
   */
  Enemy create(EnemySetup setup, JsonValue entry, Summoner summoner) {
    return constructor.create(setup, entry, summoner);
  }
}
