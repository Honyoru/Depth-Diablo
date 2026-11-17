package com.epitech.depthdiablo.debug;

import com.epitech.depthdiablo.engine.EventListener;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The F2 debug feature (GDD 1.1.2): turns every event on the bus into a short-lived line from its
 * emitter to its receiver, so the decoupled objects can be seen talking. It is just one more
 * listener: the engine does not know it exists. Pattern: Observer.
 */
public final class EventLaser implements DebugFeature, EventListener {

  /** How long a line stays on screen: half a second. */
  public static final int LINE_LIFETIME_TICKS = 30;

  /**
   * One line on screen, fixed where the event happened.
   *
   * @param event the event the line shows
   * @param fromX x of the centre of the emitter's body box
   * @param fromY y of the centre of the emitter's body box
   * @param toX x of the centre of the receiver's body box, or {@code fromX} without a receiver
   * @param toY y of the centre of the receiver's body box, or {@code fromY} without a receiver
   * @param ticksLeft ticks before the line disappears
   */
  public record Line(
      GameEvent event, float fromX, float fromY, float toX, float toY, int ticksLeft) {

    /**
     * Tells whether the event has a receiver. Without one, the line is a single point.
     *
     * @return true if the line goes from the emitter to a receiver
     */
    public boolean hasReceiver() {
      return event.receiver().isPresent();
    }

    private Line withTicksLeft(int ticks) {
      return new Line(event, fromX, fromY, toX, toY, ticks);
    }
  }

  private final List<Line> lines = new ArrayList<>();

  private boolean enabled;

  @Override
  public String getName() {
    return "Event laser";
  }

  /**
   * Returns the lines to draw, oldest first.
   *
   * @return a read-only view of the lines on screen
   */
  public List<Line> getLines() {
    return Collections.unmodifiableList(lines);
  }

  @Override
  public void enable() {
    enabled = true;
  }

  @Override
  public void disable() {
    enabled = false;
    lines.clear();
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void onTick(long tick) {
    lines.replaceAll(line -> line.withTicksLeft(line.ticksLeft() - 1));
    lines.removeIf(line -> line.ticksLeft() <= 0);
  }

  @Override
  public void onEvent(GameEvent event) {
    if (!enabled) {
      return;
    }
    Aabb from = event.source().getBodyBox();
    Aabb to = event.receiver().map(Entity::getBodyBox).orElse(from);
    lines.add(
        new Line(
            event, centreX(from), centreY(from), centreX(to), centreY(to), LINE_LIFETIME_TICKS));
  }

  private static float centreX(Aabb box) {
    return box.x() + box.width() / 2f;
  }

  private static float centreY(Aabb box) {
    return box.y() + box.height() / 2f;
  }
}
