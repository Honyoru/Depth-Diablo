package com.epitech.depthdiablo.engine;

/**
 * Something the R rewind can save and put back (GDD 1.1.2): it writes its own state into a {@link
 * StateBuffer} and reads it back, so no other class sees its private fields. A subclass that adds
 * state calls {@code super} first in both methods, so the parent's values always come first.
 *
 * <p>Pattern: Memento, the originator.
 */
public interface Rewindable {

  /**
   * Writes the state of this object after what the buffer already holds.
   *
   * @param out the photo being taken
   */
  void writeState(StateBuffer out);

  /**
   * Puts this object back in the state it wrote, reading the values in the order {@link
   * #writeState} wrote them.
   *
   * @param in the photo being restored, its read cursors at this object's first value
   * @throws IllegalStateException if the photo holds fewer values than this object reads
   */
  void readState(StateBuffer in);
}
