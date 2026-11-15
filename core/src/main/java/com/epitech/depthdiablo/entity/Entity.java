package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import java.util.Objects;

/**
 * Anything that lives in the world and is updated once per logic tick (GDD 1.3.1).
 *
 * <p>Pattern: Template Method. {@link #update(long)} is final and calls {@link #onUpdate(long)},
 * the step each subclass fills in.
 */
public abstract class Entity implements Updatable, Collidable {
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
    this.bodyBox = Objects.requireNonNull(bodyBox, "The body box of an entity must not be null");
  }

  /**
   * Advances this entity by one logic tick. Does nothing once the entity is removed.
   *
   * @param tick the number of the current logic tick
   */
  @Override
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
  @Override
  public final Aabb bodyBox() {
    return bodyBox;
  }

  /**
   * Tells if entity stops movement. False by default; walls, closed doors an chests return true.
   *
   * @return true if nothing can move throught is entity
   */
  @Override
  public boolean blocks() {
    return false;
  }

  /**
   * Called when other entity's bodybox ovrelaps this one. Does nothing by default; trapsand pickups
   * override it.
   *
   * @param other the enity that touch this one
   * @throws NullPointerException if other is null
   */
  @Override
  public void onContact(Entity other) {
    Objects.requireNonNull(other, "The entity touched must not be null");
  }

  /**
   * Returns the position of this entity: bottom-left corner of its bodybox.
   *
   * @return a new vetor, safe to change
   */
  public final Vector2 position() {
    return new Vector2(bodyBox.x(), bodyBox.y());
  }

  /**
   * Moves this entity so that the bottom-left corner its bodybox is at the given position. The size
   * of the boxe does not change.
   *
   * @param position the new bottom-left corner
   * @throws NullPointerException if positionis null
   */
  protected final void moveTo(Vector2 position) {
    Objects.requireNonNull(position, "The new position of an entity must not be null");
    bodyBox = new Aabb(position.x, position.y, bodyBox.width(), bodyBox.height());
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
