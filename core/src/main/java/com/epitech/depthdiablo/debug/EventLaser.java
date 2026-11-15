package com.epitech.depthdiablo.debug;

import com.epitech.depthdiablo.engine.EventListener;
import com.epitech.depthdiablo.engine.GameEvent;
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
      throw new UnsupportedOperationException("hasReceiver is not implemented yet");
    }
  }

  @Override
  public String name() {
    throw new UnsupportedOperationException("name is not implemented yet");
  }

  @Override
  public void enable() {
    throw new UnsupportedOperationException("enable is not implemented yet");
  }

  @Override
  public void disable() {
    throw new UnsupportedOperationException("disable is not implemented yet");
  }

  @Override
  public boolean isEnabled() {
    throw new UnsupportedOperationException("isEnabled is not implemented yet");
  }

  @Override
  public void onTick(long tick) {
    throw new UnsupportedOperationException("onTick is not implemented yet");
  }

  @Override
  public void onEvent(GameEvent event) {
    throw new UnsupportedOperationException("onEvent is not implemented yet");
  }

  /**
   * Returns the lines to draw, oldest first.
   *
   * @return a read-only view of the lines on screen
   */
  public List<Line> lines() {
    throw new UnsupportedOperationException("lines is not implemented yet");
  }
}
