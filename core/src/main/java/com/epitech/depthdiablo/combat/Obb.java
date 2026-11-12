package com.epitech.depthdiablo.combat;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.entity.Aabb;
import java.util.Objects;

/**
 * An Obb (Oriented Bounding Box) is a rectangle that can turn: a melee swing's hitbox pointing
 * towards the aim (GDD 1.5). Unlike an Aabb, its sides follow its angle instead of the axes.
 *
 * @param centreX the x-coordinate of the centre of the rectangle
 * @param centreY the y-coordinate of the centre of the rectangle
 * @param angleDegrees the direction its length points to, in degrees; 0 points right, 90 up
 * @param length its size along its direction (a weapon's reach); above 0
 * @param width its size across its direction; above 0
 * @throws IllegalArgumentException if a value is not a finite number, or the length or the width is
 *     0 or less
 */
public record Obb(float centreX, float centreY, float angleDegrees, float length, float width) {

  public Obb {
    requireFinite("Centre x", centreX);
    requireFinite("Centre y", centreY);
    requireFinite("Angle", angleDegrees);
    requireAboveZero("Length", length);
    requireAboveZero("Width", width);
  }

  /**
   * Creates the rectangle that covers exactly the given box, at angle 0: the hitbox of a trap or a
   * contact attack, which never turns.
   *
   * @param box the box to cover
   * @return a rectangle with the same area and position as the box
   * @throws NullPointerException if {@code box} is {@code null}
   * @throws IllegalArgumentException if the box has a width or a height of 0
   */
  public static Obb around(Aabb box) {
    Objects.requireNonNull(box, "box is null");
    return new Obb(
        box.x() + box.width() / 2, box.y() + box.height() / 2, 0, box.width(), box.height());
  }

  /**
   * Returns the 4 corners of this rectangle, counter-clockwise: back-right, front-right,
   * front-left, back-left, seen along its direction. The renderer draws them (F1 X-ray view) and
   * the overlap test projects them.
   *
   * @return a new array of 4 new vectors, safe to change
   */
  public Vector2[] corners() {
    double radians = Math.toRadians(angleDegrees);
    float alongX = (float) Math.cos(radians);
    float alongY = (float) Math.sin(radians);
    float acrossX = -alongY;
    float acrossY = alongX;
    float halfLength = length / 2;
    float halfWidth = width / 2;
    return new Vector2[] {
      corner(alongX, alongY, acrossX, acrossY, -halfLength, -halfWidth),
      corner(alongX, alongY, acrossX, acrossY, halfLength, -halfWidth),
      corner(alongX, alongY, acrossX, acrossY, halfLength, halfWidth),
      corner(alongX, alongY, acrossX, acrossY, -halfLength, halfWidth)
    };
  }

  /**
   * Checks if this rectangle overlaps a box, such as a hurtbox (GDD 1.5). Shapes that only touch on
   * an edge do not overlap, as with {@link Aabb#overlaps}.
   *
   * <p>Separating Axis Theorem: two convex shapes do not overlap if their shadows on one axis do
   * not overlap. For a rectangle against a box, the 4 axes to try are x, y and this rectangle's two
   * sides.
   *
   * @param other the box to check
   * @return true if they share some area
   * @throws NullPointerException if {@code other} is {@code null}
   */
  public boolean overlaps(Aabb other) {
    Objects.requireNonNull(other, "other is null");
    Vector2[] mine = corners();
    Vector2[] theirs = {
      new Vector2(other.x(), other.y()),
      new Vector2(other.x() + other.width(), other.y()),
      new Vector2(other.x() + other.width(), other.y() + other.height()),
      new Vector2(other.x(), other.y() + other.height())
    };
    Vector2[] axes = {Vector2.X, Vector2.Y, mine[1].cpy().sub(mine[0]), mine[3].cpy().sub(mine[0])};
    for (Vector2 axis : axes) {
      if (separatedOn(axis, mine, theirs)) {
        return false;
      }
    }
    return true;
  }

  private static boolean separatedOn(Vector2 axis, Vector2[] first, Vector2[] second) {
    float firstMin = Float.POSITIVE_INFINITY;
    float firstMax = Float.NEGATIVE_INFINITY;
    for (Vector2 point : first) {
      float shadow = axis.dot(point);
      firstMin = Math.min(firstMin, shadow);
      firstMax = Math.max(firstMax, shadow);
    }
    float secondMin = Float.POSITIVE_INFINITY;
    float secondMax = Float.NEGATIVE_INFINITY;
    for (Vector2 point : second) {
      float shadow = axis.dot(point);
      secondMin = Math.min(secondMin, shadow);
      secondMax = Math.max(secondMax, shadow);
    }
    return firstMax <= secondMin || secondMax <= firstMin;
  }

  private Vector2 corner(
      float alongX, float alongY, float acrossX, float acrossY, float along, float across) {
    return new Vector2(
        centreX + alongX * along + acrossX * across, centreY + alongY * along + acrossY * across);
  }

  private static void requireFinite(String name, float value) {
    if (Float.isNaN(value) || Float.isInfinite(value)) {
      throw new IllegalArgumentException(name + " must be a finite number: " + value);
    }
  }

  private static void requireAboveZero(String name, float value) {
    requireFinite(name, value);
    if (value <= 0) {
      throw new IllegalArgumentException(name + " must be above 0: " + value);
    }
  }
}
