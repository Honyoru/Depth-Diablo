package com.epitech.depthdiablo.entity;

/**
 * A solid tile of the map (GDD 1.5). It never moves and nothing can walk through it: movers ask the
 * world whether a box is free, and a wall makes it taken.
 */
public class Wall extends Entity {

  /**
   * Creates a wall occupying the given box.
   *
   * @param box the box the wall occupies in the world, usually one tile
   * @throws NullPointerException if the box is null
   */
  public Wall(Aabb box) {
    super(box);
  }

  /**
   * Tells that a wall stops movement.
   *
   * @return always true
   */
  @Override
  public boolean blocks() {
    return true;
  }

  /**
   * Does nothing: a wall does not change over time.
   *
   * @param tick the number of the current logic tick
   */
  @Override
  public void onUpdate(long tick) {
    // A wall does nothing over time.
  }
}
