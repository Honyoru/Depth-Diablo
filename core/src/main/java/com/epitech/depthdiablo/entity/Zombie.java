package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.FrontContactAttack;
import com.epitech.depthdiablo.engine.World;

/**
 * The rat (fast, weak; bites at contact; GDD 1.3). It has no code of its own: its values come from
 * {@code data/characters.json} (GDD 1.3.1).
 */
public final class Zombie extends MeleeEnemy {

  /**
   * Creates a zombie.
   *
   * @param bodyBox the box the rat occupies in the world; must not be {@code null}
   * @param stats where its stats come from (GDD 1.6); must not be {@code null}
   * @param reach how far its bite goes past its body box, in pixels; 0 or more and finite
   * @param hurtbox the rat's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param summoned true if a summoner spawned it
   * @param world the world it lives in; must not be {@code null}
   */
  public Zombie(
      Aabb bodyBox,
      StatSource stats,
      float reach,
      Aabb hurtbox,
      int invincibilityTicksAfterHit,
      boolean summoned,
      World world) {
    super(
        bodyBox,
        stats,
        new FrontContactAttack(reach),
        hurtbox,
        invincibilityTicksAfterHit,
        summoned,
        world);
  }
}
