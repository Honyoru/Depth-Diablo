package com.epitech.depthdiablo.debug;

/** The views the F1 switcher loops through (GDD 1.1.2). */
public enum ViewMode {
  /** The normal game: map tiles and sprites. */
  SPRITES,
  /** Hurtboxes, hitboxes and velocity vectors drawn over a dark screen. */
  XRAY,
  /** Flat shapes in strong colours, also an accessibility view. */
  HIGH_CONTRAST;

  /**
   * Returns the view F1 shows after this one.
   *
   * @return the next view, back to the first after the last
   */
  public ViewMode next() {
    ViewMode[] all = values();
    return all[(ordinal() + 1) % all.length];
  }
}
