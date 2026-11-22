package com.epitech.depthdiablo.item;

/**
 * The short bow: an Archer's first bow (GDD 1.3.1). It shoots an arrow like every bow; its values
 * come from {@code data/items.json} (Q93), so other bows of this kind are the same class with other
 * data.
 */
public final class ShortBow extends Bow {

  /**
   * Creates a short bow with the values of its data entry.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE}
   * @param price its shop price in coins; 0 means free
   * @param attackBonus the flat bonus added to the holder's ATTACK when it builds; 0 or more
   * @param attackSpeed its base attack speed, multiplied into the holder's ATTACK_SPEED; above 0
   *     and finite
   * @param range how far its arrow flies, in pixels; above 0 and finite
   * @param projectileSpeed how fast its arrow flies, in pixels per second; above 0 and finite
   * @param projectileSpriteId the picture of its arrow, pointing right; not blank
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a value is out of range
   */
  ShortBow(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float attackSpeed,
      float range,
      float projectileSpeed,
      String projectileSpriteId) {
    super(
        id,
        nameKey,
        spriteId,
        rarity,
        price,
        attackBonus,
        attackSpeed,
        range,
        projectileSpeed,
        projectileSpriteId);
  }
}
