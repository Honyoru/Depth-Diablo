package com.epitech.depthdiablo.engine;

import java.util.Arrays;
import java.util.Objects;

/**
 * One photo of the game for the R rewind (GDD 1.1.2, Q86): every {@link Rewindable} writes its
 * values here one after another, then reads them back in the same order to go back to that moment.
 * The buffer does not know what a value means, only its place, so {@code readState} must read
 * exactly what {@code writeState} wrote, in the same order.
 *
 * <p>Values live in primitive arrays, one per type, so recording 60 photos per second creates no
 * boxed numbers and no garbage (Q86). The arrays double when they are full and are kept by {@link
 * #clear()}, so a reused photo stops allocating once it is big enough.
 *
 * <p>Pattern: Memento, the stored state ({@link Rewindable} is the originator, {@link
 * com.epitech.depthdiablo.debug.Rewind} the caretaker).
 */
public final class StateBuffer {

  private static final int FIRST_SIZE = 64;

  private int[] ints = new int[FIRST_SIZE];
  private float[] floats = new float[FIRST_SIZE];
  private long[] longs = new long[FIRST_SIZE];
  private Object[] refs = new Object[FIRST_SIZE];

  /** How many values of each type were written. */
  private int intCount;

  private int floatCount;
  private int longCount;
  private int refCount;

  /** Index of the next value of each type to read. */
  private int intRead;

  private int floatRead;
  private int longRead;
  private int refRead;

  /**
   * Forgets everything written and keeps the arrays, so the next photo in this buffer allocates
   * nothing. The references are dropped too, so a dead entity is no longer held here.
   */
  public void clear() {
    Arrays.fill(refs, 0, refCount, null);
    intCount = 0;
    floatCount = 0;
    longCount = 0;
    refCount = 0;
    startReading();
  }

  /** Puts the read cursors back to the first value, before a {@code readState} pass. */
  public void startReading() {
    intRead = 0;
    floatRead = 0;
    longRead = 0;
    refRead = 0;
  }

  /**
   * Writes a whole number after the ones already written.
   *
   * @param value the number to keep
   */
  public void writeInt(int value) {
    if (intCount == ints.length) {
      ints = Arrays.copyOf(ints, ints.length * 2);
    }
    ints[intCount] = value;
    intCount++;
  }

  /**
   * Reads the next whole number, in the order they were written.
   *
   * @return the number
   * @throws IllegalStateException if every written number was already read
   */
  public int readInt() {
    if (intRead == intCount) {
      throw new IllegalStateException(
          "Nothing left to read in this state: read more ints than the " + intCount + " written");
    }
    int value = ints[intRead];
    intRead++;
    return value;
  }

  /**
   * Writes a decimal number after the ones already written, e.g. a position.
   *
   * @param value the number to keep
   */
  public void writeFloat(float value) {
    if (floatCount == floats.length) {
      floats = Arrays.copyOf(floats, floats.length * 2);
    }
    floats[floatCount] = value;
    floatCount++;
  }

  /**
   * Reads the next decimal number, in the order they were written.
   *
   * @return the number
   * @throws IllegalStateException if every written number was already read
   */
  public float readFloat() {
    if (floatRead == floatCount) {
      throw new IllegalStateException(
          "Nothing left to read in this state: read more floats than the "
              + floatCount
              + " written");
    }
    float value = floats[floatRead];
    floatRead++;
    return value;
  }

  /**
   * Writes a long number after the ones already written, e.g. the tick of the world.
   *
   * @param value the number to keep
   */
  public void writeLong(long value) {
    if (longCount == longs.length) {
      longs = Arrays.copyOf(longs, longs.length * 2);
    }
    longs[longCount] = value;
    longCount++;
  }

  /**
   * Reads the next long number, in the order they were written.
   *
   * @return the number
   * @throws IllegalStateException if every written number was already read
   */
  public long readLong() {
    if (longRead == longCount) {
      throw new IllegalStateException(
          "Nothing left to read in this state: read more longs than the " + longCount + " written");
    }
    long value = longs[longRead];
    longRead++;
    return value;
  }

  /**
   * Writes a yes / no value, kept as 1 or 0 with the whole numbers.
   *
   * @param value the value to keep
   */
  public void writeBoolean(boolean value) {
    writeInt(value ? 1 : 0);
  }

  /**
   * Reads the next yes / no value.
   *
   * @return the value
   * @throws IllegalStateException if every written whole number was already read
   */
  public boolean readBoolean() {
    return readInt() != 0;
  }

  /**
   * Writes a reference to an object, kept as the object itself, not a copy: an entity of the world,
   * an enemy's target.
   *
   * @param value the object, or null for "none"
   */
  public void writeRef(Object value) {
    if (refCount == refs.length) {
      refs = Arrays.copyOf(refs, refs.length * 2);
    }
    refs[refCount] = value;
    refCount++;
  }

  /**
   * Reads the next reference, in the order they were written.
   *
   * @param <T> the type the caller expects
   * @param type the class the caller expects, e.g. {@code Character.class}
   * @return the same object that was written, or null if null was written
   * @throws NullPointerException if {@code type} is null
   * @throws IllegalStateException if every written reference was already read
   * @throws ClassCastException if the object is not of the given type
   */
  public <T> T readRef(Class<T> type) {
    Objects.requireNonNull(type, "The type of the reference to read must not be null");
    if (refRead == refCount) {
      throw new IllegalStateException(
          "Nothing left to read in this state: read more references than the "
              + refCount
              + " written");
    }
    Object value = refs[refRead];
    refRead++;
    return type.cast(value);
  }
}
