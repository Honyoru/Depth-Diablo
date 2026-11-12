package com.epitech.depthdiablo.combat;

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
