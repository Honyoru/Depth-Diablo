package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.character.Character;

/**
 * Finishes a target that a landed hit leaves under a part of its max HP (GDD 1.3.1). The bone ring
 * creates it. The finishing blow is status damage: it goes through invincibility and publishes its
 * own damage and death events, credited to the attacker.
 *
 * <p>Pattern: Decorator.
 */
public final class Execute extends AttackDecorator {
  private final float threshold;

  /**
   * Wraps an attack with an execute.
   *
   * @param inner the attack to wrap
   * @param threshold the part of max HP under which the target dies, above 0 and below 1
   * @throws NullPointerException if {@code inner} is {@code null}
   * @throws IllegalArgumentException if the threshold is outside (0, 1)
   */
  public Execute(Attack inner, float threshold) {
    super(inner);
    if (!(threshold > 0 && threshold < 1)) {
      throw new IllegalArgumentException("Execute threshold must be in (0, 1): " + threshold);
    }
    this.threshold = threshold;
  }

  /**
   * Applies the wrapped hit; if it landed and the target lives on under the threshold, kills it.
   *
   * @param attacker the character who hits
   * @param target the character who is hit
   * @return what the wrapped hit did, unchanged (the finishing blow sends its own events)
   */
  @Override
  public HitResult hit(Character attacker, Character target) {
    HitResult result = super.hit(attacker, target);
    if (result.landed() && !target.isDead() && target.getHp() <= target.getMaxHp() * threshold) {
      target.takeStatusDamage(target.getHp(), attacker);
    }
    return result;
  }
}
