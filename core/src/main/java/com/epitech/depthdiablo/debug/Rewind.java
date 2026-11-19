package com.epitech.depthdiablo.debug;

import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.engine.Rewindable;
import com.epitech.depthdiablo.engine.StateBuffer;
import java.util.Objects;

/**
 * The R debug feature: holding R plays the last seconds backwards, like a video (GDD 1.1.2). It
 * takes a photo of the world after every tick and, while R is held, puts the previous photo back on
 * every tick instead of moving forward.
 *
 * <p>The photos live in a circular buffer created once (Q86): when it is full, the next photo
 * overwrites the oldest one, so nothing is allocated during the game. After a rewind, the new
 * photos overwrite the ones that were rewound past.
 *
 * <p>Pattern: Memento, the caretaker ({@link Rewindable} is the originator, {@link StateBuffer} the
 * memento).
 */
public final class Rewind implements DebugFeature {

  /** 5 seconds of photos at 60 ticks per second: 300 (Q86). */
  public static final int CAPACITY = 5 * GameConstants.TICKS_PER_SECOND;

  private final Rewindable world;

  /** The photos, used in a circle: after the last box comes the first one again. */
  private final StateBuffer[] frames;

  /** The box of the newest photo, -1 while there is none. */
  private int newest = -1;

  /** How many photos are kept, at most the capacity. */
  private int count;

  private boolean enabled = true;

  /**
   * Creates the feature, enabled, with every photo allocated now.
   *
   * @param world what is saved and put back, the {@link com.epitech.depthdiablo.engine.World}
   * @param capacity how many photos are kept, e.g. {@link #CAPACITY}
   * @throws NullPointerException if {@code world} is null
   * @throws IllegalArgumentException if {@code capacity} is below 2: going back needs the present
   *     and one older photo
   */
  public Rewind(Rewindable world, int capacity) {
    this.world = Objects.requireNonNull(world, "The world to rewind must not be null");
    if (capacity < 2) {
      throw new IllegalArgumentException(
          "A rewind needs room for 2 photos at least, was " + capacity);
    }
    frames = new StateBuffer[capacity];
    for (int i = 0; i < capacity; i++) {
      frames[i] = new StateBuffer();
    }
  }

  @Override
  public String getName() {
    return "Rewind";
  }

  @Override
  public void enable() {
    enabled = true;
  }

  /** Switches R off and forgets every photo: once on again, it starts from the present. */
  @Override
  public void disable() {
    enabled = false;
    count = 0;
    newest = -1;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Takes a photo of the world, after a normal tick. When the buffer is full, it overwrites the
   * oldest photo.
   *
   * @param tick the number of the tick that just ran
   */
  @Override
  public void onTick(long tick) {
    newest = (newest + 1) % frames.length;
    StateBuffer frame = frames[newest];
    frame.clear();
    world.writeState(frame);
    if (count < frames.length) {
      count++;
    }
  }

  /**
   * Goes back one tick: drops the newest photo, which is the present, and puts the one before it
   * back into the world. Called once per tick while R is held.
   *
   * @return true if the world went back; false if the feature is off or no older photo is left
   */
  public boolean stepBack() {
    if (!enabled || count < 2) {
      return false;
    }
    count--;
    newest = (newest - 1 + frames.length) % frames.length; // + length: Java's % can be negative
    StateBuffer frame = frames[newest];
    frame.startReading();
    world.readState(frame);
    return true;
  }

  /**
   * Returns how many photos are kept.
   *
   * @return from 0 to the capacity
   */
  public int getFrameCount() {
    return count;
  }
}
