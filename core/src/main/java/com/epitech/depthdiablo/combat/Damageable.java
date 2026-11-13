package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Aabb;

/** Represents an object that can take damage and have a hurtbox. */
public interface Damageable {

  /**
   * @return The hurtbox of the object.
   */
  Aabb hurtbox();

  /**
   * @param amount The amount of damage to take.
   */
  void takeDamage(int amount);

  /**
   * @return Whether the object is dead.
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
