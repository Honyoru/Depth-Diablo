package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.DotSpec;

/**
 * The values of one entry of {@code data/items.json} (Q93, Q129), typed by its {@code "class"}.
 * {@link ItemSpecReader} reads them and {@link ItemFactory} builds the item; the item's constructor
 * checks the values.
 *
 * <p>Sealed, so the factory's {@code switch} must handle every kind of item.
 */
public sealed interface ItemSpec {
  /**
   * Returns the item's key in the data files.
   *
   * @return the id
   */
  String id();

  /**
   * Returns the translation key of the item's name.
   *
   * @return the name key
   */
  String nameKey();

  /**
   * Returns the picture the renderer draws for the item
   *
   * @return the sprite id
   */
  String spriteId();

  /**
   * Returns the item's shop price.
   *
   * @return the price in coins
   */
  int price();

  /**
   * A {@code "Potion"} entry.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param price its shop price in coins
   * @param healAmount the HP it restores
   */
  record PotionSpec(String id, String nameKey, String spriteId, int price, int healAmount)
      implements ItemSpec {}

  /**
   * A {@code "Sword"} entry.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param rarity its loot tier
   * @param price its shop price in coins
   * @param attackBonus the attack it adds to its holder's
   * @param attackSpeed its base attacks per second
   * @param range how far the blade reaches, in pixels
   * @param width how wide the blade is, in pixels
   * @param arcDegrees the angle the swing covers
   * @param swingTicks how many ticks a swing lasts
   */
  record SwordSpec(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float attackSpeed,
      float range,
      float width,
      float arcDegrees,
      int swingTicks)
      implements ItemSpec {}

  /**
   * A {@code "LeatherArmor"} entry.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param rarity its loot tier
   * @param price its shop price in coins
   * @param defenseBonus the defense it adds to its holder's
   */
  record LeatherArmorSpec(
      String id, String nameKey, String spriteId, Rarity rarity, int price, int defenseBonus)
      implements ItemSpec {}

  /**
   * A {@code "FireRing"} entry.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param price its shop price in coins
   * @param burn the burn each landed hit applies
   */
  record FireRingSpec(String id, String nameKey, String spriteId, int price, DotSpec burn)
      implements ItemSpec {}
}
