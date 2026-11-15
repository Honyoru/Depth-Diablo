package com.epitech.depthdiablo.item;

import java.util.Objects;

/**
 * Base type of everything the player can carry: weapons, armor, consumables and passive items (GDD
 * 1.3.1). Values come from {@code data/items.json} (Q93).
 */
public abstract class Item {
  private final String id;
  private final String nameKey;
  private final String spriteId;
  private final Rarity rarity;
  private final int price;

  /**
   * Creates an item with the values every item shares.
   *
   * @param id the item's key in the data files, e.g. {@code "potion_small"}
   * @param nameKey the translation key of its name, e.g. {@code "item.potion_small.name"}
   * @param spriteId the picture the renderer draws for it
   * @param rarity how rare it is; loot tables use it to choose what drops
   * @param price its shop price in coins; 0 means free
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a text is blank or the price is negative
   */
  protected Item(String id, String nameKey, String spriteId, Rarity rarity, int price) {
    this.id = requireText(id, "id");
    this.nameKey = requireText(nameKey, "nameKey");
    this.spriteId = requireText(spriteId, "spriteId");
    this.rarity = Objects.requireNonNull(rarity, "The rarity of item " + id + " must not be null");
    if (price < 0) {
      throw new IllegalArgumentException(
          "The price of item " + id + " must not be negative, was " + price);
    }
    this.price = price;
  }

  private static String requireText(String value, String name) {
    Objects.requireNonNull(value, "The " + name + " of an item must not be null");
    if (value.isBlank()) {
      throw new IllegalArgumentException(
          "The " + name + " of an item must not be blank, was \"" + value + "\"");
    }
    return value;
  }

  /**
   * Returns the item's key in the data files.
   *
   * @return the id
   */
  public final String id() {
    return id;
  }

  /**
   * Returns the translation key of the item's name.
   *
   * @return the name key
   */
  public final String nameKey() {
    return nameKey;
  }

  /**
   * Returns the picture the renderer draws for this item.
   *
   * @return the sprite id
   */
  public final String spriteId() {
    return spriteId;
  }

  /**
   * Returns how rare this item is.
   *
   * @return the rarity
   */
  public final Rarity rarity() {
    return rarity;
  }

  /**
   * Returns the shop price of this item.
   *
   * @return the price in coins, 0 or more
   */
  public final int price() {
    return price;
  }
}
