package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.epitech.depthdiablo.debug.EventLaser;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.GameEvent;
import java.util.Objects;

/**
 * Draws the F2 event laser lines over any view (GDD 1.1.2): one colour per event type, fading out
 * as the line gets old. An event without a receiver is drawn as a ring around its emitter.
 */
final class EventLaserOverlay implements Disposable {
  private static final Color DAMAGE_COLOR = Color.RED;
  private static final Color DEATH_COLOR = Color.MAGENTA;

  /** Radius of the ring drawn for an event without a receiver, in pixels. */
  private static final float RING_RADIUS = 10f;

  private final EventLaser laser;
  private final ShapeRenderer shapes = new ShapeRenderer();
  private final Color color = new Color();

  /**
   * Creates the overlay for one laser.
   *
   * @param laser the debug feature that holds the lines
   */
  EventLaserOverlay(EventLaser laser) {
    this.laser = Objects.requireNonNull(laser, "The event laser must not be null");
  }

  /**
   * Draws the lines on screen, if any.
   *
   * @param camera the world camera
   */
  void render(OrthographicCamera camera) {
    if (laser.lines().isEmpty()) {
      return;
    }
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    shapes.setProjectionMatrix(camera.combined);
    shapes.begin(ShapeRenderer.ShapeType.Line);
    for (EventLaser.Line line : laser.lines()) {
      color.set(colorOf(line.event()));
      color.a = (float) line.ticksLeft() / EventLaser.LINE_LIFETIME_TICKS;
      shapes.setColor(color);
      if (line.hasReceiver()) {
        shapes.line(line.fromX(), line.fromY(), line.toX(), line.toY());
      } else {
        shapes.circle(line.fromX(), line.fromY(), RING_RADIUS);
      }
    }
    shapes.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  /** No default branch: a new event type stops this compiling until it gets a colour. */
  private static Color colorOf(GameEvent event) {
    return switch (event) {
      case DamageDealt damage -> DAMAGE_COLOR;
      case EntityDied death -> DEATH_COLOR;
    };
  }

  @Override
  public void dispose() {
    shapes.dispose();
  }
}
