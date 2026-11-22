package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.geometry.Aabb;
import org.junit.jupiter.api.Test;

/** GDD 1.5: a wall is a static entity that stops movement. */
class WallTest {

  @Test
  void should_block_movement() {
    Wall wall = new Wall(new Aabb(0, 0, 32, 32));

    boolean blocks = wall.blocks();

    assertTrue(blocks);
  }

  @Test
  void should_stay_in_place_when_updated() {
    Aabb box = new Aabb(64, 32, 32, 32);
    Wall wall = new Wall(box);

    wall.update(1);

    assertSame(box, wall.getBodyBox());
  }

  @Test
  void should_reject_a_missing_box() {
    assertThrows(NullPointerException.class, () -> new Wall(null));
  }
}
