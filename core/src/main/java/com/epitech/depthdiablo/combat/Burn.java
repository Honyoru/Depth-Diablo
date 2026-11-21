package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Character;

/**
 * A burn (GDD 1.3.1, 1.6): damage over time from fire, such as the fire ring's. The view tints a
 * burning character red.
 */
public final class Burn extends DamageOverTime {

  /**
   * Creates a burn from its values.
   *
   * @param spec the damage, interval and duration, from {@code data/items.json}
   * @param source the character who applied it, credited for its damage
   * @throws NullPointerException if an argument is {@code null}
   */
  public Burn(DotSpec spec, Character source) {
    super(spec, source);
  }
}
