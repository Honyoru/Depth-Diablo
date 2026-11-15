package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.epitech.depthdiablo.GameConstants;
import java.util.Objects;

/** Renders the Startup Menu (Play/Quit), Mode Selection (Arena/Endless), and 3-Hero Selection. */
public final class MenuScreen extends ScreenAdapter {

  private final GameApplication app;
  private final Skin skin;
  private final MenuFlowState flowState;
  private final Stage stage;
  private final Table rootTable;

  /**
   * Creates the menu screen.
   *
   * @param app the parent game application; must not be {@code null}
   * @param menuFlowState the menu navigation state machine; must not be {@code null}
   * @param hudSkin the shared UI skin; must not be {@code null}
   */
  public MenuScreen(GameApplication app, MenuFlowState menuFlowState, Skin hudSkin) {
    this.app = Objects.requireNonNull(app, "The game application must not be null");
    this.skin = Objects.requireNonNull(hudSkin, "The UI skin must not be null");
    this.flowState = Objects.requireNonNull(menuFlowState, "The menu flow state must not be null");
    this.stage =
        new Stage(new FitViewport(GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT));
    this.rootTable = new Table();
    this.rootTable.setFillParent(true);
    this.stage.addActor(rootTable);
    rebuildUi();
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);
    rebuildUi();
  }

  private void rebuildUi() {
    rootTable.clearChildren();
    switch (flowState.currentStep()) {
      case MAIN_MENU -> buildMainMenu();
      case MODE_SELECTION -> buildModeSelection();
      case CHARACTER_SELECTION -> buildCharacterSelection();
      case IN_GAME ->
          Gdx.app.postRunnable(
              () -> app.startGameScreen(flowState.selectedMode(), flowState.selectedHero()));
    }
  }

  private void buildMainMenu() {
    rootTable.add(new Label("DEPTH DIABLO", skin)).padBottom(30f).row();

    TextButton playBtn = new TextButton("Play", skin);
    playBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            flowState.openModeSelection();
            rebuildUi();
          }
        });
    rootTable.add(playBtn).width(220f).height(48f).pad(8f).row();

    TextButton quitBtn = new TextButton("Quit", skin);
    quitBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            Gdx.app.exit();
          }
        });
    rootTable.add(quitBtn).width(220f).height(48f).pad(8f).row();
  }

  private void buildModeSelection() {
    rootTable.add(new Label("SELECT MODE", skin)).padBottom(24f).row();

    for (MenuFlowState.GameMode mode : MenuFlowState.GameMode.values()) {
      TextButton modeBtn = new TextButton(mode.displayName(), skin);
      modeBtn.addListener(
          new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
              flowState.selectMode(mode);
              rebuildUi();
            }
          });
      rootTable.add(modeBtn).width(240f).height(48f).pad(8f).row();
    }

    TextButton backBtn = new TextButton("Back", skin);
    backBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            flowState.backToMainMenu();
            rebuildUi();
          }
        });
    rootTable.add(backBtn).width(160f).height(40f).padTop(16f).row();
  }

  private void buildCharacterSelection() {
    rootTable
        .add(new Label("SELECT HERO (" + flowState.selectedMode().displayName() + ")", skin))
        .colspan(3)
        .padBottom(24f)
        .row();

    for (MenuFlowState.HeroType hero : MenuFlowState.HeroType.values()) {
      TextButton heroBtn = new TextButton(hero.displayName(), skin);
      heroBtn.setDisabled(!hero.isPlayable());
      if (hero.isPlayable()) {
        heroBtn.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                if (flowState.trySelectHero(hero) && flowState.startGame()) {
                  rebuildUi();
                }
              }
            });
      }
      rootTable.add(heroBtn).width(220f).height(64f).pad(10f);
    }
    rootTable.row();

    TextButton backBtn = new TextButton("Back", skin);
    backBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            flowState.openModeSelection();
            rebuildUi();
          }
        });
    rootTable.add(backBtn).colspan(3).width(160f).height(40f).padTop(24f);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.06f, 0.05f, 0.08f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
