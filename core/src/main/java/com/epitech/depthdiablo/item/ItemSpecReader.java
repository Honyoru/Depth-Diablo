package com.epitech.depthdiablo.item;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.combat.BurnSpec;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public final class ItemSpecReader {
  private ItemSpecReader() {}

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
          "FireRing",
          (id, json) ->
              new ItemSpec.FireRingSpec(
                  id,
                  json.getString("nameKey"),
                  json.getString("spriteId"),
                  json.getInt("price"),
                  readBurn(id, json.get("burn"))));

  private static BurnSpec readBurn(String id, JsonValue burn) {
    if (burn == null) {
      throw new IllegalArgumentException("Item " + id + " has no \"burn\" object in the data file");
    }
    return new BurnSpec(
        burn.getInt("damage"), burn.getInt("intervalTicks"), burn.getInt("durationTicks"));
  }

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
