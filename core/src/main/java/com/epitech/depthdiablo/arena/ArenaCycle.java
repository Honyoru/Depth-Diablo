package com.epitech.depthdiablo.arena;

import com.epitech.depthdiablo.engine.Rewindable;
import com.epitech.depthdiablo.engine.StateBuffer;

/**
 * An arena cycle consists of waves with breaks between them, ending in a boss wave followed by a
 * shop, and tracks the number of waves defeated as the score. It is in the rewind photo (R): a
 * rewind puts back the wave, the phase, the break timer and the score with the world.
 */
public final class ArenaCycle implements Rewindable {

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

  private final boolean shopEnabled;

  /**
   * Creates a new arena cycle whose boss wave opens the shop.
   *
   * @param wavesPerCycle the number of normal waves in the cycle + 1 boss wave
   * @param breakTicks the number of ticks spent between two normal waves
   * @throws IllegalArgumentException if a provided value is lower than 1
   */
  public ArenaCycle(int wavesPerCycle, int breakTicks) {
    this(wavesPerCycle, breakTicks, true);
  }

  /**
   * Creates a new arena cycle.
   *
   * @param wavesPerCycle the number of normal waves in the cycle + 1 boss wave
   * @param breakTicks the number of ticks spent between two waves
   * @param shopEnabled false: a cleared boss wave starts a break like any wave, no shop
   * @throws IllegalArgumentException if a provided value is lower than 1
   */
  public ArenaCycle(int wavesPerCycle, int breakTicks, boolean shopEnabled) {
    if (wavesPerCycle < 1) {
      throw new IllegalArgumentException("wavesPerCycle must be >= 1: " + wavesPerCycle);
    }
    if (breakTicks < 1) {
      throw new IllegalArgumentException("breakTicks must be >= 1: " + breakTicks);
    }
    this.wavesPerCycle = wavesPerCycle;
    this.breakTicks = breakTicks;
    this.shopEnabled = shopEnabled;
    this.wave = 1;
    this.phase = Phase.WAVE;
    this.breakTicksLeft = 0;
    this.score = 0;
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
   * Gets the ticks left before the next wave, so the HUD can show a countdown.
   *
   * @return the ticks left in the break; 0 outside a break
   */
  public int getBreakTicksLeft() {
    return breakTicksLeft;
  }

  /**
   * Gets the number of waves defeated.
   *
   * @return the score
   */
  public int getScore() {
    return score;
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
    if (isBossWave() && shopEnabled) {
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
   * Writes what changes during a run: the wave, the phase, the break ticks left and the score.
   *
   * @param out the photo being taken
   */
  @Override
  public void writeState(StateBuffer out) {
    out.writeInt(wave);
    out.writeInt(phase.ordinal());
    out.writeInt(breakTicksLeft);
    out.writeInt(score);
  }

  /**
   * Puts back what {@link #writeState} wrote, in the same order.
   *
   * @param in the photo being restored
   */
  @Override
  public void readState(StateBuffer in) {
    wave = in.readInt();
    phase = Phase.values()[in.readInt()];
    breakTicksLeft = in.readInt();
    score = in.readInt();
  }
}
