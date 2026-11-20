package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.FrontContactAttack;

/**
 * The zombie (slow, sturdy; hits at contact; GDD 1.3). It has no code of its own: its values come
 * from {@code data/characters.json} (GDD 1.3.1). The king of the arena is a big zombie.
 */
public final class Zombie extends MeleeEnemy {

  /**
   * Creates a zombie.
   *
   * @param setup the values every enemy shares; must not be {@code null}
   * @param reach how far its hit goes past its body box, in pixels; 0 or more and finite
   * @throws NullPointerException if {@code setup} is {@code null}
   * @throws IllegalArgumentException if {@code reach} is negative or not finite
   */
  public Zombie(EnemySetup setup, float reach) {
    super(setup, new FrontContactAttack(reach));
  }
}
