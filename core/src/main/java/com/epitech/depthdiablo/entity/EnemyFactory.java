package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;

/**
 * Builds enemies from {@code data/enemies.json} (Q23, Q129): {@link EnemySpecReader} reads the
 * entry of an id, including the class it is built as, and {@link #create} builds that class with
 * those values. After F5 the next call uses the new values.
 *
 * <p>Pattern: Simple Factory.
 */
public final class EnemyFactory {
  private final DataRegistry enemies;
  private final World world;

  /**
   * Creates a factory that reads its values from the given registry.
   *
   * @param enemies the entries of {@code data/enemies.json}
   * @param world the world the enemies it creates live in
   * @throws NullPointerException if {@code enemies} or {@code world} is null
   */
  public EnemyFactory(DataRegistry enemies, World world) {
    this.enemies = Objects.requireNonNull(enemies, "The enemy data registry must not be null");
    this.world = Objects.requireNonNull(world, "World must not be null");
  }

  /**
   * Returns the base values of an enemy type.
   *
   * @param id the enemy's id ex {@code "rat"}
   * @return its values
   */
  public EnemySpec spec(String id) {
    return EnemySpecReader.read(id, enemies.definition(id));
  }

  /**
   * Creates an enemy of the given type, idle and with no target. The caller adds it to the world
   * and alerts it. The floor multiplier (GDD 1.6) is still to set: every floorgive the base stats
   * for now.
   *
   * @param id the enemy's id, e.g. {@code "rat"}
   * @param floor the floor it spawns on, 1 or more
   * @param summoned true if a summoner spawns it; a summoned enemy never drops loot
   * @param position the bottom-left corner of its body box, in pixels
   * @return a new enemy of the class its entry names
   * @throws IllegalArgumentException if the floor is below 1, the id is unknown, or a value is
   *     missing or invalid
   * @throws NullPointerException if the floor is below 1, the id is null
   */
  public Enemy create(String id, int floor, boolean summoned, Vector2 position) {
    if (floor < 1) {
      throw new IllegalArgumentException("The floor must be 1 or more, was " + floor);
    }
    Objects.requireNonNull(position, "The position of a new enemy must not be null");
    EnemySpec spec = spec(id);
    Aabb body = new Aabb(position.x, position.y, spec.width(), spec.height());
    int invincibility = spec.invincibilityTicks();
    return switch (spec.kind()) {
      case RAT -> new Rat(body, spec.stats(), body, invincibility, summoned, spec.reach(), world);
      case ZOMBIE ->
          new Zombie(body, spec.stats(), body, invincibility, summoned, spec.reach(), world);
    };
  }
}
