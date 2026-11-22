package com.epitech.depthdiablo.engine;

import java.util.List;
import java.util.Objects;

/**
 * Several {@link Rewindable} saved as one photo (GDD 1.1.2, R): the world and what runs beside it,
 * such as the Arena cycle and its wave director. Each part writes its state after the previous one
 * and reads it back in the same order.
 *
 * <p>Pattern: Composite (a group is a {@link Rewindable} made of {@link Rewindable}s).
 */
public final class RewindGroup implements Rewindable {
  private final List<Rewindable> parts;

  /**
   * Creates a group of the given parts, saved in this order.
   *
   * @param parts the objects to save together; at least one
   * @throws NullPointerException if {@code parts} or one of them is {@code null}
   * @throws IllegalArgumentException if there is no part
   */
  public RewindGroup(Rewindable... parts) {
    this.parts = List.of(Objects.requireNonNull(parts, "parts is null"));
    if (this.parts.isEmpty()) {
      throw new IllegalArgumentException("A rewind group needs at least one part");
    }
  }

  /**
   * Writes every part, in order.
   *
   * @param out the photo being taken
   */
  @Override
  public void writeState(StateBuffer out) {
    for (Rewindable part : parts) {
      part.writeState(out);
    }
  }

  /**
   * Reads every part back, in the order they were written.
   *
   * @param in the photo being restored
   */
  @Override
  public void readState(StateBuffer in) {
    for (Rewindable part : parts) {
      part.readState(in);
    }
  }
}
