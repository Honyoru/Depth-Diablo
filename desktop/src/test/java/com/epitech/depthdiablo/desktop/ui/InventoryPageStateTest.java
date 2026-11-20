package com.epitech.depthdiablo.desktop.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.4, Q58: the inventory page opens and closes with I and pauses the game while open. */
class InventoryPageStateTest {
  private static final PageSlot WEAPON = new PageSlot(PageSlot.Kind.WEAPON, 0);
  private static final PageSlot POTION = new PageSlot(PageSlot.Kind.HOTBAR, 0);
  private static final PageSlot RING = new PageSlot(PageSlot.Kind.PASSIVE, 0);
  private static final PageSlot FREE_PASSIVE = new PageSlot(PageSlot.Kind.PASSIVE, 5);

  @Test
  void should_be_closed_when_created() {
    assertFalse(new InventoryPageState().isOpen());
  }

  @Test
  void should_open_when_toggled() {
    InventoryPageState page = new InventoryPageState();

    page.toggle();

    assertTrue(page.isOpen());
  }

  @Test
  void should_close_when_toggled_again() {
    InventoryPageState page = new InventoryPageState();
    page.toggle();

    page.toggle();

    assertFalse(page.isOpen());
  }

  @Test
  void should_select_nothing_when_created() {
    assertEquals(Optional.empty(), new InventoryPageState().getSelected());
  }

  @Test
  void should_select_the_item_clicked() {
    InventoryPageState page = new InventoryPageState();

    page.click(RING, false);

    assertEquals(Optional.of(RING), page.getSelected());
  }

  @Test
  void should_select_nothing_when_a_free_slot_is_clicked() {
    InventoryPageState page = new InventoryPageState();
    page.click(RING, false);

    page.click(FREE_PASSIVE, true);

    assertEquals(Optional.empty(), page.getSelected());
  }

  @Test
  void should_swap_with_the_passive_slot_clicked_after_a_switch() {
    InventoryPageState page = new InventoryPageState();
    page.click(RING, false);
    page.toggleSwitch();

    Optional<PassiveSwap> swap = page.click(FREE_PASSIVE, true);

    assertEquals(Optional.of(new PassiveSwap(0, 5)), swap);
    assertFalse(page.isSwitching());
    assertEquals(Optional.empty(), page.getSelected());
  }

  @Test
  void should_cancel_the_switch_when_a_slot_that_is_not_passive_is_clicked() {
    InventoryPageState page = new InventoryPageState();
    page.click(RING, false);
    page.toggleSwitch();

    Optional<PassiveSwap> swap = page.click(POTION, false);

    assertEquals(Optional.empty(), swap);
    assertFalse(page.isSwitching());
  }

  @Test
  void should_cancel_the_switch_and_keep_the_selection_when_switch_is_pressed_again() {
    InventoryPageState page = new InventoryPageState();
    page.click(RING, false);
    page.toggleSwitch();

    page.toggleSwitch();

    assertFalse(page.isSwitching());
    assertEquals(Optional.of(RING), page.getSelected());
  }

  @Test
  void should_not_start_a_switch_when_the_selected_item_is_not_passive() {
    InventoryPageState page = new InventoryPageState();
    page.click(WEAPON, false);

    page.toggleSwitch();

    assertFalse(page.isSwitching());
  }

  @Test
  void should_cancel_the_selection_and_the_switch_when_closed() {
    InventoryPageState page = new InventoryPageState();
    page.toggle();
    page.click(RING, false);
    page.toggleSwitch();

    page.toggle();

    assertEquals(Optional.empty(), page.getSelected());
    assertFalse(page.isSwitching());
  }
}
