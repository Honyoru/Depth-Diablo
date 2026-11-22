package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.epitech.depthdiablo.GameConstants;
import java.util.List;
import java.util.Objects;

/** Scene2D HUD displaying the health bar, coin counter, 3 quick-access slots, and minimap frame. */
public final class Hud implements Disposable {

  /** Size of the HUD texts: the skin's font is 32 px, too big for a corner. */
  private static final float TEXT_SCALE = 0.6f;

  /** Size of the key letters on the hotbar. */
  private static final float KEY_SCALE = 0.4f;

  /** Size of the texts of the help panel. */
  private static final float HELP_SCALE = 0.45f;

  /** Every control, key then action, in AZERTY: libGDX reads key positions (Z Q S D = W A S D). */
  private static final List<String[]> CONTROLS =
      List.of(
          new String[] {"Z Q S D", "Move"},
          new String[] {"Left click", "Attack"},
          new String[] {"Shift", "Dash"},
          new String[] {"Space", "Dodge roll (invincible)"},
          new String[] {"E", "Pick up / shop"},
          new String[] {"1 2 3", "Select a consumable"},
          new String[] {"A", "Use it"},
          new String[] {"I", "Inventory (pause)"},
          new String[] {"Esc", "Back to the menu"},
          new String[] {"R (hold)", "Rewind"},
          new String[] {"F1 / F2 / F5", "Debug views / event laser / reload data"},
          new String[] {"H", "Show or hide this help"});

  /** Size of the BOSS title above its life bar. */
  private static final float BOSS_TITLE_SCALE = 0.7f;

  private final Stage stage;

  private final HudState state;

  private final ProgressBar healthBar;

  private final ImageTextButton coinCounter;

  private final ImageButton[] slotButtons = new ImageButton[HudState.SLOT_COUNT];

  /** Each slot: its frame button with the picture of its consumable over it, scaled as one. */
  private final Stack[] slotStacks = new Stack[HudState.SLOT_COUNT];

  /** The picture of the consumable in each slot, drawn over the slot frame. */
  private final Image[] slotIcons = new Image[HudState.SLOT_COUNT];

  /** The sprite id each icon shows, to change its picture only when it changes. */
  private final String[] shownSpriteIds = new String[HudState.SLOT_COUNT];

  private final SpriteTextures textures;

  private final Image minimapBox;

  /** Bottom left: the wave in the Arena, the floor in Endless. */
  private final Label progressLabel;

  /** Top centre: the Arena countdown or the shop; empty during a wave. */
  private final Label timerLabel;

  /** Top centre during a boss fight: the BOSS title and its life bar. */
  private final Table bossTable;

  private final ProgressBar bossBar;

  /** "E: pick up", above the item in range; placed freely on the stage, not in the table. */
  private final Label pickUpHint;

  /** Every control, at the centre, while H is on. */
  private final Table helpPanel;

  /** "H: controls", bottom left, during the first seconds of a run. */
  private final Label helpHint;

