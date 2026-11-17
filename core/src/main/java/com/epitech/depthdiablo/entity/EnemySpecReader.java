package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.utils.JsonValue;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class EnemySpecReader {
  private EnemySpecReader() {}

  public static EnemySpec read(String id, JsonValue entry) {
    Objects.requireNonNull(id, "The id of an enemy must not be null");
    Objects.requireNonNull(entry, "The entry of enemy " + id + " must not be null");
    return new EnemySpec(
        id,
        entry.getString("spriteId"),
        readStats(id, entry.get("stats")),
        entry.getFloat("width"),
        entry.getFloat("height"));
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
