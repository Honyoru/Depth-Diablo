package com.epitech.depthdiablo.entity.spawn;

import com.epitech.depthdiablo.entity.stats.BaseStats;
import com.epitech.depthdiablo.entity.stats.StatSource;
import java.util.Objects;

/**
 * The values every character type shares, read from {@code data/characters.json} (Q93). The values
 * of one family only (an enemy's reach, a player's weapon) are read by the factory of that family.
 *
 * <p>Pattern: Type Object (GPP), as a Value Object record (Q97).
 *
 * @param id the character's id, e.g. {@code "rat"}
 * @param kind the class it is built as
 * @param spriteId the picture the renderer draws for it, e.g. {@code "enemies/rat"}
 * @param stats its stats before any item (GDD 1.6): the {@link BaseStats} read from the file, which
 *     checks every value, or the {@link ReloadableStats} the factory shares for F5
 * @param width the width of its body box, in pixels
 * @param height the height of its body box, in pixels
 * @param invincibilityTicks how many ticks it cannot be hurt after a hit (Q28); 0 or more
 */
public record CharacterSpec(
    String id,
    CharacterKind kind,
    String spriteId,
    StatSource stats,
    float width,
    float height,
    int invincibilityTicks) {

  /**
   * Checks the values BaseStats does not check.
   *
   * @throws NullPointerException if a text, the kind or the stats are null
   * @throws IllegalArgumentException if a size is not a finite number above 0 or the invincibility
   *     is negative
   */
  public CharacterSpec {
    Objects.requireNonNull(id, "id is null");
    Objects.requireNonNull(kind, "kind of " + id + " is null");
    Objects.requireNonNull(spriteId, "spriteId of " + id + " is null");
    Objects.requireNonNull(stats, "stats of " + id + " is null");
    if (width <= 0
        || Float.isNaN(width)
        || Float.isInfinite(width)
        || height <= 0
        || Float.isNaN(height)
        || Float.isInfinite(height)) {
      throw new IllegalArgumentException(
          "The size of " + id + " must be finite and positive, was " + width + " x " + height);
    }
    if (invincibilityTicks < 0) {
      throw new IllegalArgumentException(
          "The invincibility of " + id + " must not be negative, was " + invincibilityTicks);
    }
  }

  /**
   * Returns a copy of this spec with other stats; every other value stays the same. The {@link
   * CharacterFactory} uses it to give a character the stats F5 can swap.
   *
   * @param newStats the stats of the copy
   * @return a new spec; this one is not changed
   * @throws NullPointerException if {@code newStats} is null
   */
  public CharacterSpec withStats(StatSource newStats) {
    return new CharacterSpec(id, kind, spriteId, newStats, width, height, invincibilityTicks);
  }
}
