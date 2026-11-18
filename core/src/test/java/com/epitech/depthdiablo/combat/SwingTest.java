package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.EventBus;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.item.FireRing;
import com.epitech.depthdiablo.item.MeleeWeapon;
import com.epitech.depthdiablo.item.Sword;
import com.epitech.depthdiablo.testdata.Items;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** GDD 1.6: a swing sweeps the blade of a melee weapon around its holder. */
class SwingTest {
  private static final float DELTA = 0.001f;
  private static final Supplier<List<Entity>> NO_TARGETS = List::of;

  /** Centre (100, 50); its hurtbox is its body. Its ATTACK of 10 already counts its weapon. */
  private final Character holder =
      aCharacter()
          .withBodyBox(new Aabb(92, 42, 16, 16))
          .withHurtbox(new Aabb(92, 42, 16, 16))
          .withAttack(10)
          .withFaction(Faction.PLAYER)
          .build();

  @Test
  void should_start_the_blade_half_an_arc_before_the_aim() {
    Swing swing = aSwing(Items.sword(), 0f);

    assertEquals(-45f, swing.getShape().angleDegrees(), DELTA);
  }

  @Test
  void should_turn_the_blade_around_the_holder_centre() {
    Swing swing = aSwing(Items.sword(), 0f);

    // half a blade (12 px) from (100, 50), at -45 degrees
    assertEquals(108.485f, swing.getShape().centreX(), DELTA);
    assertEquals(41.515f, swing.getShape().centreY(), DELTA);
  }

  @Test
  void should_keep_its_aim() {
    assertEquals(30f, aSwing(Items.sword(), 30f).getAimDegrees());
  }

  @Test
  void should_not_be_over_when_it_starts() {
    Swing swing = aSwing(Items.sword(), 0f);

    assertEquals(0, swing.getTicksElapsed());
    assertFalse(swing.isOver());
  }

  @Test
  void should_throw_when_the_aim_is_not_a_number() {
    Sword sword = Items.sword();

    assertThrows(IllegalArgumentException.class, () -> aSwing(sword, Float.NaN));
  }

  @Test
  void should_throw_when_the_aim_is_infinite() {
    Sword sword = Items.sword();

    assertThrows(IllegalArgumentException.class, () -> aSwing(sword, Float.POSITIVE_INFINITY));
  }

  @Test
  void should_throw_when_the_weapon_is_null() {
    EventBus bus = new EventBus();

    assertThrows(NullPointerException.class, () -> new Swing(null, holder, 0f, NO_TARGETS, bus));
  }

  @Test
  void should_throw_when_the_holder_is_null() {
    Sword sword = Items.sword();
    EventBus bus = new EventBus();

    assertThrows(NullPointerException.class, () -> new Swing(sword, null, 0f, NO_TARGETS, bus));
  }

  @Test
  void should_throw_when_the_targets_are_null() {
    Sword sword = Items.sword();
    EventBus bus = new EventBus();

    assertThrows(NullPointerException.class, () -> new Swing(sword, holder, 0f, null, bus));
  }

  @Test
  void should_throw_when_the_events_are_null() {
    Sword sword = Items.sword();

    assertThrows(NullPointerException.class, () -> new Swing(sword, holder, 0f, NO_TARGETS, null));
  }

  @Test
  void should_hit_an_enemy_the_blade_touches() {
    Character enemy = anEnemyAt(110, 46, 20);

    runWholeSwing(aSwingAt(enemy));

    assertEquals(17, enemy.getHp());
  }

  @Test
  void should_hit_an_enemy_only_once_per_swing() {
    Character enemy = anEnemyAt(110, 46, 20); // no i-frames: only the hitbox stops a second hit

    runWholeSwing(aSwingAt(enemy));

    assertEquals(17, enemy.getHp());
  }

  @Test
  void should_not_hit_an_enemy_out_of_reach() {
    Character enemy = anEnemyAt(200, 200, 20);

    runWholeSwing(aSwingAt(enemy));

    assertEquals(20, enemy.getHp());
  }

  @Test
  void should_not_hit_a_character_of_its_own_side() {
    Character ally =
        aCharacter()
            .withHp(20)
            .withHurtbox(new Aabb(110, 46, 8, 8))
            .withFaction(Faction.PLAYER)
            .build();

    runWholeSwing(aSwingAt(ally));

    assertEquals(20, ally.getHp());
  }

