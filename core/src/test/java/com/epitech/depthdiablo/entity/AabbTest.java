package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.FloatRange;
import org.junit.jupiter.api.Test;

/** GDD 1.5: body boxes, hurtboxes and hitboxes are axis-aligned boxes that collide by overlap. */
class AabbTest {

  @Test
  void should_overlap_when_the_boxes_share_some_area() {
    Aabb first = new Aabb(0, 0, 12, 12);
    Aabb second = new Aabb(10, 0, 16, 16);

    boolean overlaps = first.overlaps(second);

    assertTrue(overlaps);
  }

  @Test
  void should_not_overlap_when_the_boxes_are_apart_horizontally() {
    Aabb first = new Aabb(0, 0, 12, 12);
    Aabb second = new Aabb(40, 0, 16, 16);

    boolean overlaps = first.overlaps(second);

    assertFalse(overlaps);
  }

  @Test
  void should_not_overlap_when_the_boxes_are_apart_vertically() {
    Aabb first = new Aabb(0, 0, 10, 10);
    Aabb second = new Aabb(0, 20, 10, 10);

    boolean overlaps = first.overlaps(second);

    assertFalse(overlaps);
  }

  @Test
  void should_not_overlap_when_the_boxes_only_touch_on_an_edge() {
    Aabb first = new Aabb(0, 0, 10, 10);
    Aabb second = new Aabb(10, 0, 10, 10);

    boolean overlaps = first.overlaps(second);

    assertFalse(overlaps);
  }

  @Test
  void should_overlap_when_one_box_is_inside_the_other() {
    Aabb outer = new Aabb(0, 0, 100, 100);
    Aabb inner = new Aabb(40, 40, 10, 10);

    boolean overlaps = outer.overlaps(inner);

    assertTrue(overlaps);
  }

  @Property
  void should_give_the_same_answer_when_the_boxes_are_swapped(
      @ForAll @FloatRange(min = -100, max = 100) float x1,
      @ForAll @FloatRange(min = -100, max = 100) float y1,
      @ForAll @FloatRange(min = -100, max = 100) float x2,
      @ForAll @FloatRange(min = -100, max = 100) float y2,
      @ForAll @FloatRange(min = 0, max = 50) float size) {
    Aabb first = new Aabb(x1, y1, size, size);
    Aabb second = new Aabb(x2, y2, size, size);

    boolean forward = first.overlaps(second);
    boolean backward = second.overlaps(first);

    assertEquals(forward, backward);
  }

  @Property
  void should_overlap_itself_when_its_size_is_positive(
      @ForAll @FloatRange(min = -100, max = 100) float x,
      @ForAll @FloatRange(min = -100, max = 100) float y,
      @ForAll @FloatRange(min = 1, max = 50) float width,
      @ForAll @FloatRange(min = 1, max = 50) float height) {
    Aabb box = new Aabb(x, y, width, height);

    boolean overlaps = box.overlaps(box);

    assertTrue(overlaps);
  }

  @Test
  void should_reject_a_negative_width() {
    float width = -1;

    assertThrows(IllegalArgumentException.class, () -> new Aabb(0, 0, width, 10));
  }

  @Test
  void should_reject_a_negative_height() {
    float height = -1;

    assertThrows(IllegalArgumentException.class, () -> new Aabb(0, 0, 10, height));
  }

  @Test
  void should_accept_a_size_of_0() {
    Aabb point = new Aabb(5, 5, 0, 0);

    assertEquals(0, point.width());
  }

  @Test
  void should_shift_its_position_when_translated() {
    Aabb box = new Aabb(10, 20, 16, 8);

    Aabb moved = box.translated(5, -3);

    assertEquals(new Aabb(15, 17, 16, 8), moved);
  }

  @Test
  void should_leave_the_original_unchanged_when_translated() {
    Aabb box = new Aabb(10, 20, 16, 8);

    box.translated(5, -3);

    assertEquals(new Aabb(10, 20, 16, 8), box);
  }
}
