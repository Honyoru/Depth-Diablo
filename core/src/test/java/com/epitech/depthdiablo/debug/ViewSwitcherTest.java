package com.epitech.depthdiablo.debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** GDD 1.1.2, F1: the view switcher, a debug feature the game must run the same without. */
class ViewSwitcherTest {
  private final ViewSwitcher switcher = new ViewSwitcher();

  @Test
  void should_start_enabled_on_the_sprite_view() {
    ViewMode mode = switcher.getMode();

    assertTrue(switcher.isEnabled());
    assertEquals(ViewMode.SPRITES, mode);
  }

  @Test
  void should_show_the_next_view_when_cycled() {
    switcher.cycle();

    assertEquals(ViewMode.XRAY, switcher.getMode());
  }

  @Test
  void should_come_back_to_sprites_after_three_cycles() {
    switcher.cycle();
    switcher.cycle();
    switcher.cycle();

    assertEquals(ViewMode.SPRITES, switcher.getMode());
  }

  @Test
  void should_show_sprites_when_disabled() {
    switcher.cycle();

    switcher.disable();

    assertFalse(switcher.isEnabled());
    assertEquals(ViewMode.SPRITES, switcher.getMode());
  }

  @Test
  void should_ignore_cycles_while_disabled() {
    switcher.disable();

    switcher.cycle();
    switcher.enable();

    assertEquals(ViewMode.SPRITES, switcher.getMode());
  }

  @Test
  void should_come_back_to_its_view_when_enabled_again() {
    switcher.cycle();
    switcher.disable();

    switcher.enable();

    assertEquals(ViewMode.XRAY, switcher.getMode());
  }

  @Test
  void should_keep_its_view_when_the_game_ticks() {
    switcher.cycle();

    switcher.onTick(42);

    assertEquals(ViewMode.XRAY, switcher.getMode());
  }

  @Test
  void should_have_a_name_for_logs() {
    String name = switcher.getName();

    assertEquals("View switcher", name);
  }
}
