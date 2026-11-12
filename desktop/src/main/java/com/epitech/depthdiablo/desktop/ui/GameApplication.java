package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/** Root LibGDX {@link Game} managing shared rendering resources and screen transitions. */
public final class GameApplication extends Game {

  private SpriteBatch batch;
  private Skin hudSkin;

  private MenuFlowState menuFlowState;

  @Override
  public void create() {
    this.batch = new SpriteBatch();
    this.hudSkin = UiSkinFactory.loadHudSkin();
    this.menuFlowState = new MenuFlowState();
    setScreen(new MenuScreen(this, menuFlowState, hudSkin));
  }

  /** Leaves the gameplay screen and shows the main menu again. */
  public void showMenu() {
    if (getScreen() != null) {
      getScreen().dispose();
    }
    menuFlowState.backToMainMenu();
    setScreen(new MenuScreen(this, menuFlowState, hudSkin));
  }

  /**
   * Transitions from the character selection screen into the active gameplay screen.
   *
   * @param mode the chosen game mode
   * @param hero the chosen playable hero
   */
  public void startGameScreen(MenuFlowState.GameMode mode, MenuFlowState.HeroType hero) {
    if (getScreen() != null) {
      getScreen().dispose();
    }
    setScreen(new GameScreen(this, hudSkin, mode, hero));
  }

  /** Returns the shared {@link SpriteBatch}. */
  public SpriteBatch batch() {
    return batch;
  }

  @Override
  public void dispose() {
    if (getScreen() != null) {
      getScreen().dispose();
    }
    if (hudSkin != null) {
      hudSkin.dispose();
    }
    if (batch != null) {
      batch.dispose();
    }
  }
}
