package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.geometry.Aabb;

/**
 * The high-contrast view (GDD 1.1.2, also an accessibility view): flat shapes on the dark
 * background. Walls in white, the player in yellow, anything else in cyan.
 */
final class HighContrastRenderer implements Renderer {
  private static final Color WALL_COLOR = Color.WHITE;
  private static final Color PLAYER_COLOR = Color.YELLOW;
  private static final Color OTHER_COLOR = Color.CYAN;

  private final ShapeRenderer shapes = new ShapeRenderer();

  @Override
  public void render(Frame frame) {
    shapes.setProjectionMatrix(frame.camera().combined);
    shapes.begin(ShapeRenderer.ShapeType.Filled);
    for (Entity entity : frame.world().getEntities()) {
      if (entity.equals(frame.player())) {
        continue; // drawn last, on top of everything
      }
      shapes.setColor(entity.blocks() ? WALL_COLOR : OTHER_COLOR);
      drawBox(entity.getBodyBox());
    }
    Aabb body = frame.player().getBodyBox();
    shapes.setColor(PLAYER_COLOR);
    shapes.rect(
        frame.playerDrawPosition().x, frame.playerDrawPosition().y, body.width(), body.height());
    shapes.end();
  }

  private void drawBox(Aabb box) {
    shapes.rect(box.x(), box.y(), box.width(), box.height());
  }

  @Override
  public void dispose() {
    shapes.dispose();
  }
}
