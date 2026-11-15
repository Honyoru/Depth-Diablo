package com.epitech.depthdiablo.desktop.ui;

import java.util.Objects;

/** Pure-Java state machine controlling startup menu, mode selection, and character selection. */
public final class MenuFlowState {

  /** Steps in the menu navigation flow. */
  public enum ScreenStep {
    MAIN_MENU,
    MODE_SELECTION,
    CHARACTER_SELECTION,
    IN_GAME,
    GAME_OVER
  }

  /** Available game modes. */
  public enum GameMode {
    ARENA("Arena Mode"),
    ENDLESS("Endless Mode");

    private final String displayName;

    GameMode(String displayName) {
      this.displayName = displayName;
    }

    /** Returns the human-readable title of the mode. */
    public String displayName() {
      return displayName;
    }
  }

  /** Character archetypes displayed on the selection screen (1 playable in PoC). */
  public enum HeroType {
    WARRIOR("Knight", true),
    MAGE("Mage (Locked)", false),
    ROGUE("Archer (Locked)", false);

    private final String displayName;
    private final boolean playable;

    HeroType(String displayName, boolean playable) {
      this.displayName = displayName;
      this.playable = playable;
    }

    /** Returns the display label of the hero. */
    public String displayName() {
      return displayName;
    }

    /** Returns whether this hero can be selected in the current PoC. */
    public boolean isPlayable() {
      return playable;
    }
  }

  private ScreenStep currentStep = ScreenStep.MAIN_MENU;
  private GameMode selectedMode = GameMode.ARENA;
  private HeroType selectedHero = HeroType.WARRIOR;
  private int lastScore;

  /** Advances from the main menu to the mode selection screen. */
  public void openModeSelection() {
    currentStep = ScreenStep.MODE_SELECTION;
  }

  /** Returns to the main menu. */
  public void backToMainMenu() {
    currentStep = ScreenStep.MAIN_MENU;
  }

  /**
   * Selects a game mode and advances to the character selection step.
   *
   * @param mode the chosen mode; must not be {@code null}
   */
  public void selectMode(GameMode mode) {
    this.selectedMode = Objects.requireNonNull(mode, "The game mode to select must not be null");
    this.currentStep = ScreenStep.CHARACTER_SELECTION;
  }

  /**
   * Attempts to select a hero. Only playable heroes succeed.
   *
   * @param hero the hero to select; must not be {@code null}
   * @return {@code true} if the hero is playable and was selected
   */
  public boolean trySelectHero(HeroType hero) {
    Objects.requireNonNull(hero, "The hero to select must not be null");
    if (!hero.isPlayable()) {
      return false;
    }
    this.selectedHero = hero;
    return true;
  }

  /**
   * Starts the game if the currently selected hero is playable.
   *
   * @return {@code true} if the transition to {@link ScreenStep#IN_GAME} succeeded
   */
  public boolean startGame() {
    if (!selectedHero.isPlayable()) {
      return false;
    }
    currentStep = ScreenStep.IN_GAME;
    return true;
  }

  /**
   * Ends the run: HP reached 0 (GDD 1.1 step 5). Opens the game over step with the run's score.
   *
   * @param score waves defeated in Arena mode, depth reached in a dungeon run; 0 or more
   * @throws IllegalArgumentException if {@code score} is negative
   */
  public void showGameOver(int score) {
    if (score < 0) {
      throw new IllegalArgumentException("The score of a run must not be negative, was " + score);
    }
    this.lastScore = score;
    this.currentStep = ScreenStep.GAME_OVER;
  }

  /** Starts a new run from the game over step: back to the character select, same mode (Q10). */
  public void rerun() {
    currentStep = ScreenStep.CHARACTER_SELECTION;
  }

  /**
   * Returns the score of the last finished run.
   *
   * @return the score given to {@link #showGameOver(int)}, 0 before any game over
   */
  public int lastScore() {
    return lastScore;
  }

  /** Returns the current menu screen step. */
  public ScreenStep currentStep() {
    return currentStep;
  }

  /** Returns the currently selected game mode. */
  public GameMode selectedMode() {
    return selectedMode;
  }

  /** Returns the currently selected hero. */
  public HeroType selectedHero() {
    return selectedHero;
  }
}