  @Test
  void should_not_hit_its_holder() {
    runWholeSwing(aSwingAt(holder));

    assertEquals(10, holder.getHp());
  }

  @Test
  void should_hit_nothing_when_its_holder_is_dead() {
    Character enemy = anEnemyAt(110, 46, 20);
    Swing swing = aSwingAt(enemy);
    holder.takeDamage(10);

    runWholeSwing(swing);

    assertEquals(20, enemy.getHp());
  }

  @Test
  void should_publish_the_damage_it_deals() {
    Character enemy = anEnemyAt(110, 46, 20);
    EventBus events = new EventBus();
    List<GameEvent> seen = new ArrayList<>();
    events.subscribeAll(seen::add);

    runWholeSwing(new Swing(Items.sword(), holder, 0f, () -> List.of(enemy), events));
    events.flush();

    assertEquals(List.of(new DamageDealt(holder, enemy, 3)), seen);
  }

  @Test
  void should_publish_the_death_of_the_enemy_it_kills() {
    Character enemy = anEnemyAt(110, 46, 3);
    EventBus events = new EventBus();
    List<GameEvent> seen = new ArrayList<>();
    events.subscribeAll(seen::add);

    runWholeSwing(new Swing(Items.sword(), holder, 0f, () -> List.of(enemy), events));
    events.flush();

    assertEquals(
        List.of(new DamageDealt(holder, enemy, 3), new EntityDied(enemy, Optional.of(holder))),
        seen);
  }

  @Test
  void should_end_the_sweep_half_an_arc_after_the_aim() {
    Swing swing = aSwing(Items.sword(), 0f);

    runWholeSwing(swing);

    assertEquals(45f, swing.getShape().angleDegrees(), DELTA);
  }

  @Test
  void should_be_over_after_its_swing_ticks() {
    Sword sword = Items.sword();
    Swing swing = aSwing(sword, 0f);
    int last = sword.getSwingTicks();

    for (long tick = 1; tick < last; tick++) {
      swing.update(tick);
    }
    assertFalse(swing.isOver());

    swing.update(last);
    assertTrue(swing.isOver());
  }

  @Test
  void should_do_nothing_once_over() {
    Sword sword = Items.sword();
    Swing swing = aSwing(sword, 0f);
    runWholeSwing(swing);

    swing.update(sword.getSwingTicks() + 1L);

    assertEquals(sword.getSwingTicks(), swing.getTicksElapsed());
  }

  @Test
  void should_start_with_no_progress() {
    assertEquals(0f, aSwingAt(anEnemyAt(500, 500, 20)).getProgress(), 0.001f);
  }

  @Test
  void should_be_halfway_after_half_of_its_ticks() {
    Swing swing = aSwingAt(anEnemyAt(500, 500, 20));

    for (long tick = 1; tick <= 6; tick++) { // the sword swings for 12 ticks
      swing.update(tick);
    }

    assertEquals(0.5f, swing.getProgress(), 0.001f);
  }

  @Test
  void should_be_complete_once_over() {
    Swing swing = aSwingAt(anEnemyAt(500, 500, 20));

    runWholeSwing(swing);

    assertEquals(1f, swing.getProgress(), 0.001f);
  }

  @Test
  void should_hit_through_the_attack_of_its_holder() {
    holder.rebuild(List.of(aFireRing()));
    Character enemy = anEnemyAt(110, 46, 20);

    runWholeSwing(aSwingAt(enemy));
    enemy.update(1);

    assertEquals(15, enemy.getHp());
  }

  private static FireRing aFireRing() {
    return new FireRing(
        "fire_ring", "item.fire_ring.name", "items/fire_ring", 0, new BurnSpec(2, 1, 3));
  }

  private Swing aSwingAt(Character target) {
    return new Swing(Items.sword(), holder, 0f, () -> List.of(target), new EventBus());
  }

  private static Character anEnemyAt(float x, float y, int hp) {
    return aCharacter().withHp(hp).withDefense(3).withHurtbox(new Aabb(x, y, 8, 8)).build();
  }

  private static void runWholeSwing(Swing swing) {
    for (long tick = 1; !swing.isOver(); tick++) {
      swing.update(tick);
    }
  }

  private Swing aSwing(MeleeWeapon weapon, float aimDegrees) {
    return new Swing(weapon, holder, aimDegrees, NO_TARGETS, new EventBus());
  }
}
