package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Character;
import java.util.Objects;

/**
 * Adds a burn to every hit of the wrapped attack that lands (GDD 1.3.1, 1.4.1): a hit that deals no
 * damage, on a dead or invincible target, burns nothing. The fire ring creates it.
 *
 * <p>Pattern: Decorator.
 */
public final class FireTouch extends AttackDecorator {
  private final BurnSpec burn;

  /**
   * Wraps an attack with a burn.
   *
   * @param inner the attack to wrap, usually the weapon
   * @param burn the burn each landed hit applies, from {@code data/items.json}
   * @throws NullPointerException if an argument is {@code null}
   */
  public FireTouch(Attack inner, BurnSpec burn) {
    super(inner); // AttackDecorator stores the sword
    this.burn = Objects.requireNonNull(burn, "...");
  }

  /**
   * Applies the wrapped hit, then burns the target if the hit landed. The attacker is the source of
   * the burn, so its damage and kill are credited to them.
   *
   * @param attacker the character who hits
   * @param target the character who is hit
   * @return what the wrapped attack's hit did, unchanged
   */
  @Override
  public HitResult hit(Character attacker, Character target) {
    HitResult result = super.hit(attacker, target); // the sword's normal damage
    if (result.landed()) {
      target.applyStatus(
          new Burn(burn, attacker)); // attach the burn; it deals damage on later ticks
    }
    return result;
  }
}
