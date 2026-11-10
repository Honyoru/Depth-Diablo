package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** GDD 1.3.1: weapons and armor share the Equipment base. */
class EquipmentTest {

  @Test
  void should_keep_its_values_when_created() {
    Equipment equipment =
        new FakeEquipment("sword_iron", "item.sword_iron.name", "sword", Rarity.RARE, 30);

    assertEquals("sword_iron", equipment.id());
    assertEquals("item.sword_iron.name", equipment.nameKey());
    assertEquals("sword", equipment.spriteId());
    assertEquals(Rarity.RARE, equipment.rarity());
    assertEquals(30, equipment.price());
  }

  @Test
  void should_throw_when_the_rarity_is_consumable() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new FakeEquipment(
                "sword_iron", "item.sword_iron.name", "sword", Rarity.CONSUMABLE, 30));
  }

  /** Hand-written fake (Q117): equipment with nothing of its own. */
  private static final class FakeEquipment extends Equipment {
    FakeEquipment(String id, String nameKey, String spriteId, Rarity rarity, int price) {
      super(id, nameKey, spriteId, rarity, price);
    }
  }
}
