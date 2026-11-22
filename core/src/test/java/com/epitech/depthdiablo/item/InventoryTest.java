package com.epitech.depthdiablo.item;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.entity.character.Character;
import com.epitech.depthdiablo.entity.stats.BuildModifier;
import com.epitech.depthdiablo.testdata.Items;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.4: the 3 hotbar slots take only consumables, one per slot; a weapon is always held. */
class InventoryTest {

  @Test
  void should_have_an_empty_hotbar_when_created() {
    Inventory inventory = anInventory();

    for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
      assertSame(Inventory.EMPTY_CONSUMABLE_SLOT, inventory.hotbarSlot(i));
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

    assertThrows(
        IllegalArgumentException.class, () -> inventory.add(Inventory.EMPTY_CONSUMABLE_SLOT));
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

    assertSame(sword, new Inventory(sword, Items.leatherArmor()).getWeapon());
  }

  @Test
  void should_throw_when_the_starting_weapon_is_null() {
    LeatherArmor armor = Items.leatherArmor();

    assertThrows(NullPointerException.class, () -> new Inventory(null, armor));
  }

  @Test
  void should_hold_the_new_weapon_once_equipped() {
    Inventory inventory = anInventory();
    Sword newSword = Items.sword();

    inventory.equip(newSword);

    assertSame(newSword, inventory.getWeapon());
  }

  @Test
  void should_give_back_the_old_weapon_when_equipping_a_new_one() {
    Sword oldSword = Items.sword();
    Inventory inventory = new Inventory(oldSword, Items.leatherArmor());

    assertSame(oldSword, inventory.equip(Items.sword()));
  }

  @Test
  void should_keep_its_weapon_when_the_new_one_is_null() {
    Sword sword = Items.sword();
    Inventory inventory = new Inventory(sword, Items.leatherArmor());

    assertThrows(NullPointerException.class, () -> inventory.equip((Weapon) null));
    assertSame(sword, inventory.getWeapon());
  }

  @Test
  void should_hold_its_starting_armor() {
    LeatherArmor armor = Items.leatherArmor();

    assertSame(armor, new Inventory(Items.sword(), armor).getArmor());
  }

  @Test
  void should_throw_when_the_starting_armor_is_null() {
    Sword sword = Items.sword();

    assertThrows(NullPointerException.class, () -> new Inventory(sword, null));
  }

  @Test
  void should_hold_the_new_armor_once_equipped() {
    Inventory inventory = anInventory();
    LeatherArmor newArmor = Items.leatherArmor();

    inventory.equip(newArmor);

    assertSame(newArmor, inventory.getArmor());
  }

  @Test
  void should_give_back_the_old_armor_when_equipping_a_new_one() {
    LeatherArmor oldArmor = Items.leatherArmor();
    Inventory inventory = new Inventory(Items.sword(), oldArmor);

    assertSame(oldArmor, inventory.equip(Items.leatherArmor()));
  }

  @Test
  void should_keep_its_armor_when_the_new_one_is_null() {
    LeatherArmor armor = Items.leatherArmor();
    Inventory inventory = new Inventory(Items.sword(), armor);

    assertThrows(NullPointerException.class, () -> inventory.equip((Armor) null));
    assertSame(armor, inventory.getArmor());
  }

  @Test
  void should_heal_the_user_with_the_potion_of_the_hotbar_slot() {
    Inventory inventory = anInventory();
    Potion potion = Items.potion();
    inventory.add(potion);
    Character user = aCharacter().withHp(100).build();
    user.takeDamage(90); // room for the whole heal

    assertTrue(inventory.use(0, user));
    assertEquals(10 + potion.getHealAmount(), user.getHp());
  }

