package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** GDD 1.3.1: weapons and armor share the Equipment base. */
class EquipmentTest {

  @Test
  void should_keep_its_values_when_created() {
    Equipment equipment = anEquipment(Rarity.RARE);

    assertEquals("sword", equipment.getId());
    assertEquals("item.sword.name", equipment.getNameKey());
    assertEquals("items/sword", equipment.getSpriteId());
    assertEquals(Rarity.RARE, equipment.getRarity());
    assertEquals(30, equipment.getPrice());
  }

  @Test
  void should_throw_when_the_rarity_is_consumable() {
    assertThrows(IllegalArgumentException.class, () -> anEquipment(Rarity.CONSUMABLE));
  }

  private static Equipment anEquipment(Rarity rarity) {
    return new Sword(
        "sword", "item.sword.name", "items/sword", rarity, 30, 2, 1f, 24f, 8f, 90f, 12);
  }

  @Test
  void should_throw_when_the_rarity_is_passive() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Sword(
                "sword_iron",
                "item.sword_iron.name",
                "items/sword_iron",
                Rarity.PASSIVE,
                30,
                2,
                1f,
                24f,
                8f,
                90f,
                12));
  }
}
