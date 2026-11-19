package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** GDD 1.4: one line of what an item gives, shown when it is selected in the inventory. */
class ItemEffectTest {

  @Test
  void should_keep_its_key_and_its_value() {
    ItemEffect effect = new ItemEffect("bonus.attack", 2f);

    assertEquals("bonus.attack", effect.key());
    assertEquals(2f, effect.value());
  }

  @Test
  void should_throw_when_the_key_is_null() {
    assertThrows(NullPointerException.class, () -> new ItemEffect(null, 2f));
  }

  @Test
  void should_throw_when_the_key_is_blank() {
    assertThrows(IllegalArgumentException.class, () -> new ItemEffect(" ", 2f));
  }
}
