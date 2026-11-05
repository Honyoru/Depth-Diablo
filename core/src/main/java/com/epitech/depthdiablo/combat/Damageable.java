package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Aabb;

/** Role for anything that can be hit: only Characters, never chests or the shopkeeper (1.3.1). */
public interface Damageable {
  /**
   * Returns where this target can be hit.
   *
   * @return the hurtbox, in world coordinates
   */
  Aabb hurtbox();

  /**
   * Removes HP, unless the target is invincible or already dead.
   *
   * @param amount the damage, at least 1 (GDD 1.6)
   */
  void takeDamage(int amount);

  /**
   * Tells whether HP reached 0.
   *
   * @return true if the target is dead
   */
  boolean isDead();
}
