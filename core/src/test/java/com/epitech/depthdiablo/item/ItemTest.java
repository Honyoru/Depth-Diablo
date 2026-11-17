package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** GDD 1.3.1: the values every item shares, checked when the item is created. */
class ItemTest {

  @Test
  void should_keep_its_values_when_created() {
    Item item = new FakeItem("potion_small", "item.potion_small.name", "potion", Rarity.RARE, 10);

    assertEquals("potion_small", item.getId());
    assertEquals("item.potion_small.name", item.getNameKey());
    assertEquals("potion", item.getSpriteId());
    assertEquals(Rarity.RARE, item.getRarity());
    assertEquals(10, item.getPrice());
  }

  @Test
  void should_throw_when_the_id_is_null() {
    assertThrows(
        NullPointerException.class, () -> new FakeItem(null, "item.x.name", "x", Rarity.COMMON, 1));
  }

  @Test
  void should_throw_when_the_id_is_blank() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new FakeItem("  ", "item.x.name", "x", Rarity.COMMON, 1));
  }

  @Test
  void should_throw_when_the_name_key_is_blank() {
    assertThrows(
        IllegalArgumentException.class, () -> new FakeItem("x", "", "x", Rarity.COMMON, 1));
  }

  @Test
  void should_throw_when_the_sprite_id_is_blank() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new FakeItem("x", "item.x.name", "", Rarity.COMMON, 1));
  }

  @Test
  void should_throw_when_the_rarity_is_null() {
    assertThrows(NullPointerException.class, () -> new FakeItem("x", "item.x.name", "x", null, 1));
  }

  @Test
  void should_throw_when_the_price_is_negative() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new FakeItem("x", "item.x.name", "x", Rarity.COMMON, -1));
  }

  @Test
  void should_accept_a_price_of_0() {
    Item item = new FakeItem("x", "item.x.name", "x", Rarity.COMMON, 0);

    assertEquals(0, item.getPrice());
  }

  /** Hand-written fake (Q117): an item with nothing of its own. */
  private static final class FakeItem extends Item {
    FakeItem(String id, String nameKey, String spriteId, Rarity rarity, int price) {
      super(id, nameKey, spriteId, rarity, price);
    }
  }
}
