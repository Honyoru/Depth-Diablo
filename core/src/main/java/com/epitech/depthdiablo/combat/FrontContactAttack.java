package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.geometry.Obb;

/**
 * A contact attack that hits in front of the owner, towards the aim (GDD 1.3.1): its area starts at
 * the centre of the body and goes as far as the reach past the body's edge, whatever the angle. It
 * is as wide as the body.
 */
public final class FrontContactAttack extends ContactAttack {

  /**
   * Creates an attack that hits in front.
   *
   * @param reach how far the attack goes past the owner's body box, in pixels; 0 means touching
   * @throws IllegalArgumentException if {@code reach} is negative or not a finite number
   */
  public FrontContactAttack(float reach) {
    super(reach);
  }

  /**
   * Builds the area in front of the body. The body's half-size towards the aim is {@code |cos| x
   * width / 2 + |sin| x height / 2}, so the reach past the edge is the same at 0, 45 or 90 degrees.
   */
  @Override
  protected Obb area(Aabb body, float aimDegrees) {
    double radians = Math.toRadians(aimDegrees);
    float cos = (float) Math.cos(radians);
    float sin = (float) Math.sin(radians);
    float halfExtent = Math.abs(cos) * body.width() / 2 + Math.abs(sin) * body.height() / 2;
    float length = halfExtent + getReach();
    float centreX = body.x() + body.width() / 2 + cos * length / 2;
    float centreY = body.y() + body.height() / 2 + sin * length / 2;
    return new Obb(centreX, centreY, aimDegrees, length, body.width());
  }
}
