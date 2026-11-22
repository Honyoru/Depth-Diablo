package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.DotSpec;
import com.epitech.depthdiablo.entity.stats.Stat;

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

  /**
   * An {@code "Axe"} entry; same values as a sword.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param rarity its loot tier
   * @param price its shop price in coins
   * @param attackBonus the flat bonus it adds to the attack
   * @param attackSpeed its base attack speed
   * @param range how far it reaches, in pixels
   * @param width the width of its blade, in pixels
   * @param arcDegrees the angle its blade sweeps
   * @param swingTicks how many ticks one swing lasts
   */
  record AxeSpec(
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
   * A {@code "Shield"} entry.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param rarity its loot tier
   * @param price its shop price in coins
   * @param defenseBonus the defense it adds to its holder's
   */
  record ShieldSpec(
      String id, String nameKey, String spriteId, Rarity rarity, int price, int defenseBonus)
      implements ItemSpec {}

  /**
   * A {@code "StatRing"} entry.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param price its shop price in coins
   * @param stat the stat it raises
   * @param amount what it adds to that stat
   */
  record StatRingSpec(
      String id, String nameKey, String spriteId, int price, Stat stat, float amount)
      implements ItemSpec {}

  /**
   * A {@code "LifeStealRing"} entry.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param price its shop price in coins
   * @param fraction the part of the damage healed
   */
  record LifeStealRingSpec(String id, String nameKey, String spriteId, int price, float fraction)
      implements ItemSpec {}

  /**
   * An {@code "ExecutionerRing"} entry.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param price its shop price in coins
   * @param threshold the part of max HP under which a hit target dies
   */
  record ExecutionerRingSpec(String id, String nameKey, String spriteId, int price, float threshold)
      implements ItemSpec {}

  /**
   * A {@code "PlainBow"} entry: a bow that shoots plain arrows.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param rarity its loot tier
   * @param price its shop price in coins
   * @param attackBonus the flat bonus it adds to the attack
   * @param attackSpeed its base attack speed
   * @param range how far it reaches, in pixels
   * @param projectileSpeed how fast its projectile flies, in pixels per second
   * @param projectileSpriteId the picture of its projectiles
   */
  record PlainBowSpec(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float attackSpeed,
      float range,
      float projectileSpeed,
      String projectileSpriteId)
      implements ItemSpec {}

  /**
   * A {@code "FireStaff"} entry: a bow's values and the burn of its fireball.
   *
   * @param id the item's key
   * @param nameKey the translation key of its name
   * @param spriteId its picture
   * @param rarity its loot tier
   * @param price its shop price in coins
   * @param attackBonus the flat bonus it adds to the attack
   * @param attackSpeed its base attack speed
   * @param range how far it reaches, in pixels
   * @param projectileSpeed how fast its projectile flies, in pixels per second
   * @param projectileSpriteId the picture of its projectiles
   * @param burn the burn its fireball leaves
   */
  record FireStaffSpec(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float attackSpeed,
      float range,
      float projectileSpeed,
      String projectileSpriteId,
      DotSpec burn)
      implements ItemSpec {}
}
