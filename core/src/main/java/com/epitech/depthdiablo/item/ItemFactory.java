package com.epitech.depthdiablo.item;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.data.DataRegistry;
import java.util.Objects;

/**
 * Builds items from {@code data/items.json} (Q23, Q129).
 * Each entry names its class ex {@code "class":"Potion"} plus its values. After
 * F5 the next {@Link #create(string)} use the new values.
 *
 * <p>
 * Pattern: Simple Factory
 */

public final class ItemFactory {
  private final DataRegistry items;

  /**
   * Creates a factory thet read its value from the given regitry.
   *
   * @param items the entries of {@code data/items.json}
   * @throws NullPointerException if {@code items} is null
   */
  public ItemFactory(DataRegistry items) {
    this.items = Objects.requireNonNull(items, "items");
  }

  /**
   * Builds a new item from its entry.
   *
   * @param id the item's id ex {@code "potion_small"}
   * @return a new item wich the entry's alues
   * @throws IllegalArgumentException if the id is unknown, the class is unknown
   *                                  or a value is missing or invalid
   */
  public Item create(String id) {
    JsonValue entry = items.definition(id);
    String type = entry.getString("class");
    return switch (type) {
      case "potion" ->
        new Potion(
            id,
            entry.getString("nameKey"), entry.getString("spriteId"),
            entry.getInt("price"), entry.getInt("healAmount"));
      default -> throw new IllegalArgumentException("Unknown item class for " + id + ": " + type);

    };
  }
}
