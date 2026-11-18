package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.item.FireRing;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: what every contact attack does, checked on the all-around one. */
class ContactAttackTest {

  @Test
  void should_keep_its_reach_when_created() {
    assertEquals(6f, new AroundContactAttack(6f).getReach());
  }

  @Test
  void should_accept_a_reach_of_0_for_touching_only() {
    assertEquals(0f, new AroundContactAttack(0f).getReach());
  }

  @Test
  void should_throw_when_the_reach_is_negative() {
    assertThrows(IllegalArgumentException.class, () -> new AroundContactAttack(-1f));
  }

  @Test
  void should_throw_when_the_reach_is_not_a_number() {
    assertThrows(IllegalArgumentException.class, () -> new AroundContactAttack(Float.NaN));
  }

  @Test
  void should_throw_when_the_reach_is_infinite() {
    assertThrows(
        IllegalArgumentException.class, () -> new AroundContactAttack(Float.POSITIVE_INFINITY));
  }

  @Test
  void should_deal_the_damage_of_the_formula() {
    Character attacker = aCharacter().withAttack(5).build();
    Character target = aCharacter().withHp(10).withDefense(1).build();

    HitResult result = new AroundContactAttack(0f).hit(attacker, target);

    assertEquals(new HitResult(5, false), result);
    assertEquals(5, target.getHp());
  }

  @Test
  void should_tell_when_the_hit_kills() {
    Character attacker = aCharacter().withAttack(5).build();
    Character target = aCharacter().withHp(3).withDefense(1).build();

    assertTrue(new AroundContactAttack(0f).hit(attacker, target).killed());
  }

  @Test
  void should_not_hit_a_dead_target() {
    Character attacker = aCharacter().withAttack(5).build();
    Character target = aCharacter().withHp(3).withDefense(1).build();
    target.takeDamage(3);

    assertEquals(HitResult.NONE, new AroundContactAttack(0f).hit(attacker, target));
  }

  @Test
  void should_not_hit_an_invincible_target() {
    Character attacker = aCharacter().withAttack(5).build();
    Character target = aCharacter().withHp(10).withDefense(1).build();
    target.grantInvincibility(10);

    assertEquals(HitResult.NONE, new AroundContactAttack(0f).hit(attacker, target));
    assertEquals(10, target.getHp());
  }

  @Test
  void should_throw_when_the_attacker_is_null() {
    Character target = aCharacter().build();
    ContactAttack attack = new AroundContactAttack(0f);

    assertThrows(NullPointerException.class, () -> attack.hit(null, target));
  }

  @Test
  void should_throw_when_the_target_is_null() {
    Character attacker = aCharacter().build();
    ContactAttack attack = new AroundContactAttack(0f);

    assertThrows(NullPointerException.class, () -> attack.hit(attacker, null));
  }

  @Test
  void should_hit_a_hostile_character_in_reach() {
    World world = new World();
    Character owner = anOwner(world);
    Character target = aCharacterAt(world, 18f, Faction.PLAYER);

    new AroundContactAttack(4f).attack(owner, 0f, world);

    assertEquals(9, target.getHp());
  }

  @Test
  void should_not_hit_a_character_out_of_reach() {
    World world = new World();
    Character owner = anOwner(world);
    Character target = aCharacterAt(world, 30f, Faction.PLAYER);

    new AroundContactAttack(4f).attack(owner, 0f, world);

    assertEquals(10, target.getHp());
  }

  @Test
  void should_not_hit_its_own_side() {
    World world = new World();
    Character owner = anOwner(world);
    Character ally = aCharacterAt(world, 18f, Faction.ENEMY);

    new AroundContactAttack(4f).attack(owner, 0f, world);

    assertEquals(10, ally.getHp());
  }

  @Test
  void should_hit_every_hostile_character_in_reach() {
    World world = new World();
    Character owner = anOwner(world);
    Character right = aCharacterAt(world, 18f, Faction.PLAYER);
    Character left = aCharacterAt(world, -18f, Faction.PLAYER);

    new AroundContactAttack(4f).attack(owner, 0f, world);

    assertEquals(9, right.getHp());
    assertEquals(9, left.getHp());
  }

