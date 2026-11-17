package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Character;
import java.util.Objects;

/**
 * Base of every passive that changes an attack. It forwards both calls to the wrapped attack, and
 * subclasses override the one they change. Pattern: Decorator (GDD 1.3.1).
 */
public abstract class AttackDecorator implements Attack {

  protected final Attack inner;

  protected AttackDecorator(Attack inner) {
    this.inner = Objects.requireNonNull(inner, "inner cannot be null");
  }

  @Override
  public HitResult hit(Character attacker, Character target) {
    return inner.hit(attacker, target);
  }

  @Override
  public void attack(Character owner, float aimDegree, World world) {
    inner.attack(owner, aimDegree, world);
  }
}
