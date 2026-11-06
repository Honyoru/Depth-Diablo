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
}
