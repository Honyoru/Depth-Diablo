package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.input.PlayerInput;
import com.epitech.depthdiablo.item.Weapon;

/**
 * The player kinds (classes, GDD 1.2) of {@code data/characters.json}. Each kind holds the
 * constructor of its class, so adding a class is one line here.
 */
public enum PlayerKind implements CharacterKind {
  /** Built as a {@link Knight}. */
  KNIGHT(Knight::new);

  /** The constructor every player class shares. */
  @FunctionalInterface
  interface Constructor {
    Player create(
        Aabb body,
        StatSource stats,
        Weapon startingWeapon,
        Aabb hurtbox,
        int invincibilityTicks,
        PlayerInput input,
        World world);
  }

  private final Constructor constructor;

  PlayerKind(Constructor constructor) {
    this.constructor = constructor;
  }

  /** Builds a player of this kind with the constructor of its class. */
  Player create(
      Aabb body,
      StatSource stats,
      Weapon startingWeapon,
      Aabb hurtbox,
      int invincibilityTicks,
      PlayerInput input,
      World world) {
    return constructor.create(
        body, stats, startingWeapon, hurtbox, invincibilityTicks, input, world);
  }
}
