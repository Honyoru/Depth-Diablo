package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.input.PlayerInput;
import com.epitech.depthdiablo.item.Armor;
import com.epitech.depthdiablo.item.Item;
import com.epitech.depthdiablo.item.ItemFactory;
import com.epitech.depthdiablo.item.Weapon;
import java.util.Objects;

/**
 * Builds the players of {@code data/characters.json} for the {@link CharacterFactory} (Q23, Q129).
 * On top of the values every character shares, it reads the {@code "weapon"} and the {@code
 * "armor"} they start with, built by the {@link ItemFactory}.
 *
 * <p>Pattern: Simple Factory.
 */
public final class PlayerFactory {
  private final ItemFactory items;
  private final PlayerInput input;
  private final World world;

  /**
   * Creates a factory for the players of the given world.
   *
   * @param items builds the weapon each player starts with
   * @param input what the user asks for; every player it builds reads it
   * @param world the world the players it creates walk in
   * @throws NullPointerException if an argument is null
   */
  public PlayerFactory(ItemFactory items, PlayerInput input, World world) {
    this.items = Objects.requireNonNull(items, "items is null");
    this.input = Objects.requireNonNull(input, "input is null");
    this.world = Objects.requireNonNull(world, "world is null");
  }

  /**
   * Builds a player holding the weapon of its entry. The caller adds it to the world.
   *
   * @param kind the class to build
   * @param spec the values every character shares, read from its entry
   * @param entry its whole entry, for its {@code "weapon"} and its {@code "armor"}
   * @param body its body box, already placed in the world; also its hurtbox
   * @return a new player of the given kind
   * @throws NullPointerException if an argument is null
   * @throws IllegalArgumentException if the weapon or the armor is missing, unknown or not of its
   *     type
   */
  public Player create(PlayerKind kind, CharacterSpec spec, JsonValue entry, Aabb body) {
    Objects.requireNonNull(kind, "kind is null");
    Objects.requireNonNull(spec, "spec is null");
    Objects.requireNonNull(entry, "entry is null");
    Objects.requireNonNull(body, "body is null");
    Weapon weapon = startingItem(spec.id(), entry.getString("weapon"), Weapon.class);
    Armor armor = startingItem(spec.id(), entry.getString("armor"), Armor.class);
    return kind.create(
        body, spec.stats(), weapon, armor, body, spec.invincibilityTicks(), input, world);
  }

  /** Builds the item of the given id, which must be of the given type. */
  private <T extends Item> T startingItem(String playerId, String itemId, Class<T> type) {
    Item item = items.create(itemId);
    if (type.isInstance(item)) {
      return type.cast(item);
    }
    throw new IllegalArgumentException(
        "The starting "
            + type.getSimpleName()
            + " of "
            + playerId
            + " must be a "
            + type.getSimpleName()
            + ", was "
            + itemId);
  }
}
