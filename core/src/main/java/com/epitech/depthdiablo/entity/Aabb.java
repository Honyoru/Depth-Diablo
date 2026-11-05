package com.epitech.depthdiablo.entity;

/**
 * Axis-aligned bounding box, used for body boxes, hurtboxes and hitboxes (GDD 1.5).
 *
 * @param x left edge
 * @param y bottom edge
 * @param width width, never negative
 * @param height height, never negative
 */
public record Aabb(float x, float y, float width, float height) {
  /**
   * Checks the size.
   *
   * @throws IllegalArgumentException if the width or the height is negative
   */
  public Aabb {
    if (width < 0 || height < 0) {
      throw new IllegalArgumentException("Size must not be negative: " + width + " x " + height);
    }
  }

  /**
   * Tells whether two boxes share some area. Boxes that only touch on an edge do not overlap.
   *
   * @param other the other box
   * @return true if the boxes overlap
   */
  public boolean overlaps(Aabb other) {
    return x < other.x + other.width
        && other.x < x + width
        && y < other.y + other.height
        && other.y < y + height;
  }
}
