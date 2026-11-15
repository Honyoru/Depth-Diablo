package com.epitech.depthdiablo.entity;

import java.util.Objects;

/**
 * The base values of one enemy type, read from {@code data/enemies.json} (Q93). The Enemy class
 * takes them in its constructor; Rat and Zombie only differ by these values (GDD 1.3.1).
 *
 * <p>Pattern: Type Object (GPP), as a Value Object record (Q97).
 *
 * @param id the enemy's id, e.g. {@code "rat"}
 * @param spriteId the picture the renderer draws for it, e.g. {@code "enemies/rat"}
 * @param stats its stats before any item (GDD 1.6); BaseStats checks every value
 * @param width the width of its body box, in pixels
 * @param height the height of its body box, in pixels
 */
public record EnemySpec(String id, String spriteId, BaseStats stats, float width, float height) {

  /**
   * Checks the values BaseStats does not check.
   *
   * @throws NullPointerException if a text or the stats are null
   * @throws IllegalArgumentException if a size is not positive
   */
  public EnemySpec {
    Objects.requireNonNull(id, "The id of an enemy must not be null");
    Objects.requireNonNull(spriteId, "The sprite id of enemy " + id + " must not be null");
    Objects.requireNonNull(stats, "The stats of enemy " + id + " must not be null");
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException(
          "The size of enemy " + id + " must be positive, was " + width + " x " + height);
    }
  }
}
