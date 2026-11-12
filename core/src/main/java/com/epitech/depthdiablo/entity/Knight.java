package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.input.PlayerInput;

/** The melee class (GDD 1.2). It differs from the other classes by its stats and weapon (Q15). */
public final class Knight extends Player {

  /**
   * Creates a knight.
   *
   * @param bodyBox the box the knight occupies in the world; must not be {@code null}
   * @param stats where its stats come from; must not be {@code null}
   * @param hurtbox the knight's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param input what the user asks for; must not be {@code null}
   */
  public Knight(
      Aabb bodyBox,
      StatSource stats,
      Aabb hurtbox,
      int invincibilityTicksAfterHit,
      PlayerInput input) {
    super(bodyBox, stats, hurtbox, invincibilityTicksAfterHit, input);
  }
}
