package com.epitech.depthdiablo.entity;

import java.util.Objects;

/**
 * The base values of one enemy type, read from {@code data/enemies.json} (Q93). The Enemy class
 * takes them in its constructor; Rat and Zombie only differ by these values (GDD 1.3.1).
 *
 * <p>Pattern: Type Object (GPP), as a Value Object record (Q97).
 *
 * @param id the enemy's id, e.g. {@code "rat"}
 * @param kind the class it is built as
 * @param spriteId the picture the renderer draws for it, e.g. {@code "enemies/rat"}
 * @param stats its stats before any item (GDD 1.6); BaseStats checks every value
 * @param width the width of its body box, in pixels
 * @param height the height of its body box, in pixels
 * @param reach how far its attack goes past its body box, in pixels; 0 or more
 * @param invincibilityTicks how many ticks it cannot be hurt after a hit (Q28); 0 or more
 */
public record EnemySpec(
    String id,
    EnemyKind kind,
    String spriteId,
    BaseStats stats,
    float width,
    float height,
    float reach,
    int invincibilityTicks) {

  /**
   * Checks the values BaseStats does not check.
   *
   * @throws NullPointerException if a text, the kind or the stats are null
   * @throws IllegalArgumentException if a size is not positive
   */
  public EnemySpec {
    Objects.requireNonNull(id, "The id of an enemy must not be null");
    Objects.requireNonNull(kind, "The class of enemy " + id + " must not null");
    Objects.requireNonNull(spriteId, "The sprite id of enemy " + id + " must not be null");
    Objects.requireNonNull(stats, "The stats of enemy " + id + " must not be null");
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException(
          "The size of enemy " + id + " must be positive, was " + width + " x " + height);
    }
    if (!(reach >= 0) || Float.isInfinite(reach)) {
      throw new IllegalArgumentException(
          "The reach of enemy " + id + " must be a finite number of 0 or more, was " + reach);
    }
    if (invincibilityTicks < 0) {
      throw new IllegalArgumentException(
          "The invincibility of enemy " + id + " must not be negative, was " + invincibilityTicks);
    }
  }
}
