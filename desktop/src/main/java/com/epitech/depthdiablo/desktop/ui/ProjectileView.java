package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Projectile;
import java.util.Objects;

/**
 * Draws the projectiles in flight (GDD 1.3.1): the picture of their {@code spriteId}, which points
 * right, turned towards where they fly. The picture is a square as long as the projectile's body,
 * centred on it, so a thin arrow keeps its drawn proportions.
 */
final class ProjectileView {
  private final ItemTextures textures;

  /**
   * Creates the view.
   *
   * @param textures the pictures, by sprite id; the game screen owns and disposes them
   */
  ProjectileView(ItemTextures textures) {
    this.textures = Objects.requireNonNull(textures, "textures is null");
  }

  /**
   * Draws a projectile, turned along its flight.
   *
   * @param batch the batch, already begun
   * @param projectile the projectile in flight
   */
  void draw(Batch batch, Projectile projectile) {
    Texture texture = textures.get(projectile.getSpriteId());
    Rectangle area = area(projectile.getBodyBox());
    batch.draw(
        texture,
        area.x,
        area.y,
        area.width / 2f, // turns around its centre
        area.height / 2f,
        area.width,
        area.height,
        1f,
        1f,
        projectile.getAngleDegrees(),
        0,
        0,
        texture.getWidth(),
        texture.getHeight(),
        false,
        false);
  }

  /**
   * Returns the square the picture fills: as long as the body, centred on it.
   *
   * @param body the projectile's body box
   * @return the square, in world pixels
   */
  static Rectangle area(Aabb body) {
    float side = body.width();
    float centreX = body.x() + body.width() / 2f;
    float centreY = body.y() + body.height() / 2f;
    return new Rectangle(centreX - side / 2f, centreY - side / 2f, side, side);
  }
}
