package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.character.Character;

/**
 * Damage over time (GDD 1.3.1, 1.6): deals its damage at the end of every interval until its
 * duration is over. The damage ignores invincibility, grants none, and is credited to the source.
 *
 * <p>Pattern: Template Method. The ticking is written once here; each kind ({@link Burn}, {@link
 * Poison}) only gives its values, and the view tells them apart by their class.
 */
public abstract class DamageOverTime extends StatusEffect {
  private final int damage;
  private final int intervalTicks;
  private int ticksSinceLastDamage;

  /**
   * Creates the effect from its values.
   *
   * @param spec the damage, interval and duration, from {@code data/items.json}
   * @param source the character who applied it, credited for its damage
   * @throws NullPointerException if an argument is {@code null}
   */
  protected DamageOverTime(DotSpec spec, Character source) {
    super(spec.durationTicks(), source);
    this.damage = spec.damage();
    this.intervalTicks = spec.intervalTicks();
    this.ticksSinceLastDamage = 0;
  }

  /**
   * Returns the damage dealt at the end of each interval.
   *
   * @return the damage, 1 or more
   */
  public final int getDamage() {
    return damage;
  }

  /**
   * Returns how many ticks pass between two damages.
   *
   * @return the interval in ticks, 1 or more
   */
  public final int getIntervalTicks() {
    return intervalTicks;
  }

  /**
   * Counts one tick and deals the damage when an interval ends.
   *
   * @param target the character who takes the damage
   */
  @Override
  protected final void onTick(Character target) {
    ticksSinceLastDamage++;
    if (ticksSinceLastDamage >= intervalTicks) {
      target.takeStatusDamage(damage, getSource());
      ticksSinceLastDamage = 0;
    }
  }
}
