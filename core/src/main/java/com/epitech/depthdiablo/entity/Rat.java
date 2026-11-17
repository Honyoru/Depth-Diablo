package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.engine.World;

/**
 * The rat (fast, weak; bites at contact; GDD 1.3). It has no code of its own: its values come from
 * {@code data/enemies.json} (GDD 1.3.1).
 */
public final class Rat extends MeleeEnemy {

  /**
   * Creates a rat.
   *
   * @param bodyBox the box the rat occupies in the world; must not be {@code null}
   * @param stats where its stats come from (GDD 1.6); must not be {@code null}
   * @param hurtbox the rat's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param summoned true if a summoner spawned it
   * @param reach how far its attack goes past its body box, in pixels; 0 or more
   */
  public Rat(
      Aabb bodyBox,
      StatSource stats,
      Aabb hurtbox,
      int invincibilityTicksAfterHit,
      boolean summoned,
      float reach,
      World world) {
    super(bodyBox, stats, hurtbox, invincibilityTicksAfterHit, summoned, reach, world);
  }
}
