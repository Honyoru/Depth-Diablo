package com.epitech.depthdiablo.entity;

/** Something with a body that other bodies can touch (GDD 1.5). */
public interface Collidable {
  /**
   * Returns the box the object occupies in the world
   *
   * @return the bodybox
   */
  Aabb getBodyBox();

  /**
   * Tells if the objects stops movements, like wall, closed door, chest.
   *
   * @return true if nothin can move through it
   */
  boolean blocks();

  /**
   * Called when other entity's bodybox overlaps this one.
   *
   * @param other the entity that touch this one
   */
  void onContact(Entity other);
}
