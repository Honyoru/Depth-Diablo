package com.epitech.depthdiablo.arena;

/**
 * Wave order of Arena mode (GDD 1.2). Waves come in cycles; the last wave of a cycle is a boss.
 * After a normal wave, a timed break starts the next one. After the boss, the shop opens and the
 * next cycle starts only when the player activates it. Score = waves defeated.
 */
public final class ArenaCycle {
  /** What the arena is doing right now. */
  public enum Phase {
    /** A wave is being fought. */
    WAVE,
    /** Timed pause after a normal wave. */
    BREAK,
    /** The shopkeeper is open after a boss wave; waits for the player. */
    SHOP
  }

  private final int wavesPerCycle;
  private final int breakTicks;
  private Phase phase = Phase.WAVE;
  private int wave = 1;
  private int score;
  private int breakTicksLeft;

  /**
   * Creates the cycle, with wave 1 already started.
   *
   * @param wavesPerCycle waves per cycle, the last one being the boss (5 in the GDD)
   * @param breakTicks length of the break after a normal wave, in ticks (15 s in the GDD)
   * @throws IllegalArgumentException if wavesPerCycle is below 1 or breakTicks is below 1
   */
  public ArenaCycle(int wavesPerCycle, int breakTicks) {
    if (wavesPerCycle < 1 || breakTicks < 1) {
      throw new IllegalArgumentException(
          "Need at least 1 wave per cycle and 1 break tick: " + wavesPerCycle + ", " + breakTicks);
    }
    this.wavesPerCycle = wavesPerCycle;
    this.breakTicks = breakTicks;
  }

  /**
   * Called when every enemy of the current wave is dead.
   *
   * @throws IllegalStateException if no wave is being fought
   */
  public void waveCleared() {
    if (phase != Phase.WAVE) {
      throw new IllegalStateException("No wave to clear during " + phase);
    }
    score++;
    if (isBossWave()) {
      phase = Phase.SHOP;
    } else {
      phase = Phase.BREAK;
      breakTicksLeft = breakTicks;
    }
  }

  /**
   * Advances the break timer by one tick; starts the next wave when it runs out.
   *
   * @param tick the current tick number
   */
  public void update(long tick) {
    if (phase == Phase.BREAK) {
      breakTicksLeft--;
      if (breakTicksLeft == 0) {
        startNextWave();
      }
    }
  }

  /**
   * Starts the next cycle when the player leaves the shop.
   *
   * @return false if the shop is not open, and then nothing changes
   */
  public boolean startNextCycle() {
    if (phase != Phase.SHOP) {
      return false;
    }
    startNextWave();
    return true;
  }

  private void startNextWave() {
    wave++;
    phase = Phase.WAVE;
  }

  /**
   * Tells whether the current wave is the boss of its cycle.
   *
   * @return true for the last wave of each cycle
   */
  public boolean isBossWave() {
    return wave % wavesPerCycle == 0;
  }

  /**
   * Returns the current wave number.
   *
   * @return the wave number, starting at 1
   */
  public int wave() {
    return wave;
  }

  /**
   * Returns the current phase.
   *
   * @return the phase
   */
  public Phase phase() {
    return phase;
  }

  /**
   * Returns the Arena score.
   *
   * @return the number of waves defeated
   */
  public int score() {
    return score;
  }
}
