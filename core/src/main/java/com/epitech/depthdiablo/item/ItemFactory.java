package com.epitech.depthdiablo.item;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.data.DataRegistry;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Builds items from {@code data/items.json} (Q23, Q129). Each entry names its class ex {@code
 * "class":"Potion"} plus its values. After F5 the next {@link #create(String)} use the new values.
 *
 * <p>Pattern: Simple Factory
 */
public final class ItemFactory {
  /** One builder per {@code "class"} of the data file; a new item type is one more line. */
  private static final Map<String, BiFunction<String, JsonValue, Item>> BUILDERS =
      Map.of(
          "Potion",
          (id, entry) ->
              new Potion(
                  id,
                  entry.getString("nameKey"),
                  entry.getString("spriteId"),
                  entry.getInt("price"),
                  entry.getInt("healAmount")),
          "Sword",
          (id, entry) ->
              new Sword(
                  id,
                  entry.getString("nameKey"),
                  entry.getString("spriteId"),
                  Rarity.valueOf(entry.getString("rarity")),
                  entry.getInt("price"),
                  entry.getInt("attackBonus"),
                  entry.getFloat("range"),
                  entry.getFloat("width"),
                  entry.getFloat("arcDegrees"),
                  entry.getInt("swingTicks")));

  private final DataRegistry items;

  /**
   * Creates a factory thet read its value from the given regitry.
   *
   * @param items the entries of {@code data/items.json}
   * @throws NullPointerException if {@code items} is null
   */
  public ItemFactory(DataRegistry items) {
    this.items = Objects.requireNonNull(items, "The item data registry must not be null");
  }

  /**
   * Builds a new item from its entry.
   *
   * @param id the item's id ex {@code "potion_small"}
   * @return a new item which the entry's values
   * @throws IllegalArgumentException if the id is unknown, the class is unknown or a value is
   *     missing or invalid
   */
  public Item create(String id) {
    JsonValue entry = items.definition(id);
    String type = entry.getString("class");
    BiFunction<String, JsonValue, Item> builder = BUILDERS.get(type);
    if (builder == null) {
      throw new IllegalArgumentException("Unknown item class for " + id + ": " + type);
    }
    return builder.apply(id, entry);
  }
}