  @Test
  void should_empty_the_slot_once_its_consumable_is_spent() {
    Inventory inventory = anInventory();
    inventory.add(Items.potion());

    inventory.use(0, aCharacter().build());

    assertSame(Inventory.EMPTY_CONSUMABLE_SLOT, inventory.hotbarSlot(0));
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
    assertEquals(4, user.getHp());
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
  void should_give_its_weapon_then_its_armor_as_modifiers() {
    Sword sword = Items.sword();
    LeatherArmor armor = Items.leatherArmor();

    assertEquals(List.of(sword, armor), new Inventory(sword, armor).getModifiers());
  }

  @Test
  void should_give_the_new_weapon_as_modifier_once_equipped() {
    Inventory inventory = anInventory();
    Sword newSword = Items.sword();

    inventory.equip(newSword);

    assertEquals(List.of(newSword, inventory.getArmor()), inventory.getModifiers());
  }

  @Test
  void should_give_the_new_armor_as_modifier_once_equipped() {
    Inventory inventory = anInventory();
    LeatherArmor newArmor = Items.leatherArmor();

    inventory.equip(newArmor);

    assertEquals(List.of(inventory.getWeapon(), newArmor), inventory.getModifiers());
  }

  @Test
  void should_not_let_the_modifiers_be_changed_from_outside() {
    Inventory inventory = anInventory();
    List<BuildModifier> modifiers = inventory.getModifiers();
    Sword other = Items.sword();

    assertThrows(UnsupportedOperationException.class, () -> modifiers.add(other));
  }

  @Test
  void should_have_free_passive_slots_when_created() {
    Inventory inventory = anInventory();

    for (int i = 0; i < Inventory.PASSIVE_SIZE; i++) {
      assertSame(Inventory.EMPTY_PASSIVE_SLOT, inventory.passiveSlot(i));
      assertTrue(inventory.isPassiveSlotFree(i));
    }
  }

  @Test
  void should_put_a_passive_item_in_the_first_free_passive_slot() {
    Inventory inventory = anInventory();
    FireRing ring = Items.fireRing();

    assertTrue(inventory.add(ring));
    assertSame(ring, inventory.passiveSlot(0));
    assertFalse(inventory.isPassiveSlotFree(0));
  }

  @Test
  void should_refuse_a_passive_item_when_the_passive_slots_are_full() {
    Inventory inventory = anInventory();
    for (int i = 0; i < Inventory.PASSIVE_SIZE; i++) {
      inventory.add(Items.fireRing());
    }

    assertFalse(inventory.add(Items.fireRing()));
  }

  @Test
  void should_throw_when_the_passive_item_is_null() {
    Inventory inventory = anInventory();

    assertThrows(NullPointerException.class, () -> inventory.add((PassiveItem) null));
  }

  @Test
  void should_refuse_the_free_passive_slot_marker() {
    Inventory inventory = anInventory();

    assertThrows(IllegalArgumentException.class, () -> inventory.add(Inventory.EMPTY_PASSIVE_SLOT));
  }

  @Test
  void should_throw_when_the_passive_slot_is_past_the_last_one() {
    Inventory inventory = anInventory();

    assertThrows(
        IndexOutOfBoundsException.class, () -> inventory.passiveSlot(Inventory.PASSIVE_SIZE));
  }

  @Test
  void should_give_back_the_passive_item_it_removes() {
    Inventory inventory = anInventory();
    FireRing ring = Items.fireRing();
    inventory.add(ring);

    assertEquals(Optional.of(ring), inventory.removePassive(0));
  }

  @Test
  void should_free_the_slot_of_the_removed_passive_item() {
    Inventory inventory = anInventory();
    inventory.add(Items.fireRing());

    inventory.removePassive(0);

    assertTrue(inventory.isPassiveSlotFree(0));
  }

  @Test
  void should_give_nothing_when_removing_from_a_free_passive_slot() {
    assertEquals(Optional.empty(), anInventory().removePassive(0));
  }

  @Test
  void should_give_back_the_consumable_it_removes() {
    Inventory inventory = anInventory();
    Potion potion = Items.potion();
    inventory.add(potion);

    assertEquals(Optional.of(potion), inventory.removeConsumable(0));
    assertTrue(inventory.isHotbarSlotFree(0));
  }

  @Test
  void should_give_nothing_when_removing_from_a_free_hotbar_slot() {
    assertEquals(Optional.empty(), anInventory().removeConsumable(0));
  }

  @Test
  void should_give_the_passive_items_as_modifiers_after_the_armor() {
    Inventory inventory = anInventory();
    FireRing ring = Items.fireRing();
    inventory.add(ring);

    assertEquals(
        List.of(inventory.getWeapon(), inventory.getArmor(), ring), inventory.getModifiers());
  }

  @Test
  void should_give_the_passive_items_in_slot_order() {
    Inventory inventory = anInventory();
    FireRing first = Items.fireRing();
    FireRing second = Items.fireRing();
    FireRing third = Items.fireRing();
    inventory.add(first);
    inventory.add(second);
    inventory.removePassive(0);

    inventory.add(third); // picked up last, but put in slot 0

    assertEquals(
        List.of(inventory.getWeapon(), inventory.getArmor(), third, second),
        inventory.getModifiers());
  }

  @Test
  void should_swap_two_passive_items() {
    Inventory inventory = anInventory();
    FireRing first = Items.fireRing();
    FireRing second = Items.fireRing();
    inventory.add(first);
    inventory.add(second);

    inventory.swapPassiveSlots(0, 1);

    assertSame(second, inventory.passiveSlot(0));
    assertSame(first, inventory.passiveSlot(1));
  }

  @Test
  void should_move_a_passive_item_to_a_free_slot() {
    Inventory inventory = anInventory();
    FireRing ring = Items.fireRing();
    inventory.add(ring);

    inventory.swapPassiveSlots(0, 5);

    assertTrue(inventory.isPassiveSlotFree(0));
    assertSame(ring, inventory.passiveSlot(5));
  }

  @Test
  void should_change_the_order_of_the_modifiers_when_two_passive_items_are_swapped() {
    Inventory inventory = anInventory();
    FireRing first = Items.fireRing();
    FireRing second = Items.fireRing();
    inventory.add(first);
    inventory.add(second);

    inventory.swapPassiveSlots(0, 1);

    assertEquals(
        List.of(inventory.getWeapon(), inventory.getArmor(), second, first),
        inventory.getModifiers());
  }

  @Test
  void should_keep_the_slot_when_swapped_with_itself() {
    Inventory inventory = anInventory();
    FireRing ring = Items.fireRing();
    inventory.add(ring);

    inventory.swapPassiveSlots(0, 0);

    assertSame(ring, inventory.passiveSlot(0));
  }

  @Test
  void should_throw_when_a_swapped_passive_slot_is_past_the_last_one() {
    Inventory inventory = anInventory();

    assertThrows(
        IndexOutOfBoundsException.class,
        () -> inventory.swapPassiveSlots(0, Inventory.PASSIVE_SIZE));
  }

  @Test
  void should_give_no_effect_for_a_free_slot() {
    assertEquals(List.of(), Inventory.EMPTY_CONSUMABLE_SLOT.getEffects());
    assertEquals(List.of(), Inventory.EMPTY_PASSIVE_SLOT.getEffects());
  }

  @Test
  void should_get_back_its_slots_when_its_state_is_read_back() {
    Inventory inventory = anInventory();
    Weapon weapon = inventory.getWeapon();
    Armor armor = inventory.getArmor();
    Potion potion = Items.potion();
    FireRing ring = Items.fireRing();
    inventory.add(potion);
    inventory.add(ring);
    inventory.swapPassiveSlots(0, 2);
    StateBuffer photo = new StateBuffer();
    inventory.writeState(photo);
    inventory.removeConsumable(0);
    inventory.removePassive(2);
    inventory.equip(Items.sword());
    inventory.equip(Items.leatherArmor());

    photo.startReading();
    inventory.readState(photo);

    assertSame(weapon, inventory.getWeapon());
    assertSame(armor, inventory.getArmor());
    assertSame(potion, inventory.hotbarSlot(0));
    assertSame(ring, inventory.passiveSlot(2));
    assertTrue(inventory.isPassiveSlotFree(0));
  }

  private static Inventory anInventory() {
    return new Inventory(Items.sword(), Items.leatherArmor());
  }
}
