package com.epitech.depthdiablo.entity.character;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.stats.StatSource;
import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.input.PlayerInput;
import com.epitech.depthdiablo.item.Armor;
import com.epitech.depthdiablo.item.Weapon;

/**
 * The fast, fragile class (GDD 1.2): less HP than the knight, more speed and attack speed (Q15).
 */
public final class Assassin extends Player {

  /**
   * Creates an assassin; the arguments are those of {@link Knight}.
   *
   * @param bodyBox the box it occupies in the world
   * @param stats where its stats come from
   * @param startingWeapon the weapon it starts with
   * @param startingArmor the armor it starts with
   * @param hurtbox its hurtbox
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param input what the user asks for
   * @param world the world the player walks in
   */
  public Assassin(
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
