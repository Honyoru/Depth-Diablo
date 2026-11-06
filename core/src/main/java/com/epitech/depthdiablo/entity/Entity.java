package com.epitech.depthdiablo.entity;

import java.util.Objects;

/**
 * Anything that lives in the world and is updated once per logic tick (GDD 1.3.1).
 *
 * <p>Pattern: Template Method. {@link #update(long)} is final and calls {@link #onUpdate(long)},
 * the step each subclass fills in.
 */
public abstract class Entity {
  private Aabb bodyBox;
  private boolean removed;

  /**
   * Creates an entity occupying the given box.
   *
   * @param bodyBox the box the entity occupies in the world; its bottom-left corner is the entity's
   *     position
   * @throws NullPointerException if the box is null
   */
  protected Entity(Aabb bodyBox) {
    this.bodyBox = Objects.requireNonNull(bodyBox, "bodyBox");
  }

  /**
   * Advances this entity by one logic tick. Does nothing once the entity is removed.
   *
   * @param tick the number of the current logic tick
   */
  public final void update(long tick) {
    if (removed) {
      return;
    }
    onUpdate(tick);
  }

  /**
   * The step each subclass fills in, called once per tick by {@link #update(long)}.
   *
   * @param tick the number of the current logic tick
   */
  protected abstract void onUpdate(long tick);

  /**
   * Returns the box this entity occupies in the world.
   *
   * @return the body box
   */
  public final Aabb bodyBox() {
    return bodyBox;
  }

  /** Marks this entity for removal. The world drops it at the end of the tick. */
  public final void remove() {
    removed = true;
  }

  /**
   * Tells whether this entity was removed.
   *
   * @return true once {@link #remove()} was called
   */
  public final boolean isRemoved() {
    return removed;
  }
}
