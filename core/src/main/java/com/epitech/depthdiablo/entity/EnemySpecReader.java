package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.utils.JsonValue;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Reads one entry of {@code data/enemies.json} into an {@link EnemySpec} (Q23, Q129). The entry
 * comes from the {@link com.epitech.depthdiablo.data.DataRegistry}, which parses the file.
 */
public final class EnemySpecReader {
  private EnemySpecReader() {}

  /**
   * Reads an entry into the values of its enemy type.
   *
   * @param id the enemy's id, e.g. {@code "rat"}
   * @param entry its entry in the data file
   * @return its values
   * @throws NullPointerException if an argument is {@code null}
   * @throws IllegalArgumentException if a value is missing or invalid
   */
  public static EnemySpec read(String id, JsonValue entry) {
    Objects.requireNonNull(id, "The id of an enemy must not be null");
    Objects.requireNonNull(entry, "The entry of enemy " + id + " must not be null");
    return new EnemySpec(
        id,
        EnemyKind.valueOf(entry.getString("class")),
        entry.getString("spriteId"),
        readStats(id, entry.get("stats")),
        entry.getFloat("width"),
        entry.getFloat("height"),
        entry.getFloat("reach"),
        entry.getInt("invincibilityTicks"));
  }

  /** Reads one value per {@link Stat} keyed by the stat's name. */
  private static BaseStats readStats(String id, JsonValue stats) {
    if (stats == null) {
      throw new IllegalArgumentException(
          "Enemy " + id + " has no \"stats\" object in the data file");
    }
    Map<Stat, Float> values = new EnumMap<>(Stat.class);
    for (Stat stat : Stat.values()) {
      values.put(stat, stats.getFloat(stat.name()));
    }
    return new BaseStats(values);
  }
}
