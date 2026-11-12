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

    assertEquals(MenuFlowState.ScreenStep.MAIN_MENU, state.currentStep());
    assertEquals(MenuFlowState.GameMode.ARENA, state.selectedMode());
    assertEquals(MenuFlowState.HeroType.WARRIOR, state.selectedHero());
  }

  @Test
  void navigationMovesFromMainToModeToCharacterToInGame() {
    MenuFlowState state = new MenuFlowState();

    state.openModeSelection();
    assertEquals(MenuFlowState.ScreenStep.MODE_SELECTION, state.currentStep());

    state.selectMode(MenuFlowState.GameMode.ENDLESS);
    assertEquals(MenuFlowState.GameMode.ENDLESS, state.selectedMode());
    assertEquals(MenuFlowState.ScreenStep.CHARACTER_SELECTION, state.currentStep());

    assertTrue(state.trySelectHero(MenuFlowState.HeroType.WARRIOR));
    assertTrue(state.startGame());
    assertEquals(MenuFlowState.ScreenStep.IN_GAME, state.currentStep());
  }

  @Test
  void lockedHeroesCannotBeSelectedInPoc() {
    MenuFlowState state = new MenuFlowState();
    state.openModeSelection();
    state.selectMode(MenuFlowState.GameMode.ARENA);

    assertFalse(MenuFlowState.HeroType.MAGE.isPlayable());
    assertFalse(MenuFlowState.HeroType.ROGUE.isPlayable());
    assertFalse(state.trySelectHero(MenuFlowState.HeroType.MAGE));
    assertEquals(MenuFlowState.HeroType.WARRIOR, state.selectedHero());
  }

  @Test
  void nullArgumentsThrowNullPointerException() {
    MenuFlowState state = new MenuFlowState();
    assertThrows(NullPointerException.class, () -> state.selectMode(null));
    assertThrows(NullPointerException.class, () -> state.trySelectHero(null));
  }
}
