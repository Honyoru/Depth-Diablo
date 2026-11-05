package com.epitech.depthdiablo.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.epitech.depthdiablo.engine.FixedTimestep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Root application: runs the fixed-step logic loop, then draws. Rendering lives here, in desktop;
 * game logic lives in core, which never touches graphics, audio or input (GDD 3.1.1).
 */
public final class GameApplication extends ApplicationAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(GameApplication.class);

  private final FixedTimestep timestep = new FixedTimestep();
  private long tickCount;

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
    ScreenUtils.clear(Color.BLACK);
  }

  @Override
  public void dispose() {
    LOG.info("Game stopped after {} ticks", tickCount);
  }
}
