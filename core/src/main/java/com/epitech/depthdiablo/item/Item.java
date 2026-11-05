package com.epitech.depthdiablo.item;

import java.util.Objects;

/** Base type of every item (GDD 1.4): weapons, armor, consumables and passive items. */
public abstract class Item {
  private final String id;

  /**
   * Creates an item.
   *
   * @param id the item's id in {@code data/items.json}
   */
  protected Item(String id) {
    this.id = Objects.requireNonNull(id, "id");
  }

  /**
   * Returns the item's data id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }
}
