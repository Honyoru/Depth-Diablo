package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
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
import java.util.Objects;

/** Scene2D HUD displaying the health bar, coin counter, 3 quick-access slots, and minimap frame. */
public final class Hud implements Disposable {

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

  private final ItemTextures textures;

  private final Image minimapBox;

  /**
   * Builds the Scene2D HUD table using the provided skin and state model.
   *
   * @param skin the loaded UI skin; must not be {@code null}
   * @param state the HUD state model; must not be {@code null}
   * @param batch the shared sprite batch; must not be {@code null}
   * @param textures the item pictures; the game screen owns and disposes them
   */
  public Hud(Skin skin, HudState state, Batch batch, ItemTextures textures) {
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
      slotStacks[i] = new Stack(slotBtn, iconCell);
      quickAccessTable.add(slotStacks[i]).size(40f, 40f).pad(4f);
    }

    this.minimapBox = new Image(skin.get("minimap_bg", TextureRegionDrawable.class));

    Table root = new Table();
    root.setFillParent(true);

    // Top row: Health bar (top-left) and Coin counter (top-right)
    root.add(healthBar).width(180f).height(24f).top().left().pad(12f);
    root.add().expandX();
    root.add(coinCounter).top().right().pad(12f);
    root.row();

    // Middle spacer
    root.add().expand().colspan(3);
    root.row();

    // Bottom row: Empty bottom-left, 3 quick slots (bottom-center), Minimap (bottom-right)
    root.add().bottom().left().width(96f);
    root.add(quickAccessTable).bottom().padBottom(12f);
    root.add(minimapBox).size(96f, 96f).bottom().right().pad(12f);

    stage.addActor(root);
  }

  /** Returns the Scene2D stage so it can be added to an {@code InputMultiplexer}. */
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
    coinCounter.setText(String.valueOf(state.getCoins()));
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
