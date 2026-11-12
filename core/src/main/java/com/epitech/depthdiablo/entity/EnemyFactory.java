package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.data.DataRegistry;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Reads enemy type from {@code data/enemies.json} (Q23, Q129). Until the Enemy class exists it
 * gives the values as an {@Link EnemySpec};{@code create(id, floor, summoned)} (UML) will build the
 * Enemy from it. After F5 the next call uses the new values.
 *
 * <p>Pattern: Simple Factory.
 */
public final class EnemyFactory {
  private final DataRegistry enemies;

  /**
   * Creates a factory that reads its values from the given registry.
   *
   * @param enemies the entries of {@code data/enemies.json}
   * @throws NullPointerException if {@code enemies} is null
   */
  public EnemyFactory(DataRegistry enemies) {
    this.enemies = Objects.requireNonNull(enemies, "enemies is null");
  }

  /**
   * Returns the base values of an enemy type.
   *
   * @param id the enemy's id ex {@code "rat"}
   * @return its values
   * @throws IllegalArgumentException if the id is unknown or a value is missing or invalid
   */
  public EnemySpec spec(String id) {
    JsonValue entry = enemies.definition(id);
    return new EnemySpec(
        id,
        entry.getString("spriteId"),
        readStats(entry.get("stats")),
        entry.getFloat("width"),
        entry.getFloat("height"));
  }

  /** Reads one value per {@Link Stat} keyed by the stat's name. */
  private static BaseStats readStats(JsonValue stats) {
    if (stats == null) {
      throw new IllegalArgumentException("Missing \"stats\" object");
    }
    Map<Stat, Float> values = new EnumMap<>(Stat.class);
    for (Stat stat : Stat.values()) {
      values.put(stat, stats.getFloat(stat.name()));
    }
    return new BaseStats(values);
  }
}
