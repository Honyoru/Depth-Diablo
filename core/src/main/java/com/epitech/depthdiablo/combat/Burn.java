package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Character;

/**
 * Damage over time (GDD 1.3.1, 1.6): deals its damage at the end of every interval until its
 * duration is over. The damage ignores invincibility and grants none.
 */
public class Burn extends StatusEffect {
  private final int damage;
  private final int intervalTicks;
  private int ticksSinceLastDamage;

  /**
   * Creates a burn from its values.
   *
   * @param spec the damage, interval and duration, from {@code data/items.json}
   * @param source the character who applied it, credited for its damage
   * @throws NullPointerException if an argument is {@code null}
   */
  public Burn(DotSpec spec, Character source) {
    super(spec.durationTicks(), source);
    this.damage = spec.damage();
    this.intervalTicks = spec.intervalTicks();
    this.ticksSinceLastDamage = 0;
  }

  /**
   * Counts one tick and deals the damage when an interval ends.
   *
   * @param target the character who burns
   */
  @Override
  protected void onTick(Character target) {
    ticksSinceLastDamage++;
    if (ticksSinceLastDamage >= intervalTicks) {
      target.takeStatusDamage(damage, getSource());
      ticksSinceLastDamage = 0;
    }
  }
}
