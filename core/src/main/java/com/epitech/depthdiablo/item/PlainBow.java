package com.epitech.depthdiablo.item;

/**
 * A bow with no special effect (GDD 1.3.1): it shoots a plain arrow. Every such bow, from the short
 * bow to the heavy crossbow, is this class with its own values in {@code data/items.json} (Q93):
 * damage, cooldown, range and arrow speed.
 */
public final class PlainBow extends Bow {

  /**
   * Creates a plain bow with the values of its data entry.
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
  PlainBow(
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
