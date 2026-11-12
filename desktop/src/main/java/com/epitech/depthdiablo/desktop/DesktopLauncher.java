package com.epitech.depthdiablo.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.engine.FixedTimestep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Starts the desktop (LWJGL3) application. */
public final class DesktopLauncher extends ApplicationAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(DesktopLauncher.class);

  private final FixedTimestep timestep = new FixedTimestep();
  private long tickCount;

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
    new Lwjgl3Application(new com.epitech.depthdiablo.desktop.ui.GameApplication(), config);
  }

  @Override
  public void create() {
    LOG.info("Game started");
  }

  @Override
  public void render() {
    int ticks = timestep.advance(Gdx.graphics.getDeltaTime());
    for (int i = 0; i < ticks; i++) {
      tickCount++;
    }
  }

  @Override
  public void dispose() {
    LOG.info("Game stopped after {} ticks", tickCount);
  }
}
