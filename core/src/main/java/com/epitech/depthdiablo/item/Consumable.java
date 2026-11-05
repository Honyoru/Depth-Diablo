package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.entity.Character;

/**
 * An item used once from the hotbar, such as a potion or a roulette ticket.
 *
 * <p>Pattern: Command. The hotbar calls {@link #use(Character)} without knowing the effect.
 */
public abstract class Consumable extends Item {
  /**
   * Creates a consumable.
   *
   * @param id the item's id in {@code data/items.json}
   */
  protected Consumable(String id) {
    super(id);
  }

  /**
   * Applies the effect.
   *
   * @param user who uses the item
   * @return true if the item was used up and leaves the inventory
   */
  public abstract boolean use(Character user);
}
