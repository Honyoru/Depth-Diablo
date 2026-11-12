package com.epitech.depthdiablo.combat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** GDD 1.5: a melee swing's hitbox is a rectangle that turns towards the aim. */
class ObbTest {

  @Test
  void should_keep_the_values_it_was_created_with() {
    Obb obb = new Obb(10, 20, 45, 24, 8);

    assertEquals(10, obb.centreX());
    assertEquals(20, obb.centreY());
    assertEquals(45, obb.angleDegrees());
    assertEquals(24, obb.length());
    assertEquals(8, obb.width());
  }

  @Test
  void should_accept_a_negative_angle() {
    assertDoesNotThrow(() -> new Obb(0, 0, -30, 24, 8));
  }

  @ParameterizedTest
  @ValueSource(floats = {0f, -1f, Float.NaN, Float.POSITIVE_INFINITY})
  void should_reject_a_length_that_is_not_above_zero(float length) {
    assertThrows(IllegalArgumentException.class, () -> new Obb(0, 0, 0, length, 8));
  }

  @ParameterizedTest
  @ValueSource(floats = {0f, -1f, Float.NaN, Float.POSITIVE_INFINITY})
  void should_reject_a_width_that_is_not_above_zero(float width) {
    assertThrows(IllegalArgumentException.class, () -> new Obb(0, 0, 0, 24, width));
  }

  @ParameterizedTest
  @ValueSource(floats = {Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY})
  void should_reject_a_centre_or_an_angle_that_is_not_finite(float bad) {
    assertThrows(IllegalArgumentException.class, () -> new Obb(bad, 0, 0, 24, 8));
    assertThrows(IllegalArgumentException.class, () -> new Obb(0, bad, 0, 24, 8));
    assertThrows(IllegalArgumentException.class, () -> new Obb(0, 0, bad, 24, 8));
  }
}
