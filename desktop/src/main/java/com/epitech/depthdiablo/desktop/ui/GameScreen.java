package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.arena.ArenaCycle;
import com.epitech.depthdiablo.engine.FixedTimestep;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.BaseStats;
import com.epitech.depthdiablo.entity.Knight;
import com.epitech.depthdiablo.entity.Player;
import com.epitech.depthdiablo.entity.Stat;
import com.epitech.depthdiablo.entity.Wall;
import java.util.EnumMap;
import java.util.Map;
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

  /** Pause between two normal waves: 15 seconds. */
  private static final int BREAK_TICKS = 15 * GameConstants.TICKS_PER_SECOND;

  private final World world = new World();
  private final FixedTimestep timestep = new FixedTimestep();
  private final ArenaCycle arenaCycle = new ArenaCycle(WAVES_PER_CYCLE, BREAK_TICKS);
  private final boolean arenaMode;

  /** Below 1 the camera shows less of the world, so everything looks bigger. */
  private static final float CAMERA_ZOOM = 0.5f;

  private final KeyboardPlayerInput input = new KeyboardPlayerInput();
  private final SpriteBatch batch;
  private final Player player;
  private final PlayerView playerView;

  /** The size of the minimap world in pixels. */
  private static final float MINIMAP_WORLD_SIZE = 480f;

  private final Vector2 previousPosition = new Vector2();
  private final Vector2 drawPosition = new Vector2();

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
    Objects.requireNonNull(skin, "The UI skin must not be null");
    Objects.requireNonNull(hero, "The selected hero must not be null");

    this.arenaMode =
        Objects.requireNonNull(mode, "The selected game mode must not be null")
            == MenuFlowState.GameMode.ARENA;

    this.app = Objects.requireNonNull(app, "The game application must not be null");
    this.hudState = new HudState();
    this.hud = new Hud(skin, hudState, app.batch());

    this.worldCamera = new OrthographicCamera();
    this.worldCamera.setToOrtho(false, GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT);

    this.minimapCamera = new OrthographicCamera();
    this.minimapCamera.setToOrtho(false, MINIMAP_WORLD_SIZE, MINIMAP_WORLD_SIZE);

    this.tiledMap = new TmxMapLoader().load(DEFAULT_MAP_PATH);
    this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, app.batch());
    this.batch = app.batch();
    this.worldCamera.zoom = CAMERA_ZOOM;
    addWalls();
    this.player = spawnKnight(input, world);
    this.world.add(player);
    this.playerView = new PlayerView();
    this.previousPosition.set(player.position());
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
    playerView.lookTowards(input.moveDirection().x);

    runLogic(delta);
    hudState.syncFromCharacter(player);

    Gdx.gl.glClearColor(0.05f, 0.05f, 0.07f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // 1. Map, then the player on top of it
    followPlayer();
    mapRenderer.setView(worldCamera);
    mapRenderer.render();

    batch.setProjectionMatrix(worldCamera.combined);
    batch.begin();
    playerView.draw(batch, player, drawPosition, delta);
    batch.end();

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
      previousPosition.set(player.position());
      world.tick();
      if (arenaMode) {
        arenaCycle.update();
      }
    }
    drawPosition.set(previousPosition).lerp(player.position(), timestep.alpha());
  }

  private void renderMinimapPass() {
    int screenW = Gdx.graphics.getWidth();
    int screenH = Gdx.graphics.getHeight();
    int pad = Math.round(12f * screenW / GameConstants.VIRTUAL_WIDTH);
    int size = Math.round(96f * screenW / GameConstants.VIRTUAL_WIDTH);

    Gdx.gl.glViewport(screenW - size - pad, pad, size, size);
    minimapCamera.position.set(worldCamera.position); // already centred on the player
    minimapCamera.update();
    mapRenderer.setView(minimapCamera);
    mapRenderer.render();

    batch.setProjectionMatrix(minimapCamera.combined);
    batch.begin();
    playerView.draw(
        batch, player, drawPosition, 0f); // 0: the animation already advanced this frame
    batch.end();

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
    playerView.dispose();
    mapRenderer.dispose();
    tiledMap.dispose();
  }

  private static Player spawnKnight(KeyboardPlayerInput input, World world) {
    Map<Stat, Float> stats = new EnumMap<>(Stat.class);
    stats.put(Stat.MAX_HP, 100f);
    stats.put(Stat.ATTACK, 10f);
    stats.put(Stat.DEFENSE, 1f);
    stats.put(Stat.SPEED, 120f);
    stats.put(Stat.ATTACK_SPEED, 1f);
    stats.put(Stat.LUCK, 0f);
    Aabb body = new Aabb(466f, 306f, 28f, 28f); // centre of the 960 x 640 map, size of the art
    return new Knight(
        body, new BaseStats(stats), body, GameConstants.TICKS_PER_SECOND / 2, input, world);
  }

  /** Adds one blocking {@link Wall} per map tile whose "collides" property is true. */
  private void addWalls() {
    TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
    float tile = GameConstants.TILE_SIZE;
    for (int x = 0; x < layer.getWidth(); x++) {
      for (int y = 0; y < layer.getHeight(); y++) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null || cell.getTile() == null) {
          continue; // empty tile
        }
        boolean collides = cell.getTile().getProperties().get("collides", false, Boolean.class);
        if (collides) {
          world.add(new Wall(new Aabb(x * tile, y * tile, tile, tile)));
        }
      }
    }
  }

  private void followPlayer() {
    Aabb body = player.bodyBox();
    worldCamera.position.set(
        drawPosition.x + body.width() / 2f, drawPosition.y + body.height() / 2f, 0f);
    worldCamera.update();
  }
}
