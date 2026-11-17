package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Aabb;

/**
 * A contact attack that hits all around the owner (GDD 1.3.1): its area is the owner's body box
 * grown by the reach on every side, as an {@link Obb} at angle 0 (DECISIONS §5). The aim is not
 * used.
 */
public final class AroundContactAttack extends ContactAttack {

  /**
   * Creates an attack that hits all around.
   *
   * @param reach how far the attack goes past the owner's body box, in pixels; 0 means touching
   * @throws IllegalArgumentException if {@code reach} is negative or not a finite number
   */
  public AroundContactAttack(float reach) {
    super(reach);
  }

  @Override
  protected Obb area(Aabb body, float aimDegrees) {
    return Obb.around(
        new Aabb(
            body.x() - reach(),
            body.y() - reach(),
            body.width() + 2 * reach(),
            body.height() + 2 * reach()));
  }
}
