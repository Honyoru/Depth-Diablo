package com.epitech.depthdiablo.item;

/**
 * The axe: a slow, heavy melee weapon whose blade sweeps a wide arc (GDD 1.6). Axes are only built
 * by {@link ItemFactory} from {@code data/items.json} (Q93).
 */
public final class Axe extends MeleeWeapon {

  Axe(
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
      int swingTicks) {
    super(
        id,
        nameKey,
        spriteId,
        rarity,
        price,
        attackBonus,
        attackSpeed,
        range,
        width,
        arcDegrees,
        swingTicks);
  }
}
