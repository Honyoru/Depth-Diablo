package com.epitech.depthdiablo.entity.enemy;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.stats.StatSource;
import com.epitech.depthdiablo.geometry.Aabb;
import java.util.Objects;

/**
 * What every enemy is built from, whatever its family: the values of its {@code
 * data/characters.json} entry that all enemies share, plus where it lives. The family adds its own
 * values (reach, summons) next to it.
 *
 * @param body the box the enemy occupies in the world, already placed; also its hurtbox
 * @param stats where its stats come from (GDD 1.6)
 * @param spriteId the picture the renderer draws for it, e.g. {@code "enemies/rat"}
 * @param invincibilityTicks invincibility ticks granted after a hit; 0 or more
 * @param summoned true if a summoner spawned it; a summoned enemy never drops loot (Q16)
 * @param world the world it lives in: its walls stop it, its event bus hears its hits
 */
public record EnemySetup(
    Aabb body,
    StatSource stats,
    String spriteId,
    int invincibilityTicks,
    boolean summoned,
    World world) {

  /**
   * Checks the values.
   *
   * @throws NullPointerException if {@code body}, {@code stats}, {@code spriteId} or {@code world}
   *     is null
   * @throws IllegalArgumentException if {@code spriteId} is blank or {@code invincibilityTicks} is
   *     negative
   */
  public EnemySetup {
    Objects.requireNonNull(body, "body is null");
    Objects.requireNonNull(stats, "stats is null");
    Objects.requireNonNull(spriteId, "spriteId is null");
    Objects.requireNonNull(world, "world is null");
    if (spriteId.isBlank()) {
      throw new IllegalArgumentException("The sprite id of an enemy must not be blank");
    }
    if (invincibilityTicks < 0) {
      throw new IllegalArgumentException(
          "Invincibility ticks must not be negative, was " + invincibilityTicks);
    }
  }
}
