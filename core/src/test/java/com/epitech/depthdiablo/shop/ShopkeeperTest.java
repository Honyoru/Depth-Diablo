package com.epitech.depthdiablo.shop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.arena.ArenaCycle;
import com.epitech.depthdiablo.engine.ShopOpened;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.BaseStats;
import com.epitech.depthdiablo.entity.Knight;
import com.epitech.depthdiablo.entity.Player;
import com.epitech.depthdiablo.entity.Stat;
import com.epitech.depthdiablo.input.PlayerOrders;
import com.epitech.depthdiablo.testdata.Items;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The shopkeeper (GDD 1.2, 1.4): in Arena mode he trades after each boss wave, and the player
 * starts the next round through him.
 */
class ShopkeeperTest {
  private static final Aabb PLACE = new Aabb(100, 100, 96, 96);

  /** One wave per cycle: wave 1 is already a boss wave, so clearing it opens the shop. */
  private final ArenaCycle cycle = new ArenaCycle(1, 1);

  private final World world = new World();
  private final Shopkeeper shopkeeper = new Shopkeeper(PLACE, cycle, world);

  /** Every ShopOpened the world delivered. */
  private final List<ShopOpened> opened = new ArrayList<>();

  ShopkeeperTest() {
    world.getEventBus().subscribe(ShopOpened.class, event -> opened.add((ShopOpened) event));
  }

  @Test
  void should_be_away_while_the_arena_fights() {
    assertFalse(shopkeeper.isOpen());
  }

  @Test
  void should_trade_once_a_boss_wave_is_cleared() {
    cycle.waveCleared();

    assertTrue(shopkeeper.isOpen());
  }

  @Test
  void should_open_the_shop_when_a_player_talks_to_him_while_he_trades() {
    cycle.waveCleared();
    Player player = aKnight();

    shopkeeper.interact(player);
    world.getEventBus().flush();

    assertEquals(1, opened.size());
    assertSame(player, opened.get(0).source());
    assertSame(shopkeeper, opened.get(0).shopkeeper());
    assertEquals(Optional.of(shopkeeper), opened.get(0).receiver());
  }

  @Test
  void should_not_open_the_shop_while_he_is_away() {
    shopkeeper.interact(aKnight());
    world.getEventBus().flush();

    assertTrue(opened.isEmpty());
  }

  @Test
  void should_not_open_the_shop_to_a_dead_player() {
    cycle.waveCleared();
    Player player = aKnight();
    player.takeDamage(player.getHp());

    shopkeeper.interact(player);
    world.getEventBus().flush();

    assertTrue(opened.isEmpty());
  }

  @Test
  void should_start_the_next_round_and_go_away_when_the_player_asks_while_he_trades() {
    cycle.waveCleared();

    boolean started = shopkeeper.startNextRound();

    assertTrue(started);
    assertEquals(ArenaCycle.Phase.WAVE, cycle.getPhase());
    assertEquals(2, cycle.getWaveNumber());
    assertFalse(shopkeeper.isOpen());
  }

  @Test
  void should_not_start_a_round_while_he_is_away() {
    boolean started = shopkeeper.startNextRound();

    assertFalse(started);
    assertEquals(1, cycle.getWaveNumber());
  }

  @Test
  void should_let_the_player_walk_through_him() {
    assertFalse(shopkeeper.blocks());
  }

  @Test
  void should_reject_a_null_player() {
    assertThrows(NullPointerException.class, () -> shopkeeper.interact(null));
  }

  @Test
  void should_reject_a_null_cycle() {
    assertThrows(NullPointerException.class, () -> new Shopkeeper(PLACE, null, world));
  }

  private Knight aKnight() {
    Aabb box = new Aabb(0f, 0f, 16f, 16f);
    Map<Stat, Float> stats = new EnumMap<>(Stat.class);
    for (Stat stat : Stat.values()) {
      stats.put(stat, 1f);
    }
    return new Knight(
        box,
        new BaseStats(stats),
        Items.sword(),
        Items.leatherArmor(),
        box,
        0,
        new PlayerOrders(),
        world);
  }
}
