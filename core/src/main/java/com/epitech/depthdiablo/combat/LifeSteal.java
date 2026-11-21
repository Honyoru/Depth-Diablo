package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Character;

/**
 * Heals the attacker by a part of the damage of every hit that lands (GDD 1.3.1). The ruby ring
 * creates it.
 *
 * <p>Pattern: Decorator.
 */
public final class LifeSteal extends AttackDecorator {
  private final float fraction;

  /**
   * Wraps an attack with life steal.
   *
   * @param inner the attack to wrap
   * @param fraction the part of the damage healed, above 0 and at most 1
   * @throws NullPointerException if {@code inner} is {@code null}
   * @throws IllegalArgumentException if the fraction is outside (0, 1]
   */
  public LifeSteal(Attack inner, float fraction) {
    super(inner);
    if (!(fraction > 0 && fraction <= 1)) {
      throw new IllegalArgumentException("Life steal fraction must be in (0, 1]: " + fraction);
    }
    this.fraction = fraction;
  }

  /**
   * Applies the wrapped hit, then heals the attacker by at least 1 HP if it landed.
   *
   * @param attacker the character who hits
   * @param target the character who is hit
   * @return what the wrapped hit did, unchanged
   */
  @Override
  public HitResult hit(Character attacker, Character target) {
    HitResult result = super.hit(attacker, target);
    if (result.landed()) {
      attacker.heal(Math.max(1, Math.round(result.damage() * fraction)));
    }
    return result;
  }
}
