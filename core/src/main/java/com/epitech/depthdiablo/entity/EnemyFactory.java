package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.data.DataRegistry;
import java.util.Objects;

/**
 * Reads enemy type from {@code data/enemies.json} (Q23, Q129).
 * Until the Enemy class exists it gives the values as an {@Link EnemySpec};{@code create(id, floor, summoned)} (UML) will build the Enemy from it. After F5 the next call uses the new values.
 *
 * <p>Pattern: Simple Factory.
 *
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
    this.enemies = Objects.requireNonNull(enemies, "enemies");
  }

  /**
   * Returns the base values of an enemy type.
   *
   * @param id the enemy's id ex {@code "rat"}
   * @return its values
   * @throws IllegalArgumentException if the id is unknown or a value is missing or invalid
   */
  public EnemySpec spec(String id) {
    JsonValue entry = enemies.definition(id);
    return new EnemySpec(
      id,
      entry.getString("spriteId"),
      entry.getInt("maxHp"), entry.getInt("attack"), entry.getInt("resistance"),
      entry.getFloat("speed"), entry.getFloat("width"), entry.getFloat("height")
    );
  }
}
