package com.epitech.depthdiablo.item;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.combat.DotSpec;
import com.epitech.depthdiablo.entity.Stat;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Reads one entry of {@code data/items.json} into an {@link ItemSpec} (Q93, Q129). The entry comes
 * from the {@link com.epitech.depthdiablo.data.DataRegistry}, which parses the file; this is the
 * only class of the item package that reads JSON fields.
 */
public final class ItemSpecReader {
  private ItemSpecReader() {}

  /** One reader per {@code "class"} of the data file; a new item kind is one more entry. */
  private static final Map<String, BiFunction<String, JsonValue, ItemSpec>> READERS =
      Map.of(
          "Potion",
          (id, json) ->
              new ItemSpec.PotionSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  json.getInt("price"),
                  json.getInt("healAmount")),
          "Sword",
          (id, json) ->
              new ItemSpec.SwordSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  Rarity.valueOf(json.getString("rarity")),
                  json.getInt("price"),
                  json.getInt("attackBonus"),
                  json.getFloat("attackSpeed"),
                  json.getFloat("range"),
                  json.getFloat("width"),
                  json.getFloat("arcDegrees"),
                  json.getInt("swingTicks")),
          "LeatherArmor",
          (id, json) ->
              new ItemSpec.LeatherArmorSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  Rarity.valueOf(json.getString("rarity")),
                  json.getInt("price"),
                  json.getInt("defenseBonus")),
          "FireRing",
          (id, json) ->
              new ItemSpec.FireRingSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  json.getInt("price"),
                  readBurn(id, json.get("burn"))),
          "ShortBow",
          (id, json) ->
              new ItemSpec.ShortBowSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  Rarity.valueOf(json.getString("rarity")),
                  json.getInt("price"),
                  json.getInt("attackBonus"),
                  json.getFloat("attackSpeed"),
                  json.getFloat("range"),
                  json.getFloat("projectileSpeed")),
          "Axe",
          (id, json) ->
              new ItemSpec.AxeSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  Rarity.valueOf(json.getString("rarity")),
                  json.getInt("price"),
                  json.getInt("attackBonus"),
                  json.getFloat("attackSpeed"),
                  json.getFloat("range"),
                  json.getFloat("width"),
                  json.getFloat("arcDegrees"),
                  json.getInt("swingTicks")),
          "Shield",
          (id, json) ->
              new ItemSpec.ShieldSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  Rarity.valueOf(json.getString("rarity")),
                  json.getInt("price"),
                  json.getInt("defenseBonus")),
          "StatRing",
          (id, json) ->
              new ItemSpec.StatRingSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  json.getInt("price"),
                  Stat.valueOf(json.getString("stat")),
                  json.getFloat("amount")),
          "LifeStealRing",
          (id, json) ->
              new ItemSpec.LifeStealRingSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  json.getInt("price"),
                  json.getFloat("fraction")),
          "ExecutionerRing",
          (id, json) ->
              new ItemSpec.ExecutionerRingSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  json.getInt("price"),
                  json.getFloat("threshold")));

  /** Reads the {@code "burn"} object of a fire ring entry. */
  private static DotSpec readBurn(String id, JsonValue burn) {
    if (burn == null) {
      throw new IllegalArgumentException("Item " + id + " has no \"burn\" object in the data file");
    }
    return new DotSpec(
        burn.getInt("damage"), burn.getInt("intervalTicks"), burn.getInt("durationTicks"));
  }

  /**
   * Reads an entry into the spec of its {@code "class"}.
   *
   * @param id the item's id, e.g. {@code "fire_ring"}
   * @param entry its entry in the data file
   * @return the typed values of the entry
   * @throws NullPointerException if an argument is {@code null}
   * @throws IllegalArgumentException if the class is missing or unknown, or a value is missing or
   *     invalid
   */
  public static ItemSpec read(String id, JsonValue entry) {
    Objects.requireNonNull(id, "The item id must not be null");
    Objects.requireNonNull(entry, "The item entry must not be null");

    String type = entry.getString("class");
    BiFunction<String, JsonValue, ItemSpec> reader = READERS.get(type);
    if (reader == null) {
      throw new IllegalArgumentException("Unknown item class for " + id + ": " + type);
    }
    return reader.apply(id, entry);
  }
}
