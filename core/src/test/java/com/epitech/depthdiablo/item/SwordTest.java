package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

/** GDD 1.2, 1.6: the sword is a melee weapon with its placeholder values. */
class SwordTest {

  @Test
  void should_be_a_melee_weapon() {
    assertInstanceOf(MeleeWeapon.class, new Sword());
  }

  @Test
  void should_have_its_placeholder_values() {
    Sword sword = new Sword();

    assertEquals("sword", sword.id());
    assertEquals(Rarity.COMMON, sword.rarity());
    assertEquals(2, sword.attackBonus());
    assertEquals(24f, sword.range());
    assertEquals(8f, sword.width());
    assertEquals(90f, sword.arcDegrees());
    assertEquals(12, sword.swingTicks());
  }
}
