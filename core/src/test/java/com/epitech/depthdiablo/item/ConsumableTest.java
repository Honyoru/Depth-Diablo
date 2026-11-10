package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** GDD 1.3.1 and 1.4: every consumable shares the CONSUMABLE rarity. */
class ConsumableTest {

  @Test
  void should_have_the_consumable_rarity_when_created() {
    Consumable consumable =
        new FakeConsumable("potion_small", "item.potion_small.name", "potion", 10);

    Rarity rarity = consumable.rarity();

    assertEquals(Rarity.CONSUMABLE, rarity);
  }

  @Test
  void should_keep_its_values_when_created() {
    Consumable consumable =
        new FakeConsumable("potion_small", "item.potion_small.name", "potion", 10);

    assertEquals("potion_small", consumable.id());
    assertEquals("item.potion_small.name", consumable.nameKey());
    assertEquals("potion", consumable.spriteId());
    assertEquals(10, consumable.price());
  }

  /** Hand-written fake (Q117): a consumable with no effect of its own. */
  private static final class FakeConsumable extends Consumable {
    FakeConsumable(String id, String nameKey, String spriteId, int price) {
      super(id, nameKey, spriteId, price);
    }
  }
}
