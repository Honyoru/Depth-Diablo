package com.epitech.depthdiablo.arena;

/**
 * An arena cycle consists of waves with breaks between them, ending in a boss wave followed by a
 * shop, and tracks the number of waves defeated as the score.
 */
public final class ArenaCycle {

  /** The current phase of the arena cycle. */
  public enum Phase {
    /** Enemies are fighting, waiting for waveCleared(). */
    WAVE,
    /** The timer between two normal waves is running. */
    BREAK,
    /** The boss wave is cleared, the shop is open until startNextCycle(). */
    SHOP
  }

  private final int wavesPerCycle;
  private final int breakTicks;
  private int wave;
  private Phase phase;
  private int breakTicksLeft;
  private int score;

  /**
   * Creates a new arena cycle.
   *
   * @param wavesPerCycle the number of normal waves in the cycle + 1 boss wave
   * @param breakTicks the number of ticks spent between two normal waves
   * @throws IllegalArgumentException if a provided value is lower than 1
   */
  public ArenaCycle(int wavesPerCycle, int breakTicks) {
    if (wavesPerCycle < 1) {
      throw new IllegalArgumentException("wavesPerCycle must be >= 1: " + wavesPerCycle);
    }
    if (breakTicks < 1) {
      throw new IllegalArgumentException("breakTicks must be >= 1: " + breakTicks);
    }
    this.wavesPerCycle = wavesPerCycle;
    this.breakTicks = breakTicks;
    this.wave = 1;
    this.phase = Phase.WAVE;
    this.breakTicksLeft = 0;
    this.score = 0;
  }

  /** Advances the logic by one tick while the arena is in a break. */
  public void update() {
    if (phase != Phase.BREAK) {
      return;
    }
    breakTicksLeft--;
    if (breakTicksLeft <= 0) {
      wave++;
      phase = Phase.WAVE;
      breakTicksLeft = 0;
    }
  }

  /**
   * Marks the current wave as cleared.
   *
   * @throws IllegalStateException if the arena is not currently in a wave
   */
  public void waveCleared() {
    if (phase != Phase.WAVE) {
      throw new IllegalStateException(
          "Cannot clear a wave while the arena phase is " + phase + ".");
    }
    score++;
    if (isBossWave()) {
      phase = Phase.SHOP;
      breakTicksLeft = 0;
      return;
    }
    phase = Phase.BREAK;
    breakTicksLeft = breakTicks;
  }

  /**
   * Starts the next cycle after the shop phase.
   *
   * @return true if the cycle was advanced, false otherwise
   */
  public boolean startNextCycle() {
    if (phase != Phase.SHOP) {
      return false;
    }
    wave++;
    phase = Phase.WAVE;
    breakTicksLeft = 0;
    return true;
  }

  /**
   * Checks whether the current wave is the boss wave.
   *
   * @return true if the current wave is the boss wave
   */
  public boolean isBossWave() {
    return wave % wavesPerCycle == 0;
  }

  /**
   * Gets the current wave number.
   *
   * @return the current wave number
   */
  public int getWaveNumber() {
    return wave;
  }

  /**
   * Gets the current arena phase.
   *
   * @return the current phase
   */
  public Phase getPhase() {
    return phase;
  }

  /**
   * Gets the number of waves defeated.
   *
   * @return the score
   */
  public int getScore() {
    return score;
  }
}
