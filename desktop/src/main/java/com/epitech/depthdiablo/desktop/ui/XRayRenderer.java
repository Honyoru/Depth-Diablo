package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.epitech.depthdiablo.combat.Damageable;
import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.entity.character.Character;
import com.epitech.depthdiablo.entity.projectile.Projectile;
import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.geometry.Obb;

/**
 * The X-ray view (GDD 1.1.2): no sprites, only what the logic sees. Body boxes in grey, hurtboxes
 * in green for the player (blue while it is invincible) and magenta for the others, the blade of a
 * running swing and the hitbox of a projectile in red, and the player's velocity as a yellow line.
 */
final class XRayRenderer implements Renderer {
  private static final Color BODY_COLOR = Color.GRAY;
  private static final Color PLAYER_HURTBOX_COLOR = Color.GREEN;
  private static final Color ENEMY_HURTBOX_COLOR = Color.MAGENTA;
  private static final Color INVINCIBLE_COLOR = Color.BLUE;
  private static final Color VELOCITY_COLOR = Color.YELLOW;
  private static final Color BLADE_COLOR = Color.RED;

  /** The velocity is a few pixels per tick, so the line is drawn longer to be seen. */
  private static final float VELOCITY_SCALE = 8f;

  private final ShapeRenderer shapes = new ShapeRenderer();

  @Override
  public void render(Frame frame) {
    shapes.setProjectionMatrix(frame.camera().combined);
    shapes.begin(ShapeRenderer.ShapeType.Line);
    for (Entity entity : frame.world().getEntities()) {
      drawBoxes(frame, entity);
    }
    drawVelocity(frame);
    shapes.end();
  }

  private void drawBoxes(Frame frame, Entity entity) {
    float dx = 0f;
    float dy = 0f;
    if (entity.equals(frame.player())) {
      dx = frame.playerDrawPosition().x - entity.getBodyBox().x();
      dy = frame.playerDrawPosition().y - entity.getBodyBox().y();
    }
    shapes.setColor(BODY_COLOR);
    drawBox(entity.getBodyBox().translated(dx, dy));
    if (entity instanceof Damageable damageable) {
      shapes.setColor(hurtboxColor(frame, entity));
      drawBox(damageable.getHurtbox().translated(dx, dy));
    }
    if (entity instanceof Character character) {
      float shiftX = dx;
      float shiftY = dy;
      character.getCurrentSwing().ifPresent(swing -> drawBlade(swing.getShape(), shiftX, shiftY));
    }
    if (entity instanceof Projectile projectile) {
      drawBlade(projectile.getHitShape(), 0f, 0f); // red, like a blade: what hurts
    }
  }

  /** The player's hurtbox in green, or blue while nothing can hurt it; the others in magenta. */
  private static Color hurtboxColor(Frame frame, Entity entity) {
    if (!entity.equals(frame.player())) {
      return ENEMY_HURTBOX_COLOR;
    }
    return frame.player().isInvincible() ? INVINCIBLE_COLOR : PLAYER_HURTBOX_COLOR;
  }

  private void drawVelocity(Frame frame) {
    Aabb body = frame.player().getBodyBox();
    float centreX = frame.playerDrawPosition().x + body.width() / 2f;
    float centreY = frame.playerDrawPosition().y + body.height() / 2f;
    shapes.setColor(VELOCITY_COLOR);
    shapes.line(
        centreX,
        centreY,
        centreX + frame.playerVelocity().x * VELOCITY_SCALE,
        centreY + frame.playerVelocity().y * VELOCITY_SCALE);
  }

  /** Draws an oriented box: rotated around its centre by its angle. */
  private void drawBlade(Obb blade, float dx, float dy) {
    shapes.setColor(BLADE_COLOR);
    float halfLength = blade.length() / 2f;
    float halfWidth = blade.width() / 2f;
    shapes.rect(
        blade.centreX() - halfLength + dx,
        blade.centreY() - halfWidth + dy,
        halfLength,
        halfWidth,
        blade.length(),
        blade.width(),
        1f,
        1f,
        blade.angleDegrees());
  }

  private void drawBox(Aabb box) {
    shapes.rect(box.x(), box.y(), box.width(), box.height());
  }

  @Override
  public void dispose() {
    shapes.dispose();
  }
}
