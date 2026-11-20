package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.data.Reloadable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Builds every character of {@code data/characters.json} from its id (Q23, Q129). It reads the
 * values every character shares and places the body, then hands the entry to the factory of its
 * family: {@link PlayerFactory} for a {@link PlayerKind}, {@link EnemyFactory} for an {@link
 * EnemyKind}.
 *
 * <p>F5 (GDD 1.1.2): every character of one id shares one {@link ReloadableStats}, so {@link
 * #reload()} changes the stats of the live characters, and the next ones are built with the new
 * values.
 *
 * <p>It is also the {@link Summoner} of the summoners it builds: what they spawn comes from the
 * same file, with {@code summoned} set.
 *
 * <p>Pattern: Simple Factory, as a facade over one factory per family.
 */
public final class CharacterFactory implements Reloadable, Summoner {
  private final DataRegistry characters;
  private final PlayerFactory players;
  private final EnemyFactory enemies;

  /** One shared holder per character id: every live rat reads its stats through the same one. */
  private final Map<String, ReloadableStats> live = new HashMap<>();

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
   * @return a new character of the class its entry names, reading its stats through the holder of
   *     its id, so a later {@link #reload()} changes them
   * @throws NullPointerException if an argument is null
   * @throws IllegalArgumentException if the id is unknown, a value is missing or invalid, or the
   *     character is not of the given type
   */
  public <T extends Character> T create(String id, Vector2 position, Class<T> type) {
    return build(id, position, type, false);
  }

  /**
   * Creates a summoned enemy of the given id, standing at the given point. The summoner alerts it
   * and adds it to the world.
   *
   * @param characterId the enemy's id, e.g. {@code "rat"}
   * @param centre the centre of its body box, in pixels
   * @return a new enemy whose {@link Enemy#isSummoned()} is true
   * @throws NullPointerException if {@code centre} is null
   * @throws IllegalArgumentException if the id is unknown, a value is missing or invalid, or the
   *     character is not an enemy
   */
  @Override
  public Enemy summon(String characterId, Vector2 centre) {
    return build(characterId, centre, Enemy.class, true);
  }

  private <T extends Character> T build(
      String id, Vector2 position, Class<T> type, boolean summoned) {
    Objects.requireNonNull(position, "position is null");
    Objects.requireNonNull(type, "type is null");
    JsonValue entry = characters.definition(id);
    CharacterSpec spec = CharacterSpecReader.read(id, entry);
    ReloadableStats stats = live.computeIfAbsent(id, key -> new ReloadableStats(spec.stats()));
    CharacterSpec liveSpec = spec.withStats(stats);
    Aabb body =
        new Aabb(
            position.x - spec.width() / 2f,
            position.y - spec.height() / 2f,
            spec.width(),
            spec.height());
    Character character =
        switch (spec.kind()) {
          case PlayerKind kind -> players.create(kind, liveSpec, entry, body);
          case EnemyKind kind -> enemies.create(kind, liveSpec, entry, body, summoned, this);
        };
    if (!type.isInstance(character)) {
      throw new IllegalArgumentException(
          id + " is a " + character.getClass().getSimpleName() + ", not a " + type.getSimpleName());
    }
    return type.cast(character);
  }

  /**
   * Reads {@code data/characters.json} again for F5 (GDD 1.1.2). Every entry is checked first: if
   * one is invalid, the call throws and nothing changes. Then the live characters of every id still
   * in the file get its new stats at once; a character whose id was removed keeps its old stats.
   *
   * @throws IllegalArgumentException if an entry has a missing or invalid value, an unknown class,
   *     no id or the same id as another; the old values stay
   * @throws com.badlogic.gdx.utils.SerializationException if the text is not valid JSON; the old
   *     values stay
   */
  @Override
  public void reload() {
    characters.reload((id, entry) -> CharacterSpecReader.read(id, entry));
    live.forEach((id, holder) -> {
      if (characters.getIds().contains(id)) {
        CharacterSpec spec = CharacterSpecReader.read(id, characters.definition(id));
        holder.replace(spec.stats());
      }
    });
  }
}
