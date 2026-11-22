package com.epitech.depthdiablo.desktop.ui;

import java.util.Objects;

/** Pure-Java state machine controlling startup menu, mode selection, and character selection. */
public final class MenuFlowState {

  /** Steps in the menu navigation flow. */
  public enum ScreenStep {
    /** The title screen. */
    MAIN_MENU,
    /** Choosing the Arena or Endless. */
    MODE_SELECTION,
    /** Choosing the hero. */
    CHARACTER_SELECTION,
    /** A run is being played. */
    IN_GAME,
    /** The run ended; its score shows. */
    GAME_OVER
  }

  /** Available game modes, each with the map it is played on. */
  public enum GameMode {
    /** Waves of enemies in one arena, a boss every fifth wave. */
    ARENA("Arena Mode", "arena_mapV1.tmx"),
    /** Floors explored one after the other. */
    ENDLESS("Endless Mode", "Floor1.tmx");

    private final String displayName;
    private final String mapPath;

    GameMode(String displayName, String mapPath) {
      this.displayName = displayName;
      this.mapPath = mapPath;
    }

    /**
     * Returns the human-readable title of the mode.
     *
     * @return e.g. {@code "Arena Mode"}
     */
    public String getDisplayName() {
      return displayName;
    }

    /**
     * Returns the Tiled map this mode is played on.
     *
     * @return its path in the assets, e.g. {@code "arena_mapV1.tmx"}
     */
    public String getMapPath() {
      return mapPath;
    }
  }

  /** Character archetypes displayed on the selection screen (1 playable in PoC). */
  public enum HeroType {
    /** The Knight, a melee fighter. */
    WARRIOR("Knight", true, "knight"),
    /** The Mage, locked for now. */
    MAGE("Mage (Locked)", false, "mage"),
    /** The Assassin. */
    ROGUE("Assassin", true, "assassin");

    private final String displayName;
    private final boolean playable;
    private final String characterId;

    HeroType(String displayName, boolean playable, String characterId) {
      this.displayName = displayName;
      this.playable = playable;
      this.characterId = characterId;
    }

    /**
     * Returns the display label of the hero.
     *
     * @return e.g. {@code "Knight"}
     */
    public String getDisplayName() {
      return displayName;
    }

    /**
     * Tells whether this hero can be selected in the current PoC.
     *
     * @return true if it can be played
     */
    public boolean isPlayable() {
      return playable;
    }

    /**
     * Returns the id of this hero in {@code data/characters.json}.
     *
     * @return its character id, e.g. {@code "knight"}
     */
    public String getCharacterId() {
      return characterId;
    }
  }

  private ScreenStep currentStep = ScreenStep.MAIN_MENU;

  private GameMode selectedMode = GameMode.ARENA;

  private HeroType selectedHero = HeroType.WARRIOR;

  private int lastScore;

  /** Creates the menu flow on the main menu, with the Arena and the Knight selected. */
  public MenuFlowState() {
    // its fields start with their declared values
  }

  /**
   * Returns the score of the last finished run.
   *
   * @return the score given to {@link #showGameOver(int)}, 0 before any game over
   */
  public int getLastScore() {
    return lastScore;
  }

  /**
   * Returns the current menu screen step.
   *
   * @return the step the menus show
   */
  public ScreenStep getCurrentStep() {
    return currentStep;
  }

  /**
   * Returns the currently selected game mode.
   *
   * @return the mode chosen, the Arena by default
   */
  public GameMode getSelectedMode() {
    return selectedMode;
  }

  /**
   * Returns the currently selected hero.
   *
   * @return the hero chosen, the Knight by default
   */
  public HeroType getSelectedHero() {
    return selectedHero;
  }

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
}
