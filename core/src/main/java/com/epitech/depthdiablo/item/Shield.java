package com.epitech.depthdiablo.item;

/**
 * A shield worn in the armor slot (GDD 1.4). Shields of different strengths are the same class with
 * different data, built by {@link ItemFactory} from {@code data/items.json} (Q93).
 */
public final class Shield extends Armor {

  Shield(String id, String nameKey, String spriteId, Rarity rarity, int price, int defenseBonus) {
    super(id, nameKey, spriteId, rarity, price, defenseBonus);
  }
}
