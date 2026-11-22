package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.epitech.depthdiablo.desktop.ui.HitPreview.Fan;
import com.epitech.depthdiablo.desktop.ui.HitPreview.Ray;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Player;
import com.epitech.depthdiablo.item.MeleeWeapon;
import com.epitech.depthdiablo.item.RangedWeapon;
import com.epitech.depthdiablo.item.Weapon;

/**
 * Draws under the sprites where the player's weapon hurts (GDD 1.5). A melee weapon shows only the
 * part a swing has swept; a ranged weapon shows a dashed line as long as its range, very faint all
 * the time and a bit stronger while the attack button is held.
 */
final class HitPreviewView implements Disposable {
  /** Alpha of the ranged line while the player does not attack. */
  static final float IDLE_ALPHA = 0.12f;

  /** Alpha of the ranged line while the attack button is held. */
  static final float AIMING_ALPHA = 0.25f;

  /** Alpha of the part a swing has swept. */
  static final float SWEPT_ALPHA = 0.35f;

  /** Length of one dash of the ranged line, in pixels. */
  private static final float DASH = 6f;

  /** Gap between two dashes, in pixels. */
  private static final float GAP = 4f;

  /** Thickness of the ranged line, in pixels. */
  private static final float LINE_WIDTH = 1.5f;

  /** One segment of a drawn arc every this many degrees. */
  private static final float DEGREES_PER_SEGMENT = 6f;

  private final ShapeRenderer shapes = new ShapeRenderer();

  /**
   * Draws the reach of the player's weapon towards the aim, and the part a running swing swept.
   *
   * @param frame what to draw: the player, where it aims and whether it attacks
   */
  void draw(Frame frame) {
    Player player = frame.player();
    if (player.isDead()) {
      return;
    }
    Aabb body = player.getBodyBox();
    float centreX = frame.playerDrawPosition().x + body.width() / 2f;
    float centreY = frame.playerDrawPosition().y + body.height() / 2f;
    float aim = new Vector2(frame.aimPoint()).sub(centreX, centreY).angleDeg();
    float alpha = frame.attacking() ? AIMING_ALPHA : IDLE_ALPHA;

    Gdx.gl.glEnable(GL20.GL_BLEND);
    shapes.setProjectionMatrix(frame.camera().combined);
    shapes.begin(ShapeRenderer.ShapeType.Filled);
    Weapon weapon = player.getInventory().getWeapon();
    if (weapon instanceof MeleeWeapon melee) {
      player
          .getCurrentSwing()
          .ifPresent(swing ->
              fill(
                  HitPreview.sweptBy(
                      melee, centreX, centreY, swing.getAimDegrees(), swing.getProgress()),
                  SWEPT_ALPHA));
    } else if (weapon instanceof RangedWeapon ranged) {
      dashed(HitPreview.rangeOf(ranged, centreX, centreY, aim), alpha);
    }
    shapes.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  @Override
  public void dispose() {
    shapes.dispose();
  }

  /** Fills a slice of disc in white at the given alpha. */
  private void fill(Fan fan, float alpha) {
    if (fan.degrees() <= 0f) {
      return; // a swing that has not started yet sweeps nothing
    }
    shapes.setColor(1f, 1f, 1f, alpha);
    int segments = Math.max(8, Math.round(fan.degrees() / DEGREES_PER_SEGMENT));
    shapes.arc(
        fan.centreX(), fan.centreY(), fan.radius(), fan.startDegrees(), fan.degrees(), segments);
  }

  /** Draws a line as dashes of {@link #DASH} pixels with gaps of {@link #GAP}. */
  private void dashed(Ray ray, float alpha) {
    shapes.setColor(1f, 1f, 1f, alpha);
    Vector2 from = new Vector2(ray.fromX(), ray.fromY());
    Vector2 along = new Vector2(ray.toX(), ray.toY()).sub(from);
    float length = along.len();
    along.nor();
    int dashes = (int) Math.ceil(length / (DASH + GAP));
    for (int i = 0; i < dashes; i++) {
      float start = i * (DASH + GAP);
      float end = Math.min(start + DASH, length);
      shapes.rectLine(
          from.x + along.x * start,
          from.y + along.y * start,
          from.x + along.x * end,
          from.y + along.y * end,
          LINE_WIDTH);
    }
  }
}
