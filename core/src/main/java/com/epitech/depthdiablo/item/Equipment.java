package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.entity.BuildModifier;

/**
 * An item the player equips in a portrait slot: a weapon or an armor (GDD 1.3.1, 2.2).
 *
 * <p>Each equipment is a {@link BuildModifier}: it changes the build of the character who holds it.
 */
public abstract class Equipment extends Item implements BuildModifier {

  /**
   * Creates an equipment.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE} or {@link Rarity#PASSIVE}
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
    if (rarity == Rarity.PASSIVE) {
      throw new IllegalArgumentException("Equipment cannot have the PASSIVE rarity");
    }
  }
}
