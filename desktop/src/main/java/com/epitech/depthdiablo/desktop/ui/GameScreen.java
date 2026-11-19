package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.arena.ArenaCycle;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.debug.EventLaser;
import com.epitech.depthdiablo.debug.ViewMode;
import com.epitech.depthdiablo.debug.ViewSwitcher;
import com.epitech.depthdiablo.engine.FixedTimestep;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.CharacterFactory;
import com.epitech.depthdiablo.entity.Enemy;
import com.epitech.depthdiablo.entity.EnemyFactory;
import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.entity.Player;
import com.epitech.depthdiablo.entity.PlayerFactory;
import com.epitech.depthdiablo.entity.Wall;
import com.epitech.depthdiablo.input.PlayerOrders;
import com.epitech.depthdiablo.item.ItemFactory;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main gameplay screen rendering the TiledMap arena, minimap pass, and the Scene2D {@link Hud}. */
public final class GameScreen extends ScreenAdapter {

  private static final String DEFAULT_MAP_PATH = "Floor1.tmx";

  /** Name of the map layer that holds the enemy spawn tiles. */
  private static final String SPAWN_LAYER = "spawn";

  private final HudState hudState;

  private final Hud hud;

  private final OrthographicCamera worldCamera;

  private final OrthographicCamera minimapCamera;

  private final TiledMap tiledMap;

  private static final Color DEFAULT_CLEAR = new Color(0.05f, 0.05f, 0.07f, 1f);

  private final Color clearColor;

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
  private static final float CAMERA_ZOOM = 0.8f;

  private final KeyboardPlayerInput input = new KeyboardPlayerInput();

  private final PlayerOrders orders = new PlayerOrders();

  /** Builds every item of data/items.json: the players' weapons, and later loot and shop items. */
  private final ItemFactory items =
      new ItemFactory(new DataRegistry("items", () -> readData("items.json")));

  private final SpriteBatch batch;

  private final Player player;

  private final PlayerView playerView;

  /** The size of the minimap world in pixels. */
  private static final float MINIMAP_WORLD_SIZE = 480f;

  private final Vector2 previousPosition = new Vector2();

  private final Vector2 drawPosition = new Vector2();

  private final Vector2 velocity = new Vector2();

  /** F1 debug feature (GDD 1.1.2): which renderer draws the world. */
  private final ViewSwitcher viewSwitcher = new ViewSwitcher();

  private final Map<ViewMode, Renderer> renderers = new EnumMap<>(ViewMode.class);

  /** F2 debug feature (GDD 1.1.2): one line per event, drawn over any view. */
  private final EventLaser eventLaser = new EventLaser();

  private final EventLaserOverlay eventLaserOverlay = new EventLaserOverlay(eventLaser);

  /** Draws the enemies in the sprite view. */
  private final EnemyView enemyView;

  private static final float MINIMAP_BORDER = 4f;

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
    this.hud = new Hud(skin, hudState, app.getBatch());

    this.worldCamera = new OrthographicCamera();
    this.worldCamera.setToOrtho(false, GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT);

    this.minimapCamera = new OrthographicCamera();
    this.minimapCamera.setToOrtho(false, MINIMAP_WORLD_SIZE, MINIMAP_WORLD_SIZE);

    this.tiledMap = new TmxMapLoader().load(DEFAULT_MAP_PATH);
    this.clearColor = readBackgroundColor(tiledMap);

