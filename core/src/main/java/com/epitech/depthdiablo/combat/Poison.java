package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Character;

/**
 * A poison (GDD 1.3.1, 1.6): damage over time from venom. The view tints a poisoned character
 * green.
 */
public final class Poison extends DamageOverTime {

  /**
   * Creates a poison from its values.
   *
   * @param spec the damage, interval and duration, from {@code data/items.json}
   * @param source the character who applied it, credited for its damage
   * @throws NullPointerException if an argument is {@code null}
   */
  public Poison(DotSpec spec, Character source) {
    super(spec, source);
  }
}
