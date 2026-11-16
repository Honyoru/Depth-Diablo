package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aRat;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aZombie;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.testdata.EnemyBuilder;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The contact attack of rats and zombies (GDD 1.3.1, 1.6, DECISIONS §5). */
class MeleeEnemyTest {
  private final World world = new World();
  private final List<GameEvent> events = new ArrayList<>();

  MeleeEnemyTest() {
    world.eventBus().subscribeAll(events::add);
  }

  @Test
  void should_hit_its_target_with_the_damage_formula_when_in_reach() {
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18, 0);
    rat.alert(player);

    rat.attackTarget(world);

    assertEquals(8, player.hp());
  }

  @Test
  void should_publish_the_damage_it_deals(){
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18,0);
    rat.alert(player);

    rat.attackTarget(world);
    world.eventBus().flush();

    assertEquals(List.of(new DamageDealt(rat, player, 2)), events);
  }

  @Test
  void should_hit_with_its_own_attak_stat(){
    MeleeEnemy zombie = aZombie().build();
    Character player = aPlayerAt(18,0);
    zombie.alert(player);

    zombie.attackTarget(world);

    assertEquals(7,player.hp());
  }

  @Test
  void should_not_hit_a_target_its_reach(){
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(21,0);
    rat.alert(player);

    rat.attackTarget(world);

    assertEquals(10, player.hp());
  }

  @Test
  void should_not_hit_when_it_has_no_target(){
    MeleeEnemy rat = aRat().build();

    rat.attackTarget(world);
    world.eventBus().flush();

    assertTrue(events.isEmpty());
  }

  @Test
  void should_not_hit_an_invincible_target(){
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18, 0);
    player.grantInvincibility(10);
    rat.alert(player);

    rat.attackTarget(world);
    world.eventBus().flush();

    assertTrue(events.isEmpty());
  }

  @Test
  void should_not_hit_a_target_that_died_since_the_alert(){
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18, 0);
    rat.alert(player);
    player.takeDamage(player.hp());

    rat.attackTarget(world);
    world.eventBus().flush();

    assertTrue(events.isEmpty());
  }

  @Test
  void should_give_its_reach() {
    MeleeEnemy zombie = aZombie().withReach(8).build();

    assertEquals(18, zombie.reach());
  }

  @Test
  void should_reject_a_negative_reach() {
    EnemyBuilder rat = aRat().withReach(-1);

    assertThrows(IllegalArgumentException.class, rat::build);
  }

  private static Character aPlayerAt(float x, float y) {
    Aabb box = new Aabb(x,y,16,16);
    return aCharacter().withFaction(Faction.PLAYER).withBodyBox(box).withHurtbox(box).build();
  }
}
