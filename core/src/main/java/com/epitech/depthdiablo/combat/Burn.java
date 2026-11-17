package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Character;

public class Burn extends StatusEffect {
  private final int damage;
  private final int intervalTicks;
  private int ticksSinceLastDamage;

  public Burn(BurnSpec spec, Character source) {
    super(spec.durationTicks(), source);
    this.damage = spec.damage();
    this.intervalTicks = spec.intervalTicks();
    this.ticksSinceLastDamage = 0;
  }

  @Override
  protected void onTick(Character target) {
    ticksSinceLastDamage++;
    if (ticksSinceLastDamage >= intervalTicks) {
      target.takeStatusDamage(damage, getSource());
      ticksSinceLastDamage = 0;
    }
  }
}
