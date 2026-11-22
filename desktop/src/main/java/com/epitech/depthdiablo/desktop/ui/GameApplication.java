package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.epitech.depthdiablo.desktop.audio.GameAudio;

/** Root LibGDX {@link Game} managing shared rendering resources and screen transitions. */
public final class GameApplication extends Game {

  private SpriteBatch batch;

  private Skin hudSkin;

  private MenuFlowState menuFlowState;

  private GameAudio audio;

  /** Creates the application; libGDX calls {@link #create()} once the window is up. */
  public GameApplication() {
    // its fields start with their declared values
  }

  /**
   * Returns the shared {@link SpriteBatch}.
   *
   * @return the batch every screen draws with
   */
  public SpriteBatch getBatch() {
    return batch;
  }

  /**
   * Returns the game's sounds and music, shared by every screen so the loop never restarts.
   *
   * @return the audio, made once in {@link #create()}
   */
  public GameAudio getAudio() {
    return audio;
  }

  @Override
  public void create() {
    this.batch = new SpriteBatch();
    this.audio = new GameAudio();
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
   * Leaves the gameplay screen because the run ended and shows the game over menu.
   *
   * @param score the run's score, 0 or more
   */
  public void showGameOver(int score) {
    if (getScreen() != null) {
      getScreen().dispose();
    }
    menuFlowState.showGameOver(score);
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

  /** Renders the current screen, then moves the music fades on, whatever the screen. */
  @Override
  public void render() {
    super.render();
    audio.updateMusic(Gdx.graphics.getDeltaTime());
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
    if (audio != null) {
      audio.dispose();
    }
  }
}
