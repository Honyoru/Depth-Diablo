package com.epitech.depthdiablo.desktop.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link MenuFlowState}. */
class MenuFlowStateTest {

  @Test
  void defaultStateStartsAtMainMenuWithPlayableWarrior() {
    MenuFlowState state = new MenuFlowState();

    assertEquals(MenuFlowState.ScreenStep.MAIN_MENU, state.getCurrentStep());
    assertEquals(MenuFlowState.GameMode.ARENA, state.getSelectedMode());
    assertEquals(MenuFlowState.HeroType.WARRIOR, state.getSelectedHero());
  }

  @Test
  void navigationMovesFromMainToModeToCharacterToInGame() {
    MenuFlowState state = new MenuFlowState();

    state.openModeSelection();
    assertEquals(MenuFlowState.ScreenStep.MODE_SELECTION, state.getCurrentStep());

    state.selectMode(MenuFlowState.GameMode.ENDLESS);
    assertEquals(MenuFlowState.GameMode.ENDLESS, state.getSelectedMode());
    assertEquals(MenuFlowState.ScreenStep.CHARACTER_SELECTION, state.getCurrentStep());

    assertTrue(state.trySelectHero(MenuFlowState.HeroType.WARRIOR));
    assertTrue(state.startGame());
    assertEquals(MenuFlowState.ScreenStep.IN_GAME, state.getCurrentStep());
  }

  @Test
  void lockedHeroesCannotBeSelectedInPoc() {
    MenuFlowState state = new MenuFlowState();
    state.openModeSelection();
    state.selectMode(MenuFlowState.GameMode.ARENA);

    assertFalse(MenuFlowState.HeroType.MAGE.isPlayable());
    assertFalse(MenuFlowState.HeroType.ROGUE.isPlayable());
    assertFalse(state.trySelectHero(MenuFlowState.HeroType.MAGE));
    assertEquals(MenuFlowState.HeroType.WARRIOR, state.getSelectedHero());
  }

  @Test
  void nullArgumentsThrowNullPointerException() {
    MenuFlowState state = new MenuFlowState();
    assertThrows(NullPointerException.class, () -> state.selectMode(null));
    assertThrows(NullPointerException.class, () -> state.trySelectHero(null));
  }

  // ---- Game over (GDD 1.1 step 5, Q10): HP at 0 ends the run, then rerun or quit.

  @Test
  void showGameOverStoresTheScoreAndOpensTheGameOverStep() {
    MenuFlowState state = aStateInGame(MenuFlowState.GameMode.ARENA);

    state.showGameOver(7);

    assertEquals(MenuFlowState.ScreenStep.GAME_OVER, state.getCurrentStep());
    assertEquals(7, state.getLastScore());
  }

  @Test
  void rerunGoesBackToCharacterSelectionWithTheSameMode() {
    MenuFlowState state = aStateInGame(MenuFlowState.GameMode.ENDLESS);
    state.showGameOver(3);

    state.rerun();

    assertEquals(MenuFlowState.ScreenStep.CHARACTER_SELECTION, state.getCurrentStep());
    assertEquals(MenuFlowState.GameMode.ENDLESS, state.getSelectedMode());
  }

  @Test
  void showGameOverRejectsANegativeScore() {
    MenuFlowState state = aStateInGame(MenuFlowState.GameMode.ARENA);

    assertThrows(IllegalArgumentException.class, () -> state.showGameOver(-1));
  }

  @Test
  void quittingTheGameOverScreenGoesBackToTheMainMenu() {
    MenuFlowState state = aStateInGame(MenuFlowState.GameMode.ARENA);
    state.showGameOver(0);

    state.backToMainMenu();

    assertEquals(MenuFlowState.ScreenStep.MAIN_MENU, state.getCurrentStep());
  }

  @Test
  void lastScoreIsZeroBeforeAnyGameOver() {
    MenuFlowState state = new MenuFlowState();

    int score = state.getLastScore();

    assertEquals(0, score);
  }

  @Test
  void should_give_the_knight_id_for_the_warrior() {
    assertEquals("knight", MenuFlowState.HeroType.WARRIOR.getCharacterId());
  }

  private static MenuFlowState aStateInGame(MenuFlowState.GameMode mode) {
    MenuFlowState state = new MenuFlowState();
    state.openModeSelection();
    state.selectMode(mode);
    state.startGame();
    return state;
  }

  @Test
  void eachModeIsPlayedOnItsOwnMap() {
    assertEquals("arena_mapV1.tmx", MenuFlowState.GameMode.ARENA.getMapPath());
    assertEquals("Floor1.tmx", MenuFlowState.GameMode.ENDLESS.getMapPath());
  }
}