  @Test
  void should_not_hit_its_owner_even_when_neutral() {
    World world = new World();
    Character owner = aCharacterAt(world, 0f, Faction.NEUTRAL);

    new AroundContactAttack(4f).attack(owner, 0f, world);

    assertEquals(10, owner.getHp());
  }

  @Test
  void should_publish_the_damage_it_deals() {
    World world = new World();
    Character owner = anOwner(world);
    Character target = aCharacterAt(world, 18f, Faction.PLAYER);
    List<GameEvent> seen = new ArrayList<>();
    world.getEventBus().subscribeAll(seen::add);

    new AroundContactAttack(4f).attack(owner, 0f, world);
    world.getEventBus().flush();

    assertEquals(List.of(new DamageDealt(owner, target, 1)), seen);
  }

  @Test
  void should_publish_the_death_of_the_character_it_kills() {
    World world = new World();
    Character owner = anOwner(world);
    Aabb box = new Aabb(18f, 0f, 16f, 16f);
    Character target =
        aCharacter()
            .withBodyBox(box)
            .withHurtbox(box)
            .withHp(1)
            .withFaction(Faction.PLAYER)
            .build();
    world.add(target);
    List<GameEvent> seen = new ArrayList<>();
    world.getEventBus().subscribeAll(seen::add);

    new AroundContactAttack(4f).attack(owner, 0f, world);
    world.getEventBus().flush();

    assertEquals(
        List.of(new DamageDealt(owner, target, 1), new EntityDied(target, Optional.of(owner))),
        seen);
  }

  @Test
  void should_start_the_cooldown_of_its_owner() {
    World world = new World();
    Character owner = anOwner(world);

    new AroundContactAttack(4f).attack(owner, 0f, world);

    assertFalse(owner.canAttack());
  }

  @Test
  void should_throw_when_the_owner_cannot_attack_now() {
    World world = new World();
    Character owner = anOwner(world);
    ContactAttack attack = new AroundContactAttack(4f);
    attack.attack(owner, 0f, world);

    assertThrows(IllegalStateException.class, () -> attack.attack(owner, 0f, world));
  }

  @Test
  void should_keep_the_owner_ready_when_the_aim_is_wrong() {
    World world = new World();
    Character owner = anOwner(world);
    ContactAttack attack = new AroundContactAttack(4f);

    assertThrows(IllegalArgumentException.class, () -> attack.attack(owner, Float.NaN, world));
    assertTrue(owner.canAttack());
  }

  @Test
  void should_throw_when_the_owner_is_null() {
    World world = new World();
    ContactAttack attack = new AroundContactAttack(4f);

    assertThrows(NullPointerException.class, () -> attack.attack(null, 0f, world));
  }

  @Test
  void should_throw_when_the_world_is_null() {
    Character owner = anOwner(new World());
    ContactAttack attack = new AroundContactAttack(4f);

    assertThrows(NullPointerException.class, () -> attack.attack(owner, 0f, null));
    assertTrue(owner.canAttack());
  }

  @Test
  void should_hit_through_the_attack_of_its_owner() {
    World world = new World();
    Character owner = anOwner(world);
    owner.rebuild(List.of(aFireRing()));
    Character target = aCharacterAt(world, 18f, Faction.PLAYER);

    new AroundContactAttack(4f).attack(owner, 0f, world);
    target.update(1);

    assertEquals(7, target.getHp());
  }

  private static FireRing aFireRing() {
    return new FireRing(
        "fire_ring", "item.fire_ring.name", "items/fire_ring", 0, new BurnSpec(2, 1, 3));
  }

  private static Character anOwner(World world) {
    return aCharacterAt(world, 0f, Faction.ENEMY);
  }

  private static Character aCharacterAt(World world, float x, Faction faction) {
    Aabb box = new Aabb(x, 0f, 16f, 16f);
    Character character =
        aCharacter().withBodyBox(box).withHurtbox(box).withFaction(faction).build();
    world.add(character);
    return character;
  }
}
