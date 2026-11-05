package com.epitech.depthdiablo.debug;

/** One engine showcase feature (rewind, view switcher, event laser, editor); see GDD 1.1.2. */
public interface DebugFeature {
  /**
   * Returns the feature name.
   *
   * @return a short name for logs and menus
   */
  String name();

  /** Switches the feature on. */
  void enable();

  /** Switches the feature off. The game must run exactly the same without it. */
  void disable();

  /**
   * Tells whether the feature is on.
   *
   * @return true while enabled
   */
  boolean isEnabled();

  /**
   * Hook into the game loop, called once per logic tick while the feature is enabled.
   *
   * @param tick the number of the current logic tick
   */
  void onTick(long tick);
}
