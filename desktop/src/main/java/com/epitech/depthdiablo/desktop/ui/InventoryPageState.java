package com.epitech.depthdiablo.desktop.ui;

/**
 * Pure-Java model of the inventory page (GDD 1.4, Q58): whether it is open. The game is paused
 * while it is open; closing it cancels what was being done in it.
 */
public final class InventoryPageState {
  private boolean open;

  /**
   * Tells whether the page is open, so the game is paused.
   *
   * @return true while the page is shown
   */
  public boolean isOpen() {
    return open;
  }

  /** Opens the page if it is closed, closes it otherwise (key I). */
  public void toggle() {
    if (open) {
      close();
    } else {
      open = true;
    }
  }

  /** Closes the page. */
  public void close() {
    open = false;
  }
}
