package com.epitech.depthdiablo.item;

/**
 * An item used once from the hotbar, such as a potion (GDD 1.3.1, 1.4).
 *
 * <p>Pattern: Command. Each consumable is one action the player triggers.
 */
public abstract class Consumable extends Item {

  /**
   * Creates a consumable. Every consumable has the {@link Rarity#CONSUMABLE} rarity, so loot tables
   * can pick consumables by that tier.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param price its shop price in coins; 0 means free
   * @throws NullPointerException if a text is {@code null}
   * @throws IllegalArgumentException if a text is blank or the price is negative
   */
  protected Consumable(String id, String nameKey, String spriteId, int price) {
    super(id, nameKey, spriteId, Rarity.CONSUMABLE, price);
  }
}