  /**
   * Builds the Scene2D HUD table using the provided skin and state model.
   *
   * @param skin the loaded UI skin; must not be {@code null}
   * @param state the HUD state model; must not be {@code null}
   * @param batch the shared sprite batch; must not be {@code null}
   * @param textures the item pictures; the game screen owns and disposes them
   */
  public Hud(Skin skin, HudState state, Batch batch, SpriteTextures textures) {
    Objects.requireNonNull(skin, "The UI skin must not be null");
    this.state = Objects.requireNonNull(state, "The HUD state must not be null");
    this.textures = Objects.requireNonNull(textures, "textures is null");
    this.stage =
        new Stage(
            new FitViewport(GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT),
            Objects.requireNonNull(batch, "The sprite batch must not be null"));

    this.healthBar = new ProgressBar(0f, 100f, 1f, false, skin, "default-horizontal");
    this.coinCounter = new ImageTextButton("0", skin, "default");

    Table quickAccessTable = new Table();
    for (int i = 0; i < HudState.SLOT_COUNT; i++) {
      final int slotIndex = i;
      String styleName = (i == 1) ? "quick_access2" : "quick_access";
      ImageButton slotBtn = new ImageButton(skin, styleName);
      slotBtn.addListener(
          new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
              state.selectSlot(slotIndex);
            }
          });
      slotButtons[i] = slotBtn;
      Image icon = new Image();
      icon.setTouchable(Touchable.disabled);
      slotIcons[i] = icon;
      Table iconCell = new Table();
      iconCell.add(icon).size(28f, 28f);
      iconCell.setTouchable(Touchable.disabled);
      Label key = new Label(String.valueOf(i + 1), skin);
      key.setFontScale(KEY_SCALE);
      Table keyCell = new Table();
      keyCell.add(key).expand().top().left().pad(2f);
      keyCell.setTouchable(Touchable.disabled);
      slotStacks[i] = new Stack(slotBtn, iconCell, keyCell);
      quickAccessTable.add(slotStacks[i]).size(40f, 40f).pad(4f);
    }
    Label useKey = new Label("A: use", skin); // the AZERTY key; Q on QWERTY (key positions)
    useKey.setFontScale(KEY_SCALE);
    quickAccessTable.add(useKey).bottom().padLeft(6f);

    this.minimapBox = new Image(skin.get("minimap_bg", TextureRegionDrawable.class));
    this.progressLabel = new Label("", skin);
    progressLabel.setFontScale(TEXT_SCALE);
    this.helpHint = new Label("H: controls", skin);
    helpHint.setFontScale(KEY_SCALE);
    this.helpPanel = helpPanel(skin);
    this.timerLabel = new Label("", skin);
    timerLabel.setFontScale(TEXT_SCALE);
    Label bossTitle = new Label("BOSS", skin);
    bossTitle.setFontScale(BOSS_TITLE_SCALE);
    bossTitle.setColor(Color.RED);
    this.bossBar = new ProgressBar(0f, 100f, 1f, false, skin, "default-horizontal");
    this.bossTable = new Table();
    bossTable.add(bossTitle).row();
    bossTable.add(bossBar).width(320f).height(20f);
    bossTable.setVisible(false);
    Table topCentre = new Table();
    topCentre.add(timerLabel).row();
    topCentre.add(bossTable);

    Table root = new Table();
    root.setFillParent(true);

    // Top row: Health bar (top-left) and Coin counter (top-right)
    root.add(healthBar).width(180f).height(24f).top().left().pad(12f);
    root.add(topCentre).expandX().top().padTop(14f);
    root.add(coinCounter).top().right().pad(12f);
    root.row();

    // Middle spacer
    root.add().expand().colspan(3);
    root.row();

    // Bottom row: Empty bottom-left, 3 quick slots (bottom-center), Minimap (bottom-right)
    Table bottomLeft = new Table();
    bottomLeft.add(helpHint).left().row();
    bottomLeft.add(progressLabel).left();
    root.add(bottomLeft).bottom().left().pad(12f).width(96f);
    root.add(quickAccessTable).bottom().padBottom(12f);
    root.add(minimapBox).size(96f, 96f).bottom().right().pad(12f);

    stage.addActor(root);
    this.pickUpHint = new Label("E: pick up", skin);
    pickUpHint.setFontScale(KEY_SCALE);
    pickUpHint.setVisible(false);
    stage.addActor(pickUpHint);
    helpPanel.setVisible(false);
    Table centre = new Table();
    centre.setFillParent(true);
    centre.setTouchable(Touchable.disabled);
    centre.add(helpPanel);
    stage.addActor(centre);
  }

  /**
   * Converts a point of the window, y up as the world camera gives it, to the HUD's coordinates,
   * letterbox included.
   *
   * @param x the point's x in window pixels
   * @param y the point's y in window pixels, from the bottom
   * @return the same point on the HUD
   */
  public Vector2 windowToHud(float x, float y) {
    return stage.screenToStageCoordinates(new Vector2(x, Gdx.graphics.getHeight() - y));
  }

  /**
   * Returns the Scene2D stage so it can be added to an {@code InputMultiplexer}.
   *
   * @return the HUD stage
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * Updates Scene2D widgets from {@link HudState} and renders the HUD stage.
   *
   * @param delta frame time in seconds
   */
  public void render(float delta) {
    healthBar.setValue(state.getHealthPercentage());
    bossTable.setVisible(state.isBossShown());
    bossBar.setValue(state.getBossHealthPercentage());
    helpPanel.setVisible(state.isHelpShown());
    helpHint.setVisible(state.isHelpHintShown());
    pickUpHint.setVisible(state.isPickUpHintShown());
    pickUpHint.setPosition(
        state.getPickUpHintX() - pickUpHint.getPrefWidth() / 2f, state.getPickUpHintY());
    coinCounter.setText(String.valueOf(state.getCoins()));
    progressLabel.setText(state.getProgressText());
    timerLabel.setText(state.getTimerText());
    for (int i = 0; i < HudState.SLOT_COUNT; i++) {
      float scale = (i == state.getActiveSlot()) ? 1.15f : 1.0f;
      slotStacks[i].setTransform(true);
      slotStacks[i].setOrigin(Align.center); // grows from its centre, not from its corner
      slotStacks[i].setScale(scale);
      showHotbarIcon(i);
    }
    stage.getViewport().apply(); // its 16:9 area, the one its clicks are read in
    stage.act(delta);
    stage.draw();
  }

  /** Builds the help panel: one row per control, the key on the left and its action. */
  private static Table helpPanel(Skin skin) {
    Table panel = new Table(skin);
    panel.setBackground("inventory_card_bg");
    panel.pad(14f);
    for (String[] control : CONTROLS) {
      Label key = new Label(control[0], skin);
      key.setFontScale(HELP_SCALE);
      key.setColor(Color.GOLD);
      Label action = new Label(control[1], skin);
      action.setFontScale(HELP_SCALE);
      panel.add(key).left().padRight(18f);
      panel.add(action).left().row();
    }
    return panel;
  }

  /** Changes the picture of a slot icon when its consumable changed. */
  private void showHotbarIcon(int slot) {
    String spriteId = state.getHotbarSpriteId(slot);
    if (!spriteId.equals(shownSpriteIds[slot])) {
      slotIcons[slot].setDrawable(new TextureRegionDrawable(textures.get(spriteId)));
      shownSpriteIds[slot] = spriteId;
    }
  }

  /**
   * Updates the HUD viewport when the OS window is resized.
   *
   * @param width new window width in pixels
   * @param height new window height in pixels
   */
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void dispose() {
    stage.dispose();
  }

  Rectangle minimapScreenBounds() {
    Vector2 corner = minimapBox.localToStageCoordinates(new Vector2(0f, 0f));
    stage.getViewport().project(corner); // virtual -> screen pixels, letterbox included
    float scale = stage.getViewport().getScreenWidth() / (float) GameConstants.VIRTUAL_WIDTH;
    return new Rectangle(
        corner.x, corner.y, minimapBox.getWidth() * scale, minimapBox.getHeight() * scale);
  }
}
