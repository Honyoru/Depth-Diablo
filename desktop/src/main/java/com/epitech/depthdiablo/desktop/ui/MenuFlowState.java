package com.epitech.depthdiablo.desktop.ui;

import java.util.Objects;

/** Pure-Java state machine controlling startup menu, mode selection, and character selection. */
public final class MenuFlowState {

  /** Steps in the menu navigation flow. */
  public enum ScreenStep {
    MAIN_MENU,
    MODE_SELECTION,
    CHARACTER_SELECTION,
    IN_GAME
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
