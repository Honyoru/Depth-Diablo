package com.epitech.depthdiablo.item;

/**
 * An item the player equips in a portrait slot: a weapon or an armor (GDD 1.3.1, 2.2).
 *
 * <p>Pattern: it will implement BuildModifier once CharacterBuild exists (UML, items.drawio).
 */
public abstract class Equipment extends Item {

  /**
   * Creates an equipment.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE}
   * @param price its shop price in coins; 0 means free
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a text is blank, the price is negative or the rarity is
   *     CONSUMABLE
   */
  protected Equipment(String id, String nameKey, String spriteId, Rarity rarity, int price) {
    super(id, nameKey, spriteId, rarity, price);
    if (rarity == Rarity.CONSUMABLE) {
      throw new IllegalArgumentException("Equipment cannot have the CONSUMABLE rarity");
    }
  }
}
