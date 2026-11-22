package com.epitech.depthdiablo.arena;

import com.epitech.depthdiablo.engine.Rewindable;
import com.epitech.depthdiablo.engine.StateBuffer;
import java.util.Objects;

/**
 * Runs the Arena loop on top of an {@link ArenaCycle}: spawns each wave when it starts, tells the
 * cycle when the wave is cleared, and lets the fixed break run between two waves. It is in the
 * rewind photo (R) with its cycle, so a rewind to before a wave spawned spawns it again instead of
 * counting it as cleared.
 */
public final class WaveDirector implements Rewindable {

  private final ArenaCycle cycle;

  private final WavePlan plan;

  private final EnemySpawner spawner;

  /** The last wave whose enemies were spawned, 0 before the first one. */
  private int spawnedWave;

  /**
   * Creates the director of one Arena run.
   *
   * @param cycle the waves, breaks and score of the run; must not be null
   * @param plan which enemies each wave holds; must not be null
   * @param spawner puts each enemy in the world; must not be null
   */
  public WaveDirector(ArenaCycle cycle, WavePlan plan, EnemySpawner spawner) {
    this.cycle = Objects.requireNonNull(cycle, "The arena cycle must not be null");
    this.plan = Objects.requireNonNull(plan, "The wave plan must not be null");
    this.spawner = Objects.requireNonNull(spawner, "The enemy spawner must not be null");
  }

  /**
   * Advances the Arena by one tick, called once per world tick.
   *
   * <p>A wave that starts is spawned on that tick, and the count is read from the next tick on: the
   * new enemies are not in {@code enemiesAlive} yet. A spawned wave with no enemy alive is cleared.
   * The cycle then advances, which only counts down during a break.
   *
   * @param enemiesAlive the enemies of the world that are not dead, 0 or more
   * @throws IllegalArgumentException if {@code enemiesAlive} is negative
   */
  public void onTick(int enemiesAlive) {
    if (enemiesAlive < 0) {
      throw new IllegalArgumentException(
          "The number of enemies alive must not be negative, was " + enemiesAlive);
    }
    int wave = cycle.getWaveNumber();
    if (cycle.getPhase() == ArenaCycle.Phase.WAVE && spawnedWave != wave) {
      for (String id : plan.enemiesOf(wave, cycle.isBossWave())) {
        spawner.spawn(id, WavePlan.KING_ID.equals(id));
      }
      spawnedWave = wave;
      return;
    }
    if (cycle.getPhase() == ArenaCycle.Phase.WAVE && enemiesAlive == 0) {
      cycle.waveCleared();
    }
    cycle.update();
  }

  /**
   * Writes the last wave whose enemies were spawned.
   *
   * @param out the photo being taken
   */
  @Override
  public void writeState(StateBuffer out) {
    out.writeInt(spawnedWave);
  }

  /**
   * Puts back what {@link #writeState} wrote.
   *
   * @param in the photo being restored
   */
  @Override
  public void readState(StateBuffer in) {
    spawnedWave = in.readInt();
  }
}
