package com.epitech.depthdiablo.debug;

/**
 * The F1 debug feature: picks which view the screen draws (GDD 1.1.2). It only holds a choice, so
 * the game logic runs exactly the same in every view. Pattern: Strategy (the screen picks the
 * renderer that matches {@link #getMode()}).
 */
public final class ViewSwitcher implements DebugFeature {

  private boolean enabled = true;

  private ViewMode mode = ViewMode.SPRITES;

  /** Creates the F1 view switcher, on, showing the sprites. */
  public ViewSwitcher() {
    // its fields start with their declared values
  }

  @Override
  public String getName() {
    return "View switcher";
  }

  /**
   * Returns the view to draw.
   *
   * @return the chosen view, or {@link ViewMode#SPRITES} while the feature is disabled
   */
  public ViewMode getMode() {
    return enabled ? mode : ViewMode.SPRITES;
  }

  @Override
  public void enable() {
    enabled = true;
  }

  @Override
  public void disable() {
    enabled = false;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void onTick(long tick) {
    // Nothing to do per tick: the view only changes when F1 is pressed.
  }

  /** Shows the next view. Does nothing while the feature is disabled. */
  public void cycle() {
    if (enabled) {
      mode = mode.next();
    }
  }
}
