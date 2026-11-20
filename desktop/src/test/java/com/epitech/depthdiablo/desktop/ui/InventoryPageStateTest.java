package com.epitech.depthdiablo.desktop.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** GDD 1.4, Q58: the inventory page opens and closes with I and pauses the game while open. */
class InventoryPageStateTest {

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
}
