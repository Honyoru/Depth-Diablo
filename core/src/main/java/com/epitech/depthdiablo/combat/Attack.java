package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Character;

/**
 * What a character's attack does (GDD 1.3.1, 1.6). The equipped weapon is the base attack; passive
 * items wrap it to add effects, such as a burn on every hit (Decorator).
 *
 * <p>Pattern: Strategy. The character holds an Attack and never knows which weapon is behind it.
 * {@code attack(owner, aim, world)} comes with hitboxes (UML, items.drawio).
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
}
