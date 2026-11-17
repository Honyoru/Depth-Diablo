package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Character;
import java.util.Objects;

public final class FireTouch extends AttackDecorator {
  private final BurnSpec burn;

  public FireTouch(Attack inner, BurnSpec burn) {
    super(inner); // AttackDecorator stores the sword
    this.burn = Objects.requireNonNull(burn, "...");
  }

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
