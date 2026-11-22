package com.epitech.depthdiablo.entity.projectile;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Character;
import com.epitech.depthdiablo.geometry.Aabb;

/**
 * The shot of a bow: the Archer's and the Skeleton Archer's (GDD 1.3.1). It stops at the first
 * character it hurts.
 */
public final class Arrow extends Projectile {
  /**
   * Creates an arrow. Its length along its flight is the width of its body box.
   *
   * @param bodyBox where it starts; must not be {@code null}
   * @param owner the character who shot it
   * @param aimDegrees the direction it flies to, in degrees (0 = right, 90 = up)
   * @param speed how fast it flies, in pixels per second; above 0
   * @param lifetimeTicks how many ticks it flies before it is removed; above 0
   * @param spriteId the picture the renderer draws for it, pointing right
   * @param world the world it flies in
   * @throws NullPointerException if {@code bodyBox}, {@code owner}, {@code spriteId} or {@code
   *     world} is {@code null}
   * @throws IllegalArgumentException if the aim or the speed is not a finite number, the speed is 0
   *     or less, the lifetime is 0 or less, or the sprite id is blank
   */
  public Arrow(
      Aabb bodyBox,
      Character owner,
      float aimDegrees,
      float speed,
      int lifetimeTicks,
      String spriteId,
      World world) {
    super(bodyBox, owner, aimDegrees, speed, lifetimeTicks, spriteId, world);
  }

  /**
   * Stops the arrow: it does not go through the character it hurt.
   *
   * @param target the character it just hurt
   */
  @Override
  protected void onHit(Character target) {
    remove();
  }
}
