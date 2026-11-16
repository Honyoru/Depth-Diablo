package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Character;

/**
 * What a character's attack does (GDD 1.3.1, 1.6). The equipped weapon is the base attack; passive
 * items wrap it to add effects, such as a burn on every hit (Decorator).
 *
 * <p>Pattern: Strategy. The character holds an Attack and never knows which weapon is behind it.
 * {@link #attack} starts an attack (a swing, an arrow) and {@link #hit} applies one of its hits.
 */
public interface Attack {

  /**
   * Applies one hit from the attacker to the target.
   *
   * @param attacker the character who hits
   * @param target the character who is hit
   * @return what the hit did; {@link HitResult#NONE} if it did not land (dead or invincible target)
   * @throws NullPointerException if {@code attacker} or {@code target} is {@code null}
   */
  HitResult hit(Character attacker, Character target);

  /**
   * Starts one attack of the owner towards the aim: a sword starts a swing, a bow will shoot an
   * arrow. Only the owner's {@code think} calls it, once {@link Character#canAttack()} is true.
   *
   * @param owner the character who attacks
   * @param aimDegrees the direction of the attack, in degrees (0 = right, 90 = up)
   * @param world the world the attack happens in: its entities are the targets, its event bus gets
   *     the damage and deaths
   * @throws NullPointerException if an argument is {@code null}
   * @throws IllegalArgumentException if the aim is not a finite number
   * @throws IllegalStateException if the owner cannot attack now
   */
  void attack(Character owner, float aimDegrees, World world);
}
