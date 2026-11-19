package com.epitech.depthdiablo.item;

/**
 * A leather armor (GDD 1.4). It has no code of its own: leather armors of different strengths and
 * rarities are the same class with different data, so they are only built by {@link ItemFactory}
 * from {@code data/items.json} (Q93).
 */
public final class LeatherArmor extends Armor {

  /**
   * Creates a leather armor.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE} or {@link Rarity#PASSIVE}
   * @param price its shop price in coins; 0 means free
   * @param defenseBonus the defense it adds to its holder's; 0 or more
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a value of {@link Armor} is wrong
   */
  LeatherArmor(
      String id, String nameKey, String spriteId, Rarity rarity, int price, int defenseBonus) {
    super(id, nameKey, spriteId, rarity, price, defenseBonus);
  }
}
