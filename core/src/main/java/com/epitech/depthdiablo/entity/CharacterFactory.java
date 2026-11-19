package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.data.DataRegistry;
import java.util.Objects;

/**
 * Builds every character of {@code data/characters.json} from its id (Q23, Q129). It reads the
 * values every character shares and places the body, then hands the entry to the factory of its
 * family: {@link PlayerFactory} for a {@link PlayerKind}, {@link EnemyFactory} for an {@link
 * EnemyKind}. After F5 the next call uses the new values.
 *
 * <p>Pattern: Simple Factory, as a facade over one factory per family.
 */
public final class CharacterFactory {
  private final DataRegistry characters;
  private final PlayerFactory players;
  private final EnemyFactory enemies;

  /**
   * Creates a factory that reads its values from the given registry.
   *
   * @param characters the entries of {@code data/characters.json}
   * @param players builds the characters of a player kind
   * @param enemies builds the characters of an enemy kind
   * @throws NullPointerException if an argument is null
   */
  public CharacterFactory(DataRegistry characters, PlayerFactory players, EnemyFactory enemies) {
    this.characters = Objects.requireNonNull(characters, "characters is null");
    this.players = Objects.requireNonNull(players, "players is null");
    this.enemies = Objects.requireNonNull(enemies, "enemies is null");
  }

  /**
   * Creates the character of the given id, standing at the given point. The caller adds it to the
   * world.
   *
   * @param <T> the type the caller needs
   * @param id the character's id, e.g. {@code "rat"}
   * @param position the centre of its body box, in pixels
   * @param type the type the caller needs, e.g. {@code Player.class}; any supertype works
   * @return a new character of the class its entry names
   * @throws NullPointerException if an argument is null
   * @throws IllegalArgumentException if the id is unknown, a value is missing or invalid, or the
   *     character is not of the given type
   */
  public <T extends Character> T create(String id, Vector2 position, Class<T> type) {
    Objects.requireNonNull(position, "position is null");
    Objects.requireNonNull(type, "type is null");
    JsonValue entry = characters.definition(id);
    CharacterSpec spec = CharacterSpecReader.read(id, entry);
    Aabb body =
        new Aabb(
            position.x - spec.width() / 2f,
            position.y - spec.height() / 2f,
            spec.width(),
            spec.height());
    Character character =
        switch (spec.kind()) {
          case PlayerKind kind -> players.create(kind, spec, entry, body);
          case EnemyKind kind -> enemies.create(kind, spec, entry, body);
        };
    if (!type.isInstance(character)) {
      throw new IllegalArgumentException(
          id + " is a " + character.getClass().getSimpleName() + ", not a " + type.getSimpleName());
    }
    return type.cast(character);
  }
}
