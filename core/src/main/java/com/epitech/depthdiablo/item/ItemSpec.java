package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.BurnSpec;

public sealed interface ItemSpec {
  String id();

  String nameKey();

  String spriteId();

  int price();

  record PotionSpec(String id, String nameKey, String spriteId, int price, int healAmount)
      implements ItemSpec {}

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

  record FireRingSpec(String id, String nameKey, String spriteId, int price, BurnSpec burn)
      implements ItemSpec {}
}
