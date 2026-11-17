package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.epitech.depthdiablo.combat.Damageable;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Entity;

/**
 * The X-ray view (GDD 1.1.2): no sprites, only what the logic sees. Body boxes in grey, hurtboxes
 * in green and the player's velocity as a yellow line. Hitboxes get their own colour once attacks
 * exist in the world.
 */
final class XRayRenderer implements Renderer {
  private static final Color BODY_COLOR = Color.GRAY;
  private static final Color HURTBOX_COLOR = Color.GREEN;
  private static final Color VELOCITY_COLOR = Color.YELLOW;

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
      shapes.setColor(HURTBOX_COLOR);
      drawBox(damageable.getHurtbox().translated(dx, dy));
    }
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

  private void drawBox(Aabb box) {
    shapes.rect(box.x(), box.y(), box.width(), box.height());
  }

  @Override
  public void dispose() {
    shapes.dispose();
  }
}
