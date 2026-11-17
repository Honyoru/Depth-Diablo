package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.data.DataRegistry;
import java.util.Objects;

/**
 * Reads enemy type from {@code data/enemies.json} (Q23, Q129). Until the Enemy class exists it
 * gives the values as an {@link EnemySpec}; {@code create(id, floor, summoned)} (UML) will build
 * the Enemy from it. After F5 the next call uses the new values.
 *
 * <p>Pattern: Simple Factory.
 */
public final class EnemyFactory {
  private final DataRegistry enemies;

  /**
   * Creates a factory that reads its values from the given registry.
   *
   * @param enemies the entries of {@code data/enemies.json}
   * @throws NullPointerException if {@code enemies} is null
   */
  public EnemyFactory(DataRegistry enemies) {
    this.enemies = Objects.requireNonNull(enemies, "The enemy data registry must not be null");
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
}
