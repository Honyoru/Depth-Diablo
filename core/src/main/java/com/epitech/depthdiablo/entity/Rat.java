package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.FrontContactAttack;

/**
 * The rat (fast, weak; bites at contact; GDD 1.3). It has no code of its own: its values come from
 * {@code data/characters.json} (GDD 1.3.1).
 */
public final class Rat extends MeleeEnemy {

  /**
   * Creates a rat.
   *
   * @param setup the values every enemy shares; must not be {@code null}
   * @param reach how far its bite goes past its body box, in pixels; 0 or more and finite
   * @throws NullPointerException if {@code setup} is {@code null}
   * @throws IllegalArgumentException if {@code reach} is negative or not finite
   */
  public Rat(EnemySetup setup, float reach) {
    super(setup, new FrontContactAttack(reach));
  }
}
