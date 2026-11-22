package com.epitech.depthdiablo.geometry;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.FloatRange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** GDD 1.5: a melee swing's hitbox is a rectangle that turns towards the aim. */
class ObbTest {
  private static final float DELTA = 0.001f;

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

  @Test
  void should_give_the_corners_of_a_flat_rectangle_at_angle_0() {
    Vector2[] corners = new Obb(10, 20, 0, 24, 8).corners();

    assertCorner(-2, 16, corners[0]);
    assertCorner(22, 16, corners[1]);
    assertCorner(22, 24, corners[2]);
    assertCorner(-2, 24, corners[3]);
  }

  @Test
  void should_turn_the_corners_when_the_angle_is_90() {
    Vector2[] corners = new Obb(10, 20, 90, 24, 8).corners();

    assertCorner(14, 8, corners[0]);
    assertCorner(14, 32, corners[1]);
    assertCorner(6, 32, corners[2]);
    assertCorner(6, 8, corners[3]);
  }

  @Test
  void should_turn_the_corners_when_the_angle_is_45() {
    Vector2[] corners = new Obb(0, 0, 45, 24, 8).corners();

    assertCorner(-5.657f, -11.314f, corners[0]);
    assertCorner(11.314f, 5.657f, corners[1]);
    assertCorner(5.657f, 11.314f, corners[2]);
    assertCorner(-11.314f, -5.657f, corners[3]);
  }

  @Property
  void should_keep_its_length_and_width_at_any_angle(
      @ForAll @FloatRange(min = -360, max = 360) float angle) {
    Vector2[] corners = new Obb(0, 0, angle, 24, 8).corners();

    assertEquals(24, corners[0].dst(corners[1]), DELTA);
    assertEquals(8, corners[1].dst(corners[2]), DELTA);
  }

  @Test
  void should_overlap_a_box_it_covers_at_angle_0() {
    assertTrue(new Obb(10, 20, 0, 24, 8).overlaps(new Aabb(20, 18, 10, 10)));
  }

  @Test
  void should_not_overlap_a_box_beyond_its_front_at_angle_0() {
    assertFalse(new Obb(10, 20, 0, 24, 8).overlaps(new Aabb(30, 18, 10, 10)));
  }

  @Test
  void should_not_overlap_a_box_that_only_touches_its_edge() {
    assertFalse(new Obb(10, 20, 0, 24, 8).overlaps(new Aabb(22, 16, 5, 5)));
  }

  @Test
  void should_overlap_a_box_on_its_diagonal_at_angle_45() {
    assertTrue(new Obb(0, 0, 45, 24, 8).overlaps(new Aabb(6, 6, 4, 4)));
  }

  @Test
  void should_not_overlap_a_box_in_the_empty_corner_at_angle_45() {
    // The Aabb around this Obb would cover this box: the "hit where the sword is not" case.
    assertFalse(new Obb(0, 0, 45, 24, 8).overlaps(new Aabb(7, -11, 4, 4)));
  }

  @Test
  void should_throw_when_the_box_is_null() {
    Obb obb = new Obb(0, 0, 0, 24, 8);

    NullPointerException error = assertThrows(NullPointerException.class, () -> obb.overlaps(null));

    assertEquals("The box to check for overlap must not be null", error.getMessage());
  }

  @Property
  void should_overlap_a_small_box_on_its_centre_at_any_angle(
      @ForAll @FloatRange(min = -360, max = 360) float angle) {
    assertTrue(new Obb(50, 50, angle, 24, 8).overlaps(new Aabb(49, 49, 2, 2)));
  }

  @Test
  void should_cover_the_same_area_as_the_box() {
    Obb obb = Obb.around(new Aabb(10, 20, 24, 8));

    assertEquals(22, obb.centreX());
    assertEquals(24, obb.centreY());
    assertEquals(0, obb.angleDegrees());
    assertEquals(24, obb.length());
    assertEquals(8, obb.width());
  }

  @Test
  void should_put_its_corners_on_the_corners_of_the_box() {
    Vector2[] corners = Obb.around(new Aabb(10, 20, 24, 8)).corners();

    assertCorner(10, 20, corners[0]);
    assertCorner(34, 20, corners[1]);
    assertCorner(34, 28, corners[2]);
    assertCorner(10, 28, corners[3]);
  }

  @Property
  void should_overlap_exactly_when_the_box_does(
      @ForAll @FloatRange(min = -50, max = 50) float x,
      @ForAll @FloatRange(min = -50, max = 50) float y) {
    Aabb box = new Aabb(0, 0, 24, 8);
    Aabb other = new Aabb(x, y, 10, 10);

    assertEquals(box.overlaps(other), Obb.around(box).overlaps(other));
  }

  @Test
  void should_throw_when_the_box_to_cover_is_null() {
    NullPointerException error = assertThrows(NullPointerException.class, () -> Obb.around(null));

    assertEquals("The box to cover must not be null", error.getMessage());
  }

  @Test
  void should_throw_when_the_box_has_no_width() {
    Aabb flat = new Aabb(0, 0, 0, 8);

    assertThrows(IllegalArgumentException.class, () -> Obb.around(flat));
  }

  private static void assertCorner(float x, float y, Vector2 corner) {
    assertEquals(x, corner.x, DELTA);
    assertEquals(y, corner.y, DELTA);
  }
}
