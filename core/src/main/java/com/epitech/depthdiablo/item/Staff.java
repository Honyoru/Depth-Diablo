package com.epitech.depthdiablo.item;

/**
 * The staff family: a mage's weapon that casts a spell projectile (GDD 1.3.1). Each real staff
 * extends it and picks its projectile in {@link #createProjectile}, since a spell may need its own
 * data, such as the burn of a fireball; the desktop plays the staff sound for all of them (GDD
 * 4.2.2).
 */
public abstract class Staff extends RangedWeapon {

  /**
   * Creates a staff.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE}
   * @param price its shop price in coins; 0 means free
   * @param attackBonus the flat bonus added to the holder's ATTACK when it builds; 0 or more
   * @param attackSpeed its base attack speed, multiplied into the holder's ATTACK_SPEED; above 0
   *     and finite
   * @param range how far its spell flies, in pixels; above 0 and finite
   * @param projectileSpeed how fast its spell flies, in pixels per second; above 0 and finite
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a value is out of range
   */
  protected Staff(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float attackSpeed,
      float range,
      float projectileSpeed) {
    super(id, nameKey, spriteId, rarity, price, attackBonus, attackSpeed, range, projectileSpeed);
  }
}
