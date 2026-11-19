package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.HitResult;
import com.epitech.depthdiablo.combat.Hitbox;
import com.epitech.depthdiablo.combat.Obb;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;
import java.util.Optional;

/**
 * A shot that flies straight until a wall stops it or its lifetime ends: an arrow, a fireball (GDD
 * 1.3.1). Its hitbox follows it and points where it flies (GDD 1.5). It hurts through its owner's
 * attack, so the owner's decorators, such as a FireTouch, apply to it (GDD 1.4).
 *
 * <p>Pattern: Template Method ({@link #onHit} is the hook each projectile fills).
 */

public abstract class Projectile extends Entity {
  private final Character owner;
  private final Vector2 velocity;
  private final Hitbox hitbox;
  private final World world;

  /**
   * Creates a projectile. Its length along its flight is the width of its body box, its thickness
   * the height.
   *
   * @param bodyBox where it starts; must not be {@code null}
   * @param owner the character who shot it; its attack and side are the projectile's
   * @param aimDegrees the direction it flies to, in degrees (0 = right, 90 = up)
   * @param speed how fast it flies, in pixels per second; above 0
   * @param lifetimeTicks how many ticks it flies before it is removed; above 0
   * @param world the world it flies in: its walls stop it, its characters can be hurt
   * @throws NullPointerException if {@code bodyBox}, {@code owner} or {@code world} is {@code null}
   * @throws IllegalArgumentException if the aim or the speed is not a finite number, the speed is 0
   *     or less, or the lifetime is 0 or less
   */
  protected Projectile(Aabb bodyBox, Character owner, float aimDegrees, float speed, int lifetimeTicks, Worl world) {
    super(bodyBox);
    if (!Float.isFinite(aimDegrees)) {
      throw IllegalArgumentException()
    }
  }


}
