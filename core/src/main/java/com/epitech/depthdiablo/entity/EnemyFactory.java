package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;

/**
 * Builds the enemies of {@code data/characters.json} for the {@link CharacterFactory} (Q23, Q129).
 * It puts the values every enemy shares in an {@link EnemySetup}, then its {@link EnemyKind} reads
 * the values of its family from the entry. The floor multiplier (GDD 1.6) is still to come: every
 * enemy it builds has its base stats.
 *
 * <p>It is also the {@link Summoner} of the summoners it builds: what they spawn comes from the
 * same file, with {@code summoned} set.
 *
 * <p>Pattern: Simple Factory.
 */
public final class EnemyFactory {
  private final World world;

  /**
   * Creates a factory for the enemies of the given world.
   *
   * @param world the world the enemies it creates live in
   * @throws NullPointerException if {@code world} is null
   */
  public EnemyFactory(World world) {
    this.world = Objects.requireNonNull(world, "world is null");
  }

  /**
   * Builds an idle enemy with no target. The caller adds it to the world and alerts it.
   *
   * @param kind the class to build
   * @param spec the values every character shares, read from its entry
   * @param entry its whole entry, for the values of its family
   * @param body its body box, already placed in the world; also its hurtbox
   * @param summoned true if a summoner spawns it; a summoned enemy never drops loot (Q16)
   * @param summoner builds the enemies it spawns, if it is a summoner
   * @return a new enemy of the given kind
   * @throws NullPointerException if an argument is null
   * @throws IllegalArgumentException if a value of its family is missing or invalid
   */
  public Enemy create(
      EnemyKind kind,
      CharacterSpec spec,
      JsonValue entry,
      Aabb body,
      boolean summoned,
      Summoner summoner) {
    Objects.requireNonNull(kind, "kind is null");
    Objects.requireNonNull(spec, "spec is null");
    Objects.requireNonNull(entry, "entry is null");
    Objects.requireNonNull(body, "body is null");
    Objects.requireNonNull(summoner, "summoner is null");
    EnemySetup setup =
        new EnemySetup(
            body, spec.stats(), spec.spriteId(), spec.invincibilityTicks(), summoned, world);
    return kind.create(setup, entry, summoner);
  }
}
