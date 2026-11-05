package com.epitech.depthdiablo.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.epitech.depthdiablo.GameConstants;

/** Starts the desktop (LWJGL3) application. */
public final class DesktopLauncher {

  /** The 960 x 540 virtual screen is shown at x2 (Q60). */
  private static final int SCALE = 2;

  private DesktopLauncher() {}

  /**
   * Entry point.
   *
   * @param args unused
   */
  public static void main(String[] args) {
    if (StartupHelper.startNewJvmIfRequired()) {
      return;
    }
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle("Depth Diablo");
    config.useVsync(true);
    config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
    config.setWindowedMode(
        GameConstants.VIRTUAL_WIDTH * SCALE, GameConstants.VIRTUAL_HEIGHT * SCALE);
    config.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
    new Lwjgl3Application(new GameApplication(), config);
  }
}
