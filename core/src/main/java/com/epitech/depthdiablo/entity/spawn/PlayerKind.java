package com.epitech.depthdiablo.entity.spawn;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Assassin;
import com.epitech.depthdiablo.entity.character.Knight;
import com.epitech.depthdiablo.entity.character.Player;
import com.epitech.depthdiablo.entity.stats.StatSource;
import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.input.PlayerInput;
import com.epitech.depthdiablo.item.Armor;
import com.epitech.depthdiablo.item.Weapon;

/**
 * The player kinds (classes, GDD 1.2) of {@code data/characters.json}. Each kind holds the
 * constructor of its class, so adding a class is one line here.
 */
public enum PlayerKind implements CharacterKind {
  /** Built as a {@link Knight}. */
  KNIGHT(Knight::new),
  /** Built as an {@link Assassin}. */
  ASSASSIN(Assassin::new);

  /** The constructor every player class shares. */
  @FunctionalInterface
  interface Constructor {
    Player create(
        Aabb body,
        StatSource stats,
        Weapon startingWeapon,
        Armor startingArmor,
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
      Armor startingArmor,
      Aabb hurtbox,
      int invincibilityTicks,
      PlayerInput input,
      World world) {
    return constructor.create(
        body, stats, startingWeapon, startingArmor, hurtbox, invincibilityTicks, input, world);
  }
}
