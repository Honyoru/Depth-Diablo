package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Character;
import java.util.Objects;

/**
 * Base of every attack wrapper that a passive item adds (GDD 1.3.1). It forwards both calls to the
 * wrapped attack; a subclass overrides the call it changes and calls {@code super} inside it.
 *
 * <p>Pattern: Decorator. The holder sees one {@link Attack}, whatever is stacked behind it.
 */
public abstract class AttackDecorator implements Attack {

  /** The attack this one wraps: the weapon, or another decorator. */
  protected final Attack inner;

  /**
   * Wraps an attack.
   *
   * @param inner the attack to wrap
   * @throws NullPointerException if {@code inner} is {@code null}
   */
  protected AttackDecorator(Attack inner) {
    this.inner = Objects.requireNonNull(inner, "inner cannot be null");
  }

  /**
   * Applies the hit of the wrapped attack.
   *
   * @param attacker the character who hits
   * @param target the character who is hit
   * @return what the wrapped attack's hit did
   */
  @Override
  public HitResult hit(Character attacker, Character target) {
    return inner.hit(attacker, target);
  }

  /**
   * Starts the wrapped attack with the same arguments.
   *
   * @param owner the character who attacks
   * @param aimDegree the direction of the attack, in degrees (0 = right, 90 = up)
   * @param world the world the attack happens in
   */
  @Override
  public void attack(Character owner, float aimDegree, World world) {
    inner.attack(owner, aimDegree, world);
  }
}
