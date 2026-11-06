package com.epitech.depthdiablo.entity;

import java.util.Objects;

/**
 * Anything that lives in the world and is updated once per logic tick (GDD
 * 1.3.1).
 * <p>
 * Pattern: Template Method.{@link #update(long)} is final and calls
 * {@link #onUpdate(long)},
 * the step each subclass fills in.
 */
public abstract class Entity {
  private Aabb bodyBox;
  private boolean removed;

  /**
   * Creates the entity with the given body.
   *
   * @param bodyBox the body of the entity, its botom left corner is the position
   *                of the entity's position
   * @throws NullPointerException if bodyBox is null
   */
  protected Entity(Aabb bodyBox) {
    this.bodyBox = Objects.requireNonNull(bodyBox, "bodyBox");
  }

  /**
   * Advances this entity by one logic tick. Does nothing once the entity is
   * removed.
   *
   * @param tick the current tick
   */
  public final void update(long tick) {
    if (removed) {
      return;
    }
    onUpdate(tick);
  }

  /**
   * The step is subclass fills in, called once per tick by {@link #update(long)}.
   *
   * @param tick the number of the logic tick
   */
  protected abstract void onUpdate(long tick);

  /**
   * Returns the box this entity occupies in the World.
   *
   * @return the body box
   */
  public final Aabb bodyBox() {
    return bodyBox;
  }

  /**
   * Marks this entity as removed,
   * the world drops it at the end of the current tick.
   */
  public final void remove() {
    removed = true;
  }

  /**
   * Tells whether this entity is removed.
   *
   * @return true once {@link #remove()} has been called
   */
  public final boolean isRemoved() {
    return removed;
  }
}
