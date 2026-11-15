package com.epitech.depthdiablo.debug;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** GDD 1.1.2, F1: the views come in a fixed loop, sprites, X-ray, high contrast. */
class ViewModeTest {

  @Test
  void should_go_from_sprites_to_xray() {
    ViewMode next = ViewMode.SPRITES.next();

    assertEquals(ViewMode.XRAY, next);
  }

  @Test
  void should_go_from_xray_to_high_contrast() {
    ViewMode next = ViewMode.XRAY.next();

    assertEquals(ViewMode.HIGH_CONTRAST, next);
  }

  @Test
  void should_loop_back_to_sprites_after_high_contrast() {
    ViewMode next = ViewMode.HIGH_CONTRAST.next();

    assertEquals(ViewMode.SPRITES, next);
  }
}
