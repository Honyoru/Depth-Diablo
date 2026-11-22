package com.epitech.depthdiablo.entity.character;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.stats.StatSource;
import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.input.PlayerInput;
import com.epitech.depthdiablo.item.Armor;
import com.epitech.depthdiablo.item.Weapon;

/** The melee class (GDD 1.2). It differs from the other classes by its stats and weapon (Q15). */
public final class Knight extends Player {

  /**
   * Creates a knight.
   *
   * @param bodyBox the box the knight occupies in the world; must not be {@code null}
   * @param stats where its stats come from; must not be {@code null}
   * @param startingWeapon the weapon it starts with: its base attack and the first item of its
   *     inventory; must not be {@code null}
   * @param startingArmor the armor it starts with, in its inventory; must not be {@code null}
   * @param hurtbox the knight's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param input what the user asks for; must not be {@code null}
   * @param world the world the player walks in, asked what blocks it; must not be {@code null}
   */
  public Knight(
      Aabb bodyBox,
      StatSource stats,
      Weapon startingWeapon,
      Armor startingArmor,
      Aabb hurtbox,
      int invincibilityTicksAfterHit,
      PlayerInput input,
      World world) {
    super(
        bodyBox,
        stats,
        startingWeapon,
        startingArmor,
        hurtbox,
        invincibilityTicksAfterHit,
        input,
        world);
  }
}
