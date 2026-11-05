package com.epitech.depthdiablo.random;

/** Source of randomness injected everywhere, so the same seed gives the same run (Q114). */
public interface RandomSource {
  /**
   * Returns a random int.
   *
   * @param bound exclusive upper bound, must be positive
   * @return a value from 0 (inclusive) to the bound (exclusive)
   * @throws IllegalArgumentException if the bound is not positive
   */
  int nextInt(int bound);

  /**
   * Returns a random double.
   *
   * @return a value from 0.0 (inclusive) to 1.0 (exclusive)
   */
  double nextDouble();

  /**
   * Returns the seed this source was created with.
   *
   * @return the seed
   */
  long seed();
}
