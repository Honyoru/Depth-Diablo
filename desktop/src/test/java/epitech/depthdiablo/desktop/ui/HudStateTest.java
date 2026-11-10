package com.epitech.depthdiablo.desktop.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link HudState}. */
class HudStateTest {

  private static final class DummyPlayer extends Character {
    DummyPlayer(int maxHp) {
      super(new Aabb(0f, 0f, 16f, 16f), maxHp, 10, 1, new Aabb(0f, 0f, 16f, 16f), 5);
    }

    @Override
    protected void think(long tick) {
      // No-op for HUD test
    }
  }

  @Test
  void syncFromCharacterUpdatesHpAndPercentage() {
    DummyPlayer player = new DummyPlayer(200);
    player.takeDamage(50);
    HudState hudState = new HudState();

    hudState.syncFromCharacter(player);

    assertEquals(150, hudState.hp());
    assertEquals(200, hudState.maxHp());
    assertEquals(75f, hudState.healthPercentage(), 0.01f);
  }

  @Test
  void coinsAndQuickSlotsUpdateAndValidateBounds() {
    HudState hudState = new HudState();

    hudState.addCoins(42);
    assertEquals(42, hudState.coins());

    hudState.selectSlot(2);
    assertEquals(2, hudState.activeSlot());

    assertThrows(IllegalArgumentException.class, () -> hudState.addCoins(-1));
    assertThrows(IllegalArgumentException.class, () -> hudState.selectSlot(-1));
    assertThrows(IllegalArgumentException.class, () -> hudState.selectSlot(HudState.SLOT_COUNT));
  }
}
