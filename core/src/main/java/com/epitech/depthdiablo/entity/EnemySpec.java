package com.epitech.depthdiablo.entity;

import java.util.Objects;

/**
 * The base values of enemy type read from {@code data/enemies.json} (Q93). The Enemiy class tke
 * them in its constructor ; Rat and Zombie only differ by these values (GDD 1.3.1).
 *
 * <p>Pattern: ValueObject (Q97).
 *
 * @param id the enemy's id ex {@code "rat"}
 * @param spriteId the picture render draws for it
 * @param maxHp its health; checked by Character
 * @param attack the base damage of its hits; checked by Character
 * @param resistance the divisor of the damage its takes; checked by Character
 * @param speed how fast it moves, in pixels or ticks per second; set 0 for static enemy
 * @param width the widgh of his boyBox
 * @param height the heigh of his bodyBox
 */
public record EnemySpec(
    String id,
    String spriteId,
    int maxHp,
    int attack,
    int resistance,
    float speed,
    float width,
    float height) {
  /**
   * Checks the values Character does not check.
   *
   * @throws NullPointerException if a text is null
   * @throws IlegalArgumentException if the speed is negative or a size is not positive
   */
  public EnemySpec {
    Objects.requireNonNull(id, "id is null");
    Objects.requireNonNull(spriteId, "spriteId is null");
    if (speed < 0) {
      throw new IllegalArgumentException("speed must not be negative");
    }
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("width and height must be positive");
    }
  }
}
