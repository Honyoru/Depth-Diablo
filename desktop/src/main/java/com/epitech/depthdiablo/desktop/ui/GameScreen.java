package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.arena.ArenaCycle;
import com.epitech.depthdiablo.engine.FixedTimestep;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main gameplay screen rendering the TiledMap arena, minimap pass, and the Scene2D {@link Hud}. */
public final class GameScreen extends ScreenAdapter {

  private static final String DEFAULT_MAP_PATH = "arena_mapV1.tmx";

  private final HudState hudState;
  private final Hud hud;
  private final OrthographicCamera worldCamera;
  private final OrthographicCamera minimapCamera;
  private final TiledMap tiledMap;
  private final OrthogonalTiledMapRenderer mapRenderer;
  private final GameApplication app;
  private static final Logger LOG = LoggerFactory.getLogger(GameScreen.class);

  /** The boss comes every 5th wave. */
  private static final int WAVES_PER_CYCLE = 5;

  /** Pause between two normal waves: 5 seconds. */
  private static final int BREAK_TICKS = 5 * GameConstants.TICKS_PER_SECOND;

  private final World world = new World();
  private final FixedTimestep timestep = new FixedTimestep();
  private final ArenaCycle arenaCycle = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
  private final boolean arenaMode;

  /**
   * Creates the gameplay screen with the selected mode and hero.
   *
   * @param app the parent game application; must not be {@code null}
   * @param skin the loaded UI skin; must not be {@code null}
   * @param mode the selected game mode; must not be {@code null}
   * @param hero the selected playable hero; must not be {@code null}
   */
  public GameScreen(
      GameApplication app, Skin skin, MenuFlowState.GameMode mode, MenuFlowState.HeroType hero) {
    Objects.requireNonNull(app, "app");
    Objects.requireNonNull(skin, "skin");
    Objects.requireNonNull(mode, "mode");
    Objects.requireNonNull(hero, "hero");

    this.arenaMode = Objects.requireNonNull(mode, "mode") == MenuFlowState.GameMode.ARENA;

    this.app = Objects.requireNonNull(app, "app");
    this.hudState = new HudState();
    this.hud = new Hud(skin, hudState, app.batch());

    this.worldCamera = new OrthographicCamera();
    this.worldCamera.setToOrtho(false, GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT);

    this.minimapCamera = new OrthographicCamera();
    this.minimapCamera.setToOrtho(
        false, GameConstants.VIRTUAL_WIDTH * 2f, GameConstants.VIRTUAL_HEIGHT * 2f);

    this.tiledMap = new TmxMapLoader().load(DEFAULT_MAP_PATH);
    this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, app.batch());
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(hud.stage());
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      app.showMenu();
      return; // this screen is disposed now
    }
    handleQuickSlotHotkeys();
    runLogic(delta);

    Gdx.gl.glClearColor(0.05f, 0.05f, 0.07f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // 1. Render main TiledMap world
    worldCamera.update();
    mapRenderer.setView(worldCamera);
    mapRenderer.render();

    // 2. Render HUD overlay
    hud.render(delta);

    // 3. Render live minimap pass in bottom-right viewport
    renderMinimapPass();
  }

  private void handleQuickSlotHotkeys() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
      hudState.selectSlot(0);
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
      hudState.selectSlot(1);
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
      hudState.selectSlot(2);
    }
  }

  private void runLogic(float delta) {
    int ticks = timestep.advance(delta);
    for (int i = 0; i < ticks; i++) {
      world.tick();
      if (arenaMode) {
        arenaCycle.update();
      }
    }
  }

  private void renderMinimapPass() {
    int screenW = Gdx.graphics.getWidth();
    int screenH = Gdx.graphics.getHeight();
    int pad = Math.round(12f * screenW / GameConstants.VIRTUAL_WIDTH);
    int size = Math.round(96f * screenW / GameConstants.VIRTUAL_WIDTH);

    Gdx.gl.glViewport(screenW - size - pad, pad, size, size);
    minimapCamera.update();
    mapRenderer.setView(minimapCamera);
    mapRenderer.render();
    Gdx.gl.glViewport(0, 0, screenW, screenH);
  }

  /** Returns the mutable HUD state so the game loop can sync HP and coins. */
  public HudState hudState() {
    return hudState;
  }

  @Override
  public void resize(int width, int height) {
    hud.resize(width, height);
  }

  @Override
  public void dispose() {
    LOG.info("Game screen closed after {} ticks", world.currentTick());
    hud.dispose();
    mapRenderer.dispose();
    tiledMap.dispose();
  }
}
