package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.utils.JsonValue;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Reads one entry of {@code data/characters.json} into a {@link CharacterSpec} (Q23, Q129). The
 * entry comes from the {@link com.epitech.depthdiablo.data.DataRegistry}, which parses the file.
 */
public final class CharacterSpecReader {
  private CharacterSpecReader() {}

  /**
   * Reads an entry into the values of its character type.
   *
   * @param id the character's id, e.g. {@code "rat"}
   * @param entry its entry in the data file
   * @return its values
   * @throws NullPointerException if an argument is {@code null}
   * @throws IllegalArgumentException if a value is missing or invalid
   */
  public static CharacterSpec read(String id, JsonValue entry) {
    Objects.requireNonNull(id, "id is null");
    Objects.requireNonNull(entry, "entry of " + id + " is null");
    return new CharacterSpec(
        id,
        CharacterKind.valueOf(entry.getString("class")),
        entry.getString("spriteId"),
        readStats(id, entry.get("stats")),
        entry.getFloat("width"),
        entry.getFloat("height"),
        entry.getInt("invincibilityTicks"));
  }

  /** Reads one value per {@link Stat} keyed by the stat's name. */
  private static BaseStats readStats(String id, JsonValue stats) {
    if (stats == null) {
      throw new IllegalArgumentException(
          "Character " + id + " has no \"stats\" object in the data file");
    }
    Map<Stat, Float> values = new EnumMap<>(Stat.class);
    for (Stat stat : Stat.values()) {
      values.put(stat, stats.getFloat(stat.name()));
    }
    return new BaseStats(values);
  }
}
