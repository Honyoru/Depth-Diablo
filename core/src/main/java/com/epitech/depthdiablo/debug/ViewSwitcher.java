package com.epitech.depthdiablo.debug;

/**
 * The F1 debug feature: picks which view the screen draws (GDD 1.1.2). It only holds a choice, so
 * the game logic runs exactly the same in every view. Pattern: Strategy (the screen picks the
 * renderer that matches {@link #mode()}).
 */
public final class ViewSwitcher implements DebugFeature {

  @Override
  public String name() {
    throw new UnsupportedOperationException("name is not implemented yet");
  }

  @Override
  public void enable() {
    throw new UnsupportedOperationException("enable is not implemented yet");
  }

  @Override
  public void disable() {
    throw new UnsupportedOperationException("disable is not implemented yet");
  }

  @Override
  public boolean isEnabled() {
    throw new UnsupportedOperationException("isEnabled is not implemented yet");
  }

  @Override
  public void onTick(long tick) {
    throw new UnsupportedOperationException("onTick is not implemented yet");
  }

  /** Shows the next view. Does nothing while the feature is disabled. */
  public void cycle() {
    throw new UnsupportedOperationException("cycle is not implemented yet");
  }

  /**
   * Returns the view to draw.
   *
   * @return the chosen view, or {@link ViewMode#SPRITES} while the feature is disabled
   */
  public ViewMode mode() {
    throw new UnsupportedOperationException("mode is not implemented yet");
  }
}
