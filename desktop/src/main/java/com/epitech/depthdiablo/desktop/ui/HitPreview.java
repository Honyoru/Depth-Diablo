package com.epitech.depthdiablo.desktop.ui;

import com.epitech.depthdiablo.item.MeleeWeapon;
import com.epitech.depthdiablo.item.RangedWeapon;

/**
 * The shapes that show the player where its weapon hurts (GDD 1.5): the reach of a melee weapon
 * around the aim, the part of it a swing has swept, and how far a ranged weapon shoots. Pure
 * geometry; the {@code HitPreviewView} draws it.
 */
final class HitPreview {
  private HitPreview() {}

  /**
   * A slice of a disc, as {@code ShapeRenderer.arc} draws it.
   *
   * @param centreX the centre of the disc, across
   * @param centreY the centre of the disc, up
   * @param radius its radius, in pixels
   * @param startDegrees where the slice starts (0 = right, 90 = up)
   * @param degrees how wide it is, counter-clockwise
   */
  record Fan(float centreX, float centreY, float radius, float startDegrees, float degrees) {}

  /**
   * A straight line from one point to another.
   *
   * @param fromX where it starts, across
   * @param fromY where it starts, up
   * @param toX where it ends, across
   * @param toY where it ends, up
   */
  record Ray(float fromX, float fromY, float toX, float toY) {}

  /**
   * Returns the whole area a melee weapon sweeps around an aim: aim ± arc / 2, out to its range.
   *
   * @param weapon the melee weapon
   * @param centreX the centre of its holder, across
   * @param centreY the centre of its holder, up
   * @param aimDegrees where it aims (0 = right, 90 = up)
   * @return the slice it reaches
   */
  static Fan reachOf(MeleeWeapon weapon, float centreX, float centreY, float aimDegrees) {
    float arc = weapon.getArcDegrees();
    return new Fan(centreX, centreY, weapon.getRange(), aimDegrees - arc / 2f, arc);
  }

  /**
   * Returns the part a swing has swept so far: from aim - arc / 2 to where the blade is now, as in
   * {@link MeleeWeapon#swingShape}.
   *
   * @param weapon the melee weapon that swings
   * @param centreX the centre of its holder, across
   * @param centreY the centre of its holder, up
   * @param aimDegrees the aim of the swing
   * @param progress how far the swing went, from 0 to 1
   * @return the slice swept so far
   */
  static Fan sweptBy(
      MeleeWeapon weapon, float centreX, float centreY, float aimDegrees, float progress) {
    float arc = weapon.getArcDegrees();
    return new Fan(centreX, centreY, weapon.getRange(), aimDegrees - arc / 2f, arc * progress);
  }

  /**
   * Returns how far a ranged weapon shoots towards an aim.
   *
   * @param weapon the ranged weapon
   * @param fromX the centre of its holder, across
   * @param fromY the centre of its holder, up
   * @param aimDegrees where it aims (0 = right, 90 = up)
   * @return the line its projectile flies along, as long as its range
   */
  static Ray rangeOf(RangedWeapon weapon, float fromX, float fromY, float aimDegrees) {
    double radians = Math.toRadians(aimDegrees);
    return new Ray(
        fromX,
        fromY,
        fromX + (float) Math.cos(radians) * weapon.getRange(),
        fromY + (float) Math.sin(radians) * weapon.getRange());
  }
}