    this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, app.getBatch());
    this.batch = app.getBatch();
    this.worldCamera.zoom = CAMERA_ZOOM;
    addWalls();
    CharacterFactory characters = characterFactory();
    SpawnPoints spawns = findSpawnPoints();
    this.player = characters.create("knight", spawns.player(), Player.class);
    this.world.add(player);
    for (Vector2 spawn : spawns.enemies()) {
      Enemy rat = characters.create("rat", spawn, Enemy.class);
      rat.alert(player);
      world.add(rat);
    }
    this.playerView = new PlayerView();
    this.enemyView = new EnemyView();
    this.previousPosition.set(player.position());
    renderers.put(ViewMode.SPRITES, new SpriteRenderer(mapRenderer, batch, playerView, enemyView));
    renderers.put(ViewMode.XRAY, new XRayRenderer());
    renderers.put(ViewMode.HIGH_CONTRAST, new HighContrastRenderer());
    world.getEventBus().subscribeAll(eventLaser);
  }

  /** Returns the mutable HUD state so the game loop can sync HP and coins. */
  public HudState getHudState() {
    return hudState;
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(hud.getStage());
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      app.showMenu();
      return; // this screen is disposed now
    }
    handleQuickSlotHotkeys();
    if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
      viewSwitcher.cycle();
      LOG.info("View switched to {}", viewSwitcher.getMode());
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
      toggleEventLaser();
    }
    input.readInto(orders, worldCamera);
    playerView.lookTowards(orders.moveDirection().x);

    runLogic(delta);

    // HP at 0 ends the run (GDD 1.1 step 5): show the game over menu.
    if (player.isDead()) {
      int score = arenaMode ? arenaCycle.getScore() : 0; // Endless has no depth score yet
      app.showGameOver(score);
      return;
    }

    hudState.syncFromCharacter(player);

    Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // 1. The world, drawn by the renderer of the current F1 view
    followPlayer();
    velocity.set(player.position()).sub(previousPosition);
    renderers
        .get(viewSwitcher.getMode())
        .render(new Frame(worldCamera, world, player, drawPosition, velocity, delta));
    eventLaserOverlay.render(worldCamera);

    // 2. Render HUD overlay
    hud.render(delta);

    // 3. Render live minimap pass in bottom-right viewport
    renderMinimapPass();
  }

  private void toggleEventLaser() {
    if (eventLaser.isEnabled()) {
      eventLaser.disable();
    } else {
      eventLaser.enable();
    }
    LOG.info("Event laser {}", eventLaser.isEnabled() ? "on" : "off");
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
      if (eventLaser.isEnabled()) {
        eventLaser.onTick(world.getCurrentTick());
      }
      if (arenaMode) {
        arenaCycle.update();
      }
    }
    drawPosition.set(previousPosition).lerp(player.position(), timestep.getAlpha());
  }

  private void renderMinimapPass() {
    Rectangle box = hud.minimapScreenBounds();
    int inset = Math.round(MINIMAP_BORDER * box.width / 96f);
    HdpiUtils.glViewport(
        (int) box.x + inset,
        (int) box.y + inset,
        (int) box.width - 2 * inset,
        (int) box.height - 2 * inset);

    minimapCamera.position.set(worldCamera.position); // already centred on the player
    minimapCamera.update();
    mapRenderer.setView(minimapCamera);
    mapRenderer.render();

    batch.setProjectionMatrix(minimapCamera.combined);
    batch.begin();
    for (Entity entity : world.getEntities()) {
      if (entity instanceof Enemy enemy) {
        enemyView.draw(batch, enemy);
      }
    }
    playerView.draw(
        batch, player, drawPosition, 0f); // 0: the animation already advanced this frame
    batch.end();

    HdpiUtils.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }

  @Override
  public void resize(int width, int height) {
    hud.resize(width, height);
  }

  @Override
  public void dispose() {
    LOG.info("Game screen closed after {} ticks", world.getCurrentTick());
    hud.dispose();
    renderers.values().forEach(Renderer::dispose);
    eventLaserOverlay.dispose();
    playerView.dispose();
    enemyView.dispose();
    mapRenderer.dispose();
    tiledMap.dispose();
  }

  /** Builds the factory of every character, reading data/characters.json. */
  private CharacterFactory characterFactory() {
    return new CharacterFactory(
        new DataRegistry("characters", () -> readData("characters.json")),
        new PlayerFactory(items, orders, world),
        new EnemyFactory(world));
  }

  private static String readData(String file) {
    return Gdx.files.internal("data/" + file).readString("UTF-8");
  }

  /** Where the player and the enemies start, read from the map: the centres of their tiles. */
  private record SpawnPoints(Vector2 player, List<Vector2> enemies) {}

  /**
   * Returns the centre, in pixels, of the first tile whose "spawn" property is true, and of every
   * tile whose "spawn_enemy" property is true, in one pass over the map.
   */
  private SpawnPoints findSpawnPoints() {
    TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(SPAWN_LAYER);
    if (layer == null) {
      throw new IllegalStateException("No \"" + SPAWN_LAYER + "\" layer in " + DEFAULT_MAP_PATH);
    }
    float tile = GameConstants.TILE_SIZE;
    Vector2 player = null;
    List<Vector2> enemies = new ArrayList<>();
    for (int x = 0; x < layer.getWidth(); x++) {
      for (int y = 0; y < layer.getHeight(); y++) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null || cell.getTile() == null) {
          continue; // empty tile
        }
        MapProperties properties = cell.getTile().getProperties();
        Vector2 centre = new Vector2((x + 0.5f) * tile, (y + 0.5f) * tile);
        if (player == null && properties.get("spawn", false, Boolean.class)) {
          player = centre;
        }
        if (properties.get("spawn_enemy", false, Boolean.class)) {
          enemies.add(centre);
        }
      }
    }
    if (player == null) {
      throw new IllegalStateException("No tile with spawn=true in " + DEFAULT_MAP_PATH);
    }
    return new SpawnPoints(player, List.copyOf(enemies));
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
    Aabb body = player.getBodyBox();
    worldCamera.position.set(
        drawPosition.x + body.width() / 2f, drawPosition.y + body.height() / 2f, 0f);
    worldCamera.update();
  }

  /** Tiled's map "backgroundcolor" (e.g. "#281e1c"), or the default when the map has none. */
  private static Color readBackgroundColor(TiledMap map) {
    String hex = map.getProperties().get("backgroundcolor", String.class);
    if (hex == null) {
      return DEFAULT_CLEAR;
    } else return Color.valueOf(hex);
  }
}
