package com.epitech.depthdiablo.item;

/**
 * The sword: the Knight's melee weapon (GDD 1.2, 1.6). Its blade sweeps an arc around the aim.
 * Swords are only built by {@link ItemFactory} from {@code data/items.json} (Q93), so swords of
 * different sizes and rarities are the same class with different data.
 */
public final class Sword extends MeleeWeapon {

  /**
   * Creates a sword with the values of its data entry.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE}
   * @param price its shop price in coins; 0 means free
   * @param attackBonus the flat bonus added to the attack in the damage formula; 0 or more
   * @param range the length of the blade, in pixels; above 0 and finite
   * @param width the width of the blade, in pixels; above 0 and finite
   * @param arcDegrees the angle the blade sweeps, centred on the aim; from 0 to 360
   * @param swingTicks how many ticks one swing lasts; 1 or more
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a value is out of its range (see {@link MeleeWeapon})
   */
  Sword(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float range,
      float width,
      float arcDegrees,
      int swingTicks) {
    super(id, nameKey, spriteId, rarity, price, attackBonus, range, width, arcDegrees, swingTicks);
  }
}
