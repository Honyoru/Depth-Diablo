package com.epitech.depthdiablo.desktop.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.arena.ArenaCycle;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.shop.Shopkeeper;
import org.junit.jupiter.api.Test;

/** GDD 1.2, 1.4: the shop page opens on the shopkeeper and starts the next round. */
class ShopPageStateTest {
  /** One wave per cycle: clearing wave 1, a boss wave, opens the shop. */
  private final ArenaCycle cycle = new ArenaCycle(1, 1);

  private final Shopkeeper shopkeeper = new Shopkeeper(new Aabb(0, 0, 96, 96), cycle, new World());

  private final ShopPageState page = new ShopPageState();

  ShopPageStateTest() {
    cycle.waveCleared();
  }

  @Test
  void should_be_closed_when_created() {
    assertFalse(page.isOpen());
  }

  @Test
  void should_open_on_the_shopkeeper_the_player_talks_to() {
    page.open(shopkeeper);

    assertTrue(page.isOpen());
  }

  @Test
  void should_close_without_starting_the_next_round() {
    page.open(shopkeeper);

    page.close();

    assertFalse(page.isOpen());
    assertEquals(ArenaCycle.Phase.SHOP, cycle.getPhase());
  }

  @Test
  void should_start_the_next_round_and_close() {
    page.open(shopkeeper);

    boolean started = page.startNextRound();

    assertTrue(started);
    assertFalse(page.isOpen());
    assertEquals(ArenaCycle.Phase.WAVE, cycle.getPhase());
    assertEquals(2, cycle.getWaveNumber());
  }

  @Test
  void should_start_nothing_while_it_is_closed() {
    boolean started = page.startNextRound();

    assertFalse(started);
    assertEquals(ArenaCycle.Phase.SHOP, cycle.getPhase());
  }

  @Test
  void should_reject_a_null_shopkeeper() {
    assertThrows(NullPointerException.class, () -> page.open(null));
  }
}
