package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.epitech.depthdiablo.GameConstants;
import java.util.Objects;

/**
 * The shop page (GDD 1.4), opened by talking to the shopkeeper; the game is paused while it is
 * open. For now it greets the player and starts the next round; the vending slots, selling and the
 * roulette come with the coins.
 */
final class ShopPage implements Disposable {
  private static final float TEXT_SCALE = 0.5f;
  private static final float TITLE_SCALE = 0.65f;

  private final Stage stage;

  /**
   * Builds the page once.
   *
   * @param skin the HUD skin, for the font, the buttons and the page background
   * @param batch the shared sprite batch
   * @param onNextRound asked to start the next round
   * @param onLeave asked to close the page and go back to the arena
   */
  ShopPage(Skin skin, Batch batch, Runnable onNextRound, Runnable onLeave) {
    Objects.requireNonNull(skin, "skin is null");
    Objects.requireNonNull(onNextRound, "onNextRound is null");
    Objects.requireNonNull(onLeave, "onLeave is null");
    this.stage =
        new Stage(
            new FitViewport(GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT),
            Objects.requireNonNull(batch, "batch is null"));

    Table page = new Table(skin);
    page.setBackground("inventory_bg");
    page.setSize(0.5f * GameConstants.VIRTUAL_WIDTH, 0.4f * GameConstants.VIRTUAL_HEIGHT);
    page.setPosition(
        (GameConstants.VIRTUAL_WIDTH - page.getWidth()) / 2f,
        (GameConstants.VIRTUAL_HEIGHT - page.getHeight()) / 2f);
    page.pad(16f).top();
    page.add(label("Shop", skin, TITLE_SCALE)).colspan(2).padBottom(8f).row();
    page.add(label("Still alive? Let's trade.", skin, TEXT_SCALE)).colspan(2).expandY().top();
    page.row();
    page.add(button("Next round", skin, onNextRound)).padRight(12f);
    page.add(button("Leave", skin, onLeave));
    stage.addActor(page);
  }

  /** A button in the HUD style with its text drawn at the page size. */
  private static TextButton button(String text, Skin skin, Runnable action) {
    TextButton button = new TextButton(text, skin);
    button.getLabel().setFontScale(TEXT_SCALE);
    button.pad(4f, 10f, 4f, 10f);
    button.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            action.run();
          }
        });
    return button;
  }

  /** A label in the HUD font, drawn at the given scale. */
  private static Label label(String text, Skin skin, float scale) {
    Label label = new Label(text, skin);
    label.setFontScale(scale);
    return label;
  }

  /**
   * Draws the page.
   *
   * @param delta frame time in seconds
   */
  void render(float delta) {
    stage.getViewport().apply(); // its 16:9 area, the one its clicks are read in
    stage.act(delta);
    stage.draw();
  }

  /**
   * Updates the page viewport when the window is resized.
   *
   * @param width new window width in pixels
   * @param height new window height in pixels
   */
  void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  /**
   * Returns the stage of the page, to listen to its clicks.
   *
   * @return its stage
   */
  Stage getStage() {
    return stage;
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
