package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.engine.Rewindable;
import com.epitech.depthdiablo.engine.StateBuffer;
import java.util.Objects;

/**
 * Anything that lives in the world and is updated once per logic tick (GDD 1.3.1).
 *
 * <p>Pattern: Template Method. {@link #update(long)} is final and calls {@link #onUpdate(long)},
 * the step each subclass fills in.
 */
public abstract class Entity implements Updatable, Collidable, Rewindable {

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
   * Returns the box this entity occupies in the world.
   *
   * @return the body box
   */
  @Override
  public final Aabb getBodyBox() {
    return bodyBox;
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
   * @return a new vector, safe to change
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

  /**
   * Writes the part of the R photo every entity has: its position and whether it is removed. The
   * size of the body never changes, so it is not saved. A subclass calls this first.
   *
   * @param out the photo being taken
   */
  @Override
  public void writeState(StateBuffer out) {
    out.writeFloat(bodyBox.x());
    out.writeFloat(bodyBox.y());
    out.writeBoolean(removed);
  }

  /**
   * Puts back the position and the removed flag, so an entity removed since the photo comes back. A
   * subclass calls this first.
   *
   * @param in the photo being restored
   */
  @Override
  public void readState(StateBuffer in) {
    float x = in.readFloat();
    float y = in.readFloat();
    bodyBox = new Aabb(x, y, bodyBox.width(), bodyBox.height());
    removed = in.readBoolean();
  }
}
