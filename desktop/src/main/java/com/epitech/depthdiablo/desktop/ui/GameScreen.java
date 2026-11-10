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
import java.util.Objects;

/** Main gameplay screen rendering the TiledMap arena, minimap pass, and the Scene2D {@link Hud}. */
public final class GameScreen extends ScreenAdapter {

  private static final String DEFAULT_MAP_PATH = "arena_map1.tmx";

  private final HudState hudState;
  private final Hud hud;
  private final OrthographicCamera worldCamera;
  private final OrthographicCamera minimapCamera;
  private TiledMap tiledMap;
  private OrthogonalTiledMapRenderer mapRenderer;

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

    this.hudState = new HudState();
    this.hud = new Hud(skin, hudState, app.batch());

    this.worldCamera = new OrthographicCamera();
    this.worldCamera.setToOrtho(false, GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT);

    this.minimapCamera = new OrthographicCamera();
    this.minimapCamera.setToOrtho(
        false, GameConstants.VIRTUAL_WIDTH * 2f, GameConstants.VIRTUAL_HEIGHT * 2f);

    if (Gdx.files.internal(DEFAULT_MAP_PATH).exists()) {
      this.tiledMap = new TmxMapLoader().load(DEFAULT_MAP_PATH);
      this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, app.batch());
    }
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(hud.stage());
  }

  @Override
  public void render(float delta) {
    handleQuickSlotHotkeys();

    Gdx.gl.glClearColor(0.05f, 0.05f, 0.07f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // 1. Render main TiledMap world
    worldCamera.update();
    if (mapRenderer != null) {
      mapRenderer.setView(worldCamera);
      mapRenderer.render();
    }

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

  private void renderMinimapPass() {
    if (mapRenderer == null) {
      return;
    }
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
    hud.dispose();
    if (mapRenderer != null) {
      mapRenderer.dispose();
    }
    if (tiledMap != null) {
      tiledMap.dispose();
    }
  }
}
