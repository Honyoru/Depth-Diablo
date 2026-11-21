package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aRat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.engine.World;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The melee enemy AI on the gdx-ai state machine: idle, chase, attack, dead (GDD 1.3, 1.7). */
class EnemyStateTest {
  private final World world = new World();
  private final List<GameEvent> events = new ArrayList<>();

  EnemyStateTest() {
    world.getEventBus().subscribeAll(events::add);
  }

  @Test
  void should_stay_idle_and_still_when_it_has_no_target() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    world.add(rat);

    ticks(10);

    assertEquals(EnemyState.IDLE, rat.getState());
    assertEquals(0f, rat.position().x);
  }

  @Test
  void should_walk_towards_its_target_when_alerted() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayerAt(100, 0);
    world.add(rat);
    world.add(player);
    rat.alert(player);

    ticks(2); // tick 1: IDLE sees the target and switches to CHASE; tick 2: one step

    assertEquals(EnemyState.CHASE, rat.getState());
    assertEquals(1f, rat.position().x, 1e-4f); // speed 60 px/s = 1 px per tick
  }

  @Test
  void should_stop_at_a_wall_when_it_stands_in_the_way() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayerAt(100, 0);
    world.add(rat);
    world.add(player);
    world.add(new Wall(new Aabb(30, -20, 10, 60)));
    rat.alert(player);

    ticks(120);

    assertEquals(14f, rat.position().x, 1e-4f); // its right side stops at the wall's left side
  }

  @Test
  void should_stop_at_another_enemy_when_it_stands_in_the_way() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    MeleeEnemy otherRat = aRat().at(30, 0).withWorld(world).build();
    Character player = aPlayerAt(100, 0);
    world.add(rat);
    world.add(otherRat);
    world.add(player);
    rat.alert(player);

    ticks(120);

    assertEquals(14f, rat.position().x, 1e-4f); // its right side stops at the other rat's left side
  }

  @Test
  void should_still_walk_when_it_starts_on_top_of_another_enemy() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    MeleeEnemy otherRat = aRat().withWorld(world).build();
    Character player = aPlayerAt(100, 0);
    world.add(rat);
    world.add(otherRat);
    world.add(player);
    rat.alert(player);

    ticks(2); // tick 1: IDLE -> CHASE; tick 2: one step out of the other rat

    assertEquals(1f, rat.position().x, 1e-4f);
  }

  @Test
  void should_attack_its_target_when_in_reach() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayerAt(18, 0);
    world.add(rat);
    world.add(player);
    rat.alert(player);

    ticks(3); // IDLE -> CHASE, CHASE -> ATTACK, then the bite

    assertEquals(EnemyState.ATTACK, rat.getState());
    assertEquals(List.of(new DamageDealt(rat, player, 2)), events);
  }

  @Test
  void should_not_attack_again_when_less_than_one_second_has_passed() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayerAt(18, 0);
    world.add(rat);
    world.add(player);
    rat.alert(player);

    ticks(3 + 59); // the first bite, then 59 ticks of its 60-tick cooldown

    assertEquals(1, events.size());
  }

  @Test
  void should_attack_again_when_one_second_has_passed_at_attack_speed_1() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayerAt(18, 0);
    world.add(rat);
    world.add(player);
    rat.alert(player);

    ticks(3 + 60); // the first bite, then its whole 60-tick cooldown

    assertEquals(2, events.size());
  }

  @Test
  void should_go_idle_when_its_target_dies() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayerAt(18, 0);
    world.add(rat);
    world.add(player);
    rat.alert(player);
    player.takeDamage(9); // 1 HP left: the first bite kills

    ticks(4);

    assertTrue(player.isDead());
    assertEquals(EnemyState.IDLE, rat.getState());
  }

  @Test
  void should_leave_the_world_when_its_hp_reaches_0() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    world.add(rat);
    rat.takeDamage(3);

    ticks(1);

    assertEquals(EnemyState.DEAD, rat.getState());
    assertTrue(rat.isRemoved());
    assertFalse(world.getEntities().contains(rat));
  }

  @Test
  void should_stay_idle_when_watching_a_target_beyond_its_detection_range() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayerAt(GameConstants.ENEMY_DETECTION_RANGE + 50, 0);
    world.add(rat);
    world.add(player);
    rat.watch(player);

    ticks(10);

    assertEquals(EnemyState.IDLE, rat.getState());
    assertEquals(0f, rat.position().x);
  }

  @Test
  void should_walk_towards_a_watched_target_within_its_detection_range() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayerAt(100, 0);
    world.add(rat);
    world.add(player);
    rat.watch(player);

    ticks(2);

    assertEquals(EnemyState.CHASE, rat.getState());
    assertEquals(1f, rat.position().x, 1e-4f);
  }

  @Test
  void should_go_idle_when_a_watched_target_leaves_its_detection_range() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayerAt(100, 0);
    world.add(rat);
    world.add(player);
    rat.watch(player);
    ticks(2);

    player.moveTo(new Vector2(GameConstants.ENEMY_DETECTION_RANGE * 3, 0));
    ticks(2);

    assertEquals(EnemyState.IDLE, rat.getState());
  }

  @Test
  void should_chase_an_alerted_target_beyond_its_detection_range() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayerAt(GameConstants.ENEMY_DETECTION_RANGE * 3, 0);
    world.add(rat);
    world.add(player);
    rat.alert(player);

    ticks(2);

    assertEquals(EnemyState.CHASE, rat.getState());
  }

  private void ticks(int count) {
    for (int i = 0; i < count; i++) {
      world.tick();
    }
  }

  private static Character aPlayerAt(float x, float y) {
    Aabb box = new Aabb(x, y, 16, 16);
    return aCharacter().withFaction(Faction.PLAYER).withBodyBox(box).withHurtbox(box).build();
  }
}
