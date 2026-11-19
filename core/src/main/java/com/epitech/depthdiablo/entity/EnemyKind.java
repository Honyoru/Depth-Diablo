package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.engine.World;

/**
 * The enemy kinds of {@code data/characters.json}, built by the {@link EnemyFactory}. Each kind
 * holds the constructor of its class, so adding an enemy is one line here. They are all melee
 * enemies for now: a family whose constructor differs (ranged, boss) gets its own kind enum.
 */
public enum EnemyKind implements CharacterKind {
  /** Built as a {@link Rat}. */
  RAT(Rat::new),
  /** Built as a {@link Zombie}. */
  ZOMBIE(Zombie::new);

  /** The constructor every melee enemy class shares. */
  @FunctionalInterface
  interface Constructor {
    Enemy create(
        Aabb body,
        StatSource stats,
        float reach,
        Aabb hurtbox,
        int invincibilityTicks,
        boolean summoned,
        World world);
  }

  private final Constructor constructor;

  EnemyKind(Constructor constructor) {
    this.constructor = constructor;
  }

  /** Builds an enemy of this kind with the constructor of its class. */
  Enemy create(
      Aabb body,
      StatSource stats,
      float reach,
      Aabb hurtbox,
      int invincibilityTicks,
      boolean summoned,
      World world) {
    return constructor.create(body, stats, reach, hurtbox, invincibilityTicks, summoned, world);
  }
}
