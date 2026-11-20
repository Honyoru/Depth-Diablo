package com.epitech.depthdiablo.entity;

/**
 * The two quick moves of the player (GDD 2.4, Q40): a move in a fixed direction for a few ticks,
 * then a cooldown that starts once it is over. The values are the base ones; items will change them
 * through the build, never here.
 *
 * <p>Pattern: Strategy (each constant can add its own effect in {@link #onStart}).
 */
public enum Evade {
  /** Fast and straight; the player can still be hit. */
  DASH(12, 3f, 30),

  /** A bit faster than walking, invincible for the whole roll. */
  DODGE_ROLL(24, 1.5f, 48) {
    @Override
    void onStart(Character mover, int ticks) {
      mover.grantInvincibility(ticks);
    }
  };

  private final int ticks;
  private final float speedFactor;
  private final int cooldownTicks;

  Evade(int ticks, float speedFactor, int cooldownTicks) {
    this.ticks = ticks;
    this.speedFactor = speedFactor;
    this.cooldownTicks = cooldownTicks;
  }

  /**
   * Returns how long the move lasts.
   *
   * @return its duration in ticks
   */
  public int getTicks() {
    return ticks;
  }

  /**
   * Returns how much faster than walking the move goes.
   *
   * @return the factor applied to the SPEED stat
   */
  public float getSpeedFactor() {
    return speedFactor;
  }

  /**
   * Returns how long the player waits, once the move is over, before doing it again.
   *
   * @return the cooldown in ticks
   */
  public int getCooldownTicks() {
    return cooldownTicks;
  }

  /**
   * Adds the effect of the move when it starts. The dash adds nothing.
   *
   * @param mover the character who moves
   * @param ticks how long this move lasts, after the items
   */
  void onStart(Character mover, int ticks) {
    // the dash can be hit
  }
}
