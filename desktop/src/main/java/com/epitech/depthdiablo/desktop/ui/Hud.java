package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

  /**
   * Builds the Scene2D HUD table using the provided skin and state model.
   *
   * @param skin the loaded UI skin; must not be {@code null}
   * @param state the HUD state model; must not be {@code null}
   * @param batch the shared sprite batch; must not be {@code null}
   */
  public Hud(Skin skin, HudState state, Batch batch) {
    Objects.requireNonNull(skin, "skin");
    this.state = Objects.requireNonNull(state, "state");
    this.stage =
        new Stage(
            new FitViewport(GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT),
            Objects.requireNonNull(batch, "batch"));

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
      quickAccessTable.add(slotBtn).size(40f, 40f).pad(4f);
    }

    Image minimapBox = new Image(skin.get("minimap_bg", TextureRegionDrawable.class));

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

  /**
   * Updates Scene2D widgets from {@link HudState} and renders the HUD stage.
   *
   * @param delta frame time in seconds
   */
  public void render(float delta) {
    healthBar.setValue(state.healthPercentage());
    coinCounter.setText(String.valueOf(state.coins()));
    for (int i = 0; i < HudState.SLOT_COUNT; i++) {
      float scale = (i == state.activeSlot()) ? 1.15f : 1.0f;
      slotButtons[i].setTransform(true);
      slotButtons[i].setScale(scale);
    }
    stage.act(delta);
    stage.draw();
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

  /** Returns the Scene2D stage so it can be added to an {@code InputMultiplexer}. */
  public Stage stage() {
    return stage;
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
