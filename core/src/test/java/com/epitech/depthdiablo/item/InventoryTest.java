package com.epitech.depthdiablo.item;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.entity.BuildModifier;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.testdata.Items;
import java.util.List;
import org.junit.jupiter.api.Test;

/** GDD 1.4: the 3 hotbar slots take only consumables, one per slot; a weapon is always held. */
class InventoryTest {

  @Test
  void should_have_an_empty_hotbar_when_created() {
    Inventory inventory = anInventory();

    for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
      assertSame(Inventory.EMPTY, inventory.hotbarSlot(i));
    }
  }

  @Test
  void should_put_a_consumable_in_the_first_hotbar_slot() {
    Inventory inventory = anInventory();
    Potion potion = Items.potion();

    assertTrue(inventory.add(potion));
    assertSame(potion, inventory.hotbarSlot(0));
  }

  @Test
  void should_put_the_next_consumable_in_the_next_free_slot() {
    Inventory inventory = anInventory();
    inventory.add(Items.potion());
    Potion second = Items.potion();

    assertTrue(inventory.add(second));
    assertSame(second, inventory.hotbarSlot(1));
  }

  @Test
  void should_refuse_a_consumable_when_the_hotbar_is_full() {
    Inventory inventory = anInventory();
    for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
      inventory.add(Items.potion());
    }
    Consumable last = inventory.hotbarSlot(Inventory.HOTBAR_SIZE - 1);

    assertFalse(inventory.add(Items.potion()));
    assertSame(last, inventory.hotbarSlot(Inventory.HOTBAR_SIZE - 1));
  }

  @Test
  void should_throw_when_the_consumable_is_null() {
    Inventory inventory = anInventory();

    assertThrows(NullPointerException.class, () -> inventory.add((Consumable) null));
  }

  @Test
  void should_refuse_the_empty_slot_marker() {
    Inventory inventory = anInventory();

    assertThrows(IllegalArgumentException.class, () -> inventory.add(Inventory.EMPTY));
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

  @Test
  void should_heal_the_user_with_the_potion_of_the_hotbar_slot() {
    Inventory inventory = anInventory();
    Potion potion = Items.potion();
    inventory.add(potion);
    Character user = aCharacter().withHp(10).build();
    user.takeDamage(6);

    assertTrue(inventory.use(0, user));
    assertEquals(4 + potion.healAmount(), user.hp());
  }

  @Test
  void should_empty_the_slot_once_its_consumable_is_spent() {
    Inventory inventory = anInventory();
    inventory.add(Items.potion());

    inventory.use(0, aCharacter().build());

    assertSame(Inventory.EMPTY, inventory.hotbarSlot(0));
  }

  @Test
  void should_keep_the_other_slots_when_one_is_used() {
    Inventory inventory = anInventory();
    inventory.add(Items.potion());
    Potion second = Items.potion();
    inventory.add(second);

    inventory.use(0, aCharacter().build());

    assertSame(second, inventory.hotbarSlot(1));
  }

  @Test
  void should_do_nothing_when_the_hotbar_slot_is_empty() {
    Inventory inventory = anInventory();
    Character user = aCharacter().withHp(10).build();
    user.takeDamage(6);

    assertFalse(inventory.use(0, user));
    assertEquals(4, user.hp());
  }

  @Test
  void should_keep_the_consumable_when_the_user_is_null() {
    Inventory inventory = anInventory();
    Potion potion = Items.potion();
    inventory.add(potion);

    assertThrows(NullPointerException.class, () -> inventory.use(0, null));
    assertSame(potion, inventory.hotbarSlot(0));
  }

  @Test
  void should_throw_when_the_used_slot_is_outside_the_hotbar() {
    Inventory inventory = anInventory();
    Character user = aCharacter().build();

    assertThrows(IndexOutOfBoundsException.class, () -> inventory.use(-1, user));
    assertThrows(IndexOutOfBoundsException.class, () -> inventory.use(Inventory.HOTBAR_SIZE, user));
  }

  @Test
  void should_give_its_weapon_as_the_only_modifier_for_now() {
    Sword sword = Items.sword();

    assertEquals(List.of(sword), new Inventory(sword).modifiers());
  }

  @Test
  void should_give_the_new_weapon_as_modifier_once_equipped() {
    Inventory inventory = anInventory();
    Sword newSword = Items.sword();

    inventory.equip(newSword);

    assertEquals(List.of(newSword), inventory.modifiers());
  }

  @Test
  void should_not_let_the_modifiers_be_changed_from_outside() {
    Inventory inventory = anInventory();
    List<BuildModifier> modifiers = inventory.modifiers();
    Sword other = Items.sword();

    assertThrows(UnsupportedOperationException.class, () -> modifiers.add(other));
  }

  private static Inventory anInventory() {
    return new Inventory(Items.sword());
  }
}
