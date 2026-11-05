package com.epitech.depthdiablo.rules;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static com.epitech.depthdiablo.testdata.FakeItems.aConsumable;
import static com.epitech.depthdiablo.testdata.FakeItems.anItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.item.Inventory;
import com.epitech.depthdiablo.item.Item;
import com.epitech.depthdiablo.testdata.FakeItems.CountingConsumable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * GDD 1.4.1, "Items", the inventory rules the Arena slice needs. Still to add when their classes
 * land: pickup key in range (ItemPickup), passive items and burn (PassiveItem, FireRing), chest and
 * passive-item sources (Chest, LootTable).
 */
class ItemRulesTest {

  @Test
  void should_apply_the_effect_and_empty_the_slot_when_a_hotbar_consumable_is_used() {
    Inventory inventory = new Inventory();
    CountingConsumable potion = aConsumable("potion");
    inventory.add(potion);
    Character user = aCharacter().build();

    inventory.use(0, user);

    assertEquals(1, potion.uses());
    assertEquals(Optional.empty(), inventory.item(0));
  }

  @Test
  void should_do_nothing_when_an_empty_hotbar_slot_is_used() {
    Inventory inventory = new Inventory();
    Character user = aCharacter().withHp(10).build();

    inventory.use(0, user);

    assertEquals(Optional.empty(), inventory.item(0));
    assertEquals(10, user.hp());
  }

  @Test
  void should_put_every_item_in_its_own_slot_when_the_inventory_fills_up() {
    Inventory inventory = new Inventory();
    for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
      inventory.add(aConsumable("potion-" + i));
    }
    for (int i = Inventory.HOTBAR_SIZE; i < Inventory.CAPACITY; i++) {
      inventory.add(anItem("item-" + i));
    }

    Set<Item> distinct = new HashSet<>();
    for (int slot = 0; slot < Inventory.CAPACITY; slot++) {
      inventory.item(slot).ifPresent(distinct::add);
    }

    assertEquals(Inventory.CAPACITY, distinct.size());
  }

  @Test
  void should_refuse_a_pickup_when_all_20_slots_are_full() {
    Inventory inventory = new Inventory();
    for (int i = 0; i < Inventory.CAPACITY; i++) {
      inventory.add(aConsumable("potion-" + i));
    }

    boolean added = inventory.add(anItem("one-too-many"));

    assertFalse(added);
    assertTrue(inventory.isFull());
  }

  @Test
  void should_keep_non_consumables_out_of_the_hotbar() {
    Inventory inventory = new Inventory();

    inventory.add(anItem("sword"));

    for (int slot = 0; slot < Inventory.HOTBAR_SIZE; slot++) {
      assertEquals(Optional.empty(), inventory.item(slot));
    }
    assertTrue(inventory.item(Inventory.HOTBAR_SIZE).isPresent());
  }

  @Test
  void should_refuse_a_non_consumable_when_only_hotbar_slots_are_free() {
    Inventory inventory = new Inventory();
    for (int i = Inventory.HOTBAR_SIZE; i < Inventory.CAPACITY; i++) {
      inventory.add(anItem("item-" + i));
    }

    boolean added = inventory.add(anItem("sword"));

    assertFalse(added);
  }

  @Test
  void should_store_a_consumable_in_storage_when_the_hotbar_is_full() {
    Inventory inventory = new Inventory();
    for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
      inventory.add(aConsumable("potion-" + i));
    }

    boolean added = inventory.add(aConsumable("spare"));

    assertTrue(added);
    assertTrue(inventory.item(Inventory.HOTBAR_SIZE).isPresent());
  }
}
