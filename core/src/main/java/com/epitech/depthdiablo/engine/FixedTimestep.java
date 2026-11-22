package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.GameConstants;

/** Turns variable frame times into a whole number of fixed logic ticks (Q42). */
public final class FixedTimestep {

  /** Most ticks one frame may run, so a long stall cannot snowball into a freeze. */
  public static final int MAX_TICKS_PER_FRAME = 15;

  private static final double TICK_SECONDS = 1.0 / GameConstants.TICKS_PER_SECOND;

  private double accumulator;

  /** Creates a timestep with no time accumulated yet. */
  public FixedTimestep() {
    // its fields start with their declared values
  }

  /**
   * Returns how far the current frame is between the last tick and the next one. The renderer uses
   * it to draw moving things between their previous and current positions.
   *
   * @return from 0 (a tick just ran) to just below 1 (the next tick is about to run)
   */
  public float getAlpha() {
    return (float) (accumulator / TICK_SECONDS);
  }

  /**
   * Adds the time of one rendered frame and returns how many logic ticks to run now.
   *
   * @param deltaSeconds time since the previous frame, never negative
   * @return the number of ticks to run, from 0 to {@link #MAX_TICKS_PER_FRAME}
   * @throws IllegalArgumentException if the delta is negative or not a number
   */
  public int advance(double deltaSeconds) {
    if (Double.isNaN(deltaSeconds) || deltaSeconds < 0) {
      throw new IllegalArgumentException("deltaSeconds must be >= 0, was " + deltaSeconds);
    }
    accumulator += deltaSeconds;
    int ticks = 0;
    while (accumulator >= TICK_SECONDS && ticks < MAX_TICKS_PER_FRAME) {
      accumulator -= TICK_SECONDS;
      ticks++;
    }
    if (ticks == MAX_TICKS_PER_FRAME) {
      accumulator = 0;
    }
    return ticks;
  }
}
