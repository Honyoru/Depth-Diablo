package com.epitech.depthdiablo.random;

import java.util.SplittableRandom;

/** Deterministic {@link RandomSource}: the same seed always replays the same sequence. */
public final class SeededRandomSource implements RandomSource {
  private final long initialSeed;
  private final SplittableRandom random;

  /**
   * Creates a source from a seed.
   *
   * @param seed the seed that fixes the whole sequence
   */
  public SeededRandomSource(long seed) {
    this.initialSeed = seed;
    this.random = new SplittableRandom(seed);
  }

  @Override
  public int nextInt(int bound) {
    return random.nextInt(bound);
  }

  @Override
  public double nextDouble() {
    return random.nextDouble();
  }

  @Override
  public long seed() {
    return initialSeed;
  }
}
