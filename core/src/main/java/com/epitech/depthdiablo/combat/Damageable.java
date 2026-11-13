package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Aabb;

/** Represents an object that can take damage and have a hurtbox. */
public interface Damageable {

  /**
   * Returns where this one can be hit (GDD 1.5).
   *
   * @return its hurtbox
   */
  Aabb hurtbox();

  /**
   * Loses HP from a hit. Nothing happens while it is dead or invincible.
   *
   * @param amount the damage to take; at least 1
   */
  void takeDamage(int amount);

  /**
   * Tells whether its HP reached 0.
   *
   * @return true once it is dead
   */
  boolean isDead();

  /**
   * Returns the side this one fights for (GDD 1.5). An attack never hurts its own side, so enemies
   * never hit each other.
   *
   * @return its faction now; it may change while it lives, for example when charmed
   */
  Faction faction();
}
