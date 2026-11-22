package com.epitech.depthdiablo.entity.enemy;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aRat;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aZombie;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.engine.event.AttackStarted;
import com.epitech.depthdiablo.engine.event.DamageDealt;
import com.epitech.depthdiablo.engine.event.EntityDied;
import com.epitech.depthdiablo.engine.event.GameEvent;
import com.epitech.depthdiablo.entity.character.Character;
import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.testdata.EnemyBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The contact attack of rats and zombies (GDD 1.3.1, 1.6, DECISIONS §5). */
class MeleeEnemyTest {
  private final World world = new World();
  private final List<GameEvent> events = new ArrayList<>();

  MeleeEnemyTest() {
    // Hits only: an attack also publishes AttackStarted, tested in MeleeEnemyTest.
    world.getEventBus().subscribe(DamageDealt.class, events::add);
    world.getEventBus().subscribe(EntityDied.class, events::add);
  }

  @Test
  void should_hit_its_target_with_the_damage_formula_when_in_reach() {
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18, 0);
    rat.alert(player);

    rat.attackTarget(world);

    assertEquals(8, player.getHp());
  }

  @Test
  void should_publish_the_damage_it_deals() {
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18, 0);
    rat.alert(player);

    rat.attackTarget(world);
    world.getEventBus().flush();

    assertEquals(List.of(new DamageDealt(rat, player, 2)), events);
  }

  @Test
  void should_publish_the_death_when_its_hit_kills() {
    MeleeEnemy rat = aRat().build();
    Aabb box = new Aabb(18, 0, 16, 16);
    Character player =
        aCharacter()
            .withFaction(Faction.PLAYER)
            .withBodyBox(box)
            .withHurtbox(box)
            .withHp(2)
            .build();
    world.add(player);
    rat.alert(player);

    rat.attackTarget(world);
    world.getEventBus().flush();

    assertEquals(
        List.of(new DamageDealt(rat, player, 2), new EntityDied(player, Optional.of(rat))), events);
  }

  @Test
  void should_hit_with_its_own_attack_stat() {
    MeleeEnemy zombie = aZombie().build();
    Character player = aPlayerAt(18, 0);
    zombie.alert(player);

    zombie.attackTarget(world);

    assertEquals(7, player.getHp());
  }

  @Test
  void should_not_hit_a_target_beyond_its_reach() {
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(21, 0);
    rat.alert(player);

    rat.attackTarget(world);

    assertEquals(10, player.getHp());
  }

  @Test
  void should_not_hit_when_it_has_no_target() {
    MeleeEnemy rat = aRat().build();

    rat.attackTarget(world);
    world.getEventBus().flush();

    assertTrue(events.isEmpty());
  }

  @Test
  void should_publish_the_start_of_its_attack_even_when_it_does_no_damage() {
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18, 0);
    player.grantInvincibility(10);
    rat.alert(player);
    List<GameEvent> attacks = new ArrayList<>();
    world.getEventBus().subscribe(AttackStarted.class, attacks::add);

    rat.attackTarget(world);
    world.getEventBus().flush();

    assertEquals(List.of(new AttackStarted(rat)), attacks);
  }

  @Test
  void should_not_hit_an_invincible_target() {
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18, 0);
    player.grantInvincibility(10);
    rat.alert(player);

    rat.attackTarget(world);
    world.getEventBus().flush();

    assertEquals(10, player.getHp());
    assertTrue(events.isEmpty());
  }

  @Test
  void should_not_hit_a_target_that_died_since_the_alert() {
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18, 0);
    rat.alert(player);
    player.takeDamage(player.getHp());

    rat.attackTarget(world);
    world.getEventBus().flush();

    assertTrue(events.isEmpty());
  }

  @Test
  void should_start_its_cooldown_when_it_attacks() {
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18, 0);
    rat.alert(player);

    rat.attackTarget(world);

    assertFalse(rat.canAttack());
  }

  @Test
  void should_bite_only_in_front_of_it_for_a_rat() {
    MeleeEnemy rat = aRat().build();
    Character front = aPlayerAt(18, 0);
    Character behind = aPlayerAt(-18, 0);
    rat.alert(front);

    rat.attackTarget(world);

    assertEquals(8, front.getHp());
    assertEquals(10, behind.getHp());
  }

  @Test
  void should_bite_only_in_front_of_it_for_a_zombie() {
    MeleeEnemy zombie = aZombie().build();
    Character front = aPlayerAt(18, 0);
    Character behind = aPlayerAt(-18, 0);
    zombie.alert(front);

    zombie.attackTarget(world);

    assertEquals(7, front.getHp());
    assertEquals(10, behind.getHp());
  }

  @Test
  void should_give_its_reach() {
    MeleeEnemy zombie = aZombie().withReach(8).build();

    assertEquals(8, zombie.getReach());
  }

  @Test
  void should_reject_a_negative_reach() {
    EnemyBuilder rat = aRat().withReach(-1);

    assertThrows(IllegalArgumentException.class, rat::build);
  }

  private Character aPlayerAt(float x, float y) {
    Aabb box = new Aabb(x, y, 16, 16);
    Character player =
        aCharacter().withFaction(Faction.PLAYER).withBodyBox(box).withHurtbox(box).build();
    world.add(player);
    return player;
  }
}
