package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Knight;
import com.epitech.depthdiablo.entity.enemy.MeleeEnemy;
import com.epitech.depthdiablo.entity.stats.BaseStats;
import com.epitech.depthdiablo.entity.stats.Stat;
import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.input.PlayerOrders;
import com.epitech.depthdiablo.testdata.EnemyBuilder;
import com.epitech.depthdiablo.testdata.Items;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * The teleport zone to the next floor (GDD 1.1 step 3, Q11): it opens once every enemy is dead,
 * then sends the player on after 3 seconds standing in it.
 */
class TeleportZoneTest {
  /** Three tiles by three, from (100, 100) to (196, 196). */
  private static final Aabb ZONE = new Aabb(100, 100, 96, 96);

  /** 16 x 16 body at (140, 140): its centre (148, 148) is inside the zone. */
  private static final float INSIDE = 140f;

  private static final float SPEED = 120f;

  private final World world = new World();
  private final PlayerOrders orders = new PlayerOrders();
  private final TeleportZone zone = new TeleportZone(ZONE, world);

  @Test
  void should_stay_closed_while_an_enemy_is_alive() {
    world.add(aRat());
    world.add(zone);

    world.tick();

    assertFalse(zone.isOpen());
  }

  @Test
  void should_open_once_every_enemy_is_dead() {
    MeleeEnemy rat = aRat();
    world.add(rat);
    world.add(zone);
    world.tick();

    rat.takeDamage(rat.getHp());
    world.tick();

    assertTrue(zone.isOpen());
  }

  @Test
  void should_open_on_a_floor_without_enemies() {
    world.add(zone);

    world.tick();

    assertTrue(zone.isOpen());
  }

  @Test
  void should_be_ready_after_three_seconds_in_the_open_zone() {
    world.add(zone);
    world.add(aKnightAt(INSIDE, INSIDE));
    world.tick(); // opens; the player is seen inside at the end of this tick

    tick(TeleportZone.CHARGE_TICKS - 1);
    assertTrue(zone.isCharging());
    assertFalse(zone.isReady());

    world.tick();
    assertTrue(zone.isReady());
  }

  @Test
  void should_wait_three_seconds() {
    assertEquals(3 * GameConstants.TICKS_PER_SECOND, TeleportZone.CHARGE_TICKS);
  }

  @Test
  void should_reset_the_charge_when_the_player_steps_out() {
    world.add(zone);
    world.add(aKnightAt(INSIDE, INSIDE));
    tick(GameConstants.TICKS_PER_SECOND);
    assertTrue(zone.isCharging());

    orders.walk(1f, 0f);
    tick(GameConstants.TICKS_PER_SECOND); // 120 px to the right: out of the zone

    assertFalse(zone.isCharging());
    assertFalse(zone.isReady());
  }

  @Test
  void should_not_charge_while_closed() {
    world.add(aRat());
    world.add(zone);
    world.add(aKnightAt(INSIDE, INSIDE));

    tick(TeleportZone.CHARGE_TICKS + 1);

    assertFalse(zone.isCharging());
    assertFalse(zone.isReady());
  }

  @Test
  void should_not_charge_for_a_dead_player() {
    Knight knight = aKnightAt(INSIDE, INSIDE);
    knight.takeDamage(knight.getHp());
    world.add(zone);
    world.add(knight);

    tick(TeleportZone.CHARGE_TICKS + 1);

    assertFalse(zone.isCharging());
  }

  @Test
  void should_not_charge_when_only_the_edge_of_the_body_touches() {
    world.add(zone);
    world.add(aKnightAt(90f, 90f)); // overlaps the corner, centre (98, 98) outside

    tick(2);

    assertFalse(zone.isCharging());
  }

  @Test
  void should_let_everything_walk_through_it() {
    assertFalse(zone.blocks());
  }

  @Test
  void should_put_back_its_charge_after_a_rewind() {
    world.add(zone);
    world.add(aKnightAt(INSIDE, INSIDE));
    tick(10);
    StateBuffer photo = new StateBuffer();
    zone.writeState(photo);

    tick(TeleportZone.CHARGE_TICKS);
    assertTrue(zone.isReady());
    photo.startReading();
    zone.readState(photo);

    assertTrue(zone.isOpen());
    assertTrue(zone.isCharging());
    assertFalse(zone.isReady());
  }

  @Test
  void should_reject_a_null_world() {
    assertThrows(NullPointerException.class, () -> new TeleportZone(ZONE, null));
  }

  private void tick(int ticks) {
    for (int i = 0; i < ticks; i++) {
      world.tick();
    }
  }

  private MeleeEnemy aRat() {
    return EnemyBuilder.aRat().at(500, 500).withWorld(world).build();
  }

  private Knight aKnightAt(float x, float y) {
    Aabb box = new Aabb(x, y, 16f, 16f);
    Map<Stat, Float> values = new EnumMap<>(Stat.class);
    values.put(Stat.MAX_HP, 10f);
    values.put(Stat.ATTACK, 1f);
    values.put(Stat.DEFENSE, 1f);
    values.put(Stat.SPEED, SPEED);
    values.put(Stat.ATTACK_SPEED, 1f);
    values.put(Stat.LUCK, 0f);
    return new Knight(
        box, new BaseStats(values), Items.sword(), Items.leatherArmor(), box, 0, orders, world);
  }
}
