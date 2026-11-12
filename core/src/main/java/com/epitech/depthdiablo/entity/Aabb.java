package com.epitech.depthdiablo.entity;

/**
 * An Aabb (Axis-Aligned Bounding Box) is a rectangle aligned with the coordinate axes, used for
 * collision detection.
 *
 * @param x The x-coordinate of the bottom-left corner of the Aabb.
 * @param y The y-coordinate of the bottom-left corner of the Aabb.
 * @param width The width of the Aabb.
 * @param height The height of the Aabb.
 */
public record Aabb(float x, float y, float width, float height) {

  /**
   * Creates a box and checks its size.
   *
   * @throws IllegalArgumentException if the width or the height is negative
   */
  public Aabb {
    if (width < 0 || height < 0) {
      throw new IllegalArgumentException(
          "Width and height must be non-negative: width=" + width + ", height=" + height);
    }
  }

  /**
   * Checks if this Aabb overlaps with another Aabb. Boxes that only touch on an edge do not
   * overlap.
   *
   * @param other The other Aabb to check for overlap with this Aabb.
   * @return true if this Aabb overlaps with the other Aabb, false otherwise.
   */
  public boolean overlaps(Aabb other) {
    return overlapsOnAxis(this.x, this.width, other.x, other.width)
        && overlapsOnAxis(this.y, this.height, other.y, other.height);
  }

  private static boolean overlapsOnAxis(float start1, float size1, float start2, float size2) {
    return start1 + size1 > start2 && start2 + size2 > start1;
  }
}
