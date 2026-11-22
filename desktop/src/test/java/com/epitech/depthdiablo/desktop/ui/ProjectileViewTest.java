package com.epitech.depthdiablo.desktop.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.gdx.math.Rectangle;
import com.epitech.depthdiablo.entity.Aabb;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: a projectile is drawn as a square as long as its body, centred on it. */
class ProjectileViewTest {

  @Test
  void should_draw_a_square_as_long_as_the_body_centred_on_it() {
    Rectangle area = ProjectileView.area(new Aabb(10f, 20f, 16f, 4f)); // an arrow

    assertEquals(new Rectangle(10f, 14f, 16f, 16f), area);
  }
}
