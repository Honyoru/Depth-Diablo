package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;

/**
 * Builds the enemies of {@code data/characters.json} for the {@link CharacterFactory} (Q23, Q129).
 * It puts the values every enemy shares in an {@link EnemySetup}, then its {@link EnemyKind} reads
 * the values of its family from the entry. It also applies the floor multiplier (GDD 1.6): an enemy
 * built on a deeper floor gets its base HP and Attack multiplied by it, rounded; its other stats
 * stay as they are. In the Arena the multiplier is 1.
 *
 * <p>It is also the {@link Summoner} of the summoners it builds: what they spawn comes from the
 * same file, with {@code summoned} set.
 *
 * <p>Pattern: Simple Factory.
 */
public final class EnemyFactory {
  private final World world;

  /** What the base HP and Attack of every enemy it builds are multiplied by; 1 keeps them. */
  private final float statMultiplier;

  /**
   * Creates a factory for the enemies of the given world, with their base stats.
   *
   * @param world the world the enemies it creates live in
   * @throws NullPointerException if {@code world} is null
   */
  public EnemyFactory(World world) {
    this(world, 1f);
  }

  /**
   * Creates a factory for the enemies of a floor: their base HP and Attack are multiplied by the
   * floor multiplier.
   *
   * @param world the world the enemies it creates live in
   * @param statMultiplier what their base HP and Attack are multiplied by; above 0
   * @throws NullPointerException if {@code world} is null
   * @throws IllegalArgumentException if {@code statMultiplier} is not above 0
   */
  public EnemyFactory(World world, float statMultiplier) {
    this.world = Objects.requireNonNull(world, "world is null");
    if (!(statMultiplier > 0f)) {
      throw new IllegalArgumentException(
          "The stat multiplier must be above 0, was " + statMultiplier);
    }
    this.statMultiplier = statMultiplier;
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
    StatSource stats =
        statMultiplier == 1f ? spec.stats() : new FloorStats(spec.stats(), statMultiplier);
    EnemySetup setup =
        new EnemySetup(body, stats, spec.spriteId(), spec.invincibilityTicks(), summoned, world);
    return kind.create(setup, entry, summoner);
  }

  /**
   * The stats of an enemy on a deeper floor: its base HP and Attack times the multiplier, rounded,
   * read from its base stats each time so F5 still reloads them.
   */
  private record FloorStats(StatSource base, float multiplier) implements StatSource {
    @Override
    public float get(Stat stat) {
      float value = base.get(stat);
      return stat == Stat.MAX_HP || stat == Stat.ATTACK ? Math.round(value * multiplier) : value;
    }
  }
}
