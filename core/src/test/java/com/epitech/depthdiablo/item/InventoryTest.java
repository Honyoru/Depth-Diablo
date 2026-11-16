package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.testdata.Items;
import org.junit.jupiter.api.Test;

/** GDD 1.4: the 3 hotbar slots take only consumables, one per slot; a weapon is always held. */
class InventoryTest {

  @Test
  void should_have_an_empty_hotbar_when_created() {
    Inventory inventory = anInventory();

    for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
      assertTrue(inventory.hotbarSlot(i).isEmpty());
    }
  }

  @Test
  void should_put_a_consumable_in_the_first_hotbar_slot() {
    Inventory inventory = anInventory();
    Potion potion = Items.potion();

    assertTrue(inventory.add(potion));
    assertSame(potion, inventory.hotbarSlot(0).orElseThrow());
  }

  @Test
  void should_put_the_next_consumable_in_the_next_free_slot() {
    Inventory inventory = anInventory();
    inventory.add(Items.potion());
    Potion second = Items.potion();

    assertTrue(inventory.add(second));
    assertSame(second, inventory.hotbarSlot(1).orElseThrow());
  }

  @Test
  void should_refuse_a_consumable_when_the_hotbar_is_full() {
    Inventory inventory = anInventory();
    for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
      inventory.add(Items.potion());
    }
    Consumable last = inventory.hotbarSlot(Inventory.HOTBAR_SIZE - 1).orElseThrow();

    assertFalse(inventory.add(Items.potion()));
    assertSame(last, inventory.hotbarSlot(Inventory.HOTBAR_SIZE - 1).orElseThrow());
  }

  @Test
  void should_throw_when_the_consumable_is_null() {
    Inventory inventory = anInventory();

    assertThrows(NullPointerException.class, () -> inventory.add((Consumable) null));
  }

  @Test
  void should_throw_when_the_hotbar_slot_is_negative() {
    Inventory inventory = anInventory();

    assertThrows(IndexOutOfBoundsException.class, () -> inventory.hotbarSlot(-1));
  }

  @Test
  void should_throw_when_the_hotbar_slot_is_past_the_last_one() {
    Inventory inventory = anInventory();

    assertThrows(
        IndexOutOfBoundsException.class, () -> inventory.hotbarSlot(Inventory.HOTBAR_SIZE));
  }

  @Test
  void should_hold_its_starting_weapon() {
    Sword sword = Items.sword();

    assertSame(sword, new Inventory(sword).weapon());
  }

  @Test
  void should_throw_when_the_starting_weapon_is_null() {
    assertThrows(NullPointerException.class, () -> new Inventory(null));
  }

  @Test
  void should_hold_the_new_weapon_once_equipped() {
    Inventory inventory = anInventory();
    Sword newSword = Items.sword();

    inventory.equip(newSword);

    assertSame(newSword, inventory.weapon());
  }

  @Test
  void should_give_back_the_old_weapon_when_equipping_a_new_one() {
    Sword oldSword = Items.sword();
    Inventory inventory = new Inventory(oldSword);

    assertSame(oldSword, inventory.equip(Items.sword()));
  }

  @Test
  void should_keep_its_weapon_when_the_new_one_is_null() {
    Sword sword = Items.sword();
    Inventory inventory = new Inventory(sword);

    assertThrows(NullPointerException.class, () -> inventory.equip(null));
    assertSame(sword, inventory.weapon());
  }

  private static Inventory anInventory() {
    return new Inventory(Items.sword());
  }
}
