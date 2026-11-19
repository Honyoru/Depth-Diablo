package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;

/**
 * Builds the enemies of {@code data/characters.json} for the {@link CharacterFactory} (Q23, Q129).
 * On top of the values every character shares, it reads the {@code "reach"} of their contact
 * attack. The floor multiplier (GDD 1.6) and summoned enemies are still to come: every enemy it
 * builds has its base stats and is not summoned.
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
   * @param entry its whole entry, for its {@code "reach"}
   * @param body its body box, already placed in the world; also its hurtbox
   * @return a new enemy of the given kind
   * @throws NullPointerException if an argument is null
   * @throws IllegalArgumentException if the reach is missing, negative or not a finite number
   */
  public Enemy create(EnemyKind kind, CharacterSpec spec, JsonValue entry, Aabb body) {
    Objects.requireNonNull(kind, "kind is null");
    Objects.requireNonNull(spec, "spec is null");
    Objects.requireNonNull(entry, "entry is null");
    Objects.requireNonNull(body, "body is null");
    float reach = entry.getFloat("reach");
    return kind.create(body, spec.stats(), reach, body, spec.invincibilityTicks(), false, world);
  }
}
