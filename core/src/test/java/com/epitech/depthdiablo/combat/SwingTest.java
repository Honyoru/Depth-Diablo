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
import com.epitech.depthdiablo.item.MeleeWeapon;
import com.epitech.depthdiablo.item.Rarity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** GDD 1.6: a swing sweeps the blade of a melee weapon around its holder. */
class SwingTest {
  private static final float DELTA = 0.001f;
  private static final Supplier<List<Entity>> NO_TARGETS = List::of;

  /** Centre (100, 50); its hurtbox is its body. */
  private final Character holder =
      aCharacter()
          .withBodyBox(new Aabb(92, 42, 16, 16))
          .withHurtbox(new Aabb(92, 42, 16, 16))
          .withAttack(10)
          .withFaction(Faction.PLAYER)
          .build();

  @Test
  void should_start_the_blade_half_an_arc_before_the_aim() {
    Swing swing = aSwing(sword(12), 0f);

    assertEquals(-45f, swing.shape().angleDegrees(), DELTA);
  }

  @Test
  void should_turn_the_blade_around_the_holder_centre() {
    Swing swing = aSwing(sword(12), 0f);

    // half a blade (12 px) from (100, 50), at -45 degrees
    assertEquals(108.485f, swing.shape().centreX(), DELTA);
    assertEquals(41.515f, swing.shape().centreY(), DELTA);
  }

  @Test
  void should_stab_straight_at_the_aim_when_it_lasts_1_tick() {
    Swing swing = aSwing(sword(1), 30f);

    assertEquals(30f, swing.shape().angleDegrees(), DELTA);
  }

  @Test
  void should_keep_its_aim() {
    assertEquals(30f, aSwing(sword(12), 30f).aimDegrees());
  }

  @Test
  void should_not_be_over_when_it_starts() {
    Swing swing = aSwing(sword(12), 0f);

    assertEquals(0, swing.ticksElapsed());
    assertFalse(swing.isOver());
  }

  @Test
  void should_throw_when_the_aim_is_not_a_number() {
    assertThrows(IllegalArgumentException.class, () -> aSwing(sword(12), Float.NaN));
  }

  @Test
  void should_throw_when_the_aim_is_infinite() {
    assertThrows(IllegalArgumentException.class, () -> aSwing(sword(12), Float.POSITIVE_INFINITY));
  }

  @Test
  void should_throw_when_the_weapon_is_null() {
    assertThrows(
        NullPointerException.class, () -> new Swing(null, holder, 0f, NO_TARGETS, new EventBus()));
  }

  @Test
  void should_throw_when_the_holder_is_null() {
    assertThrows(
        NullPointerException.class,
        () -> new Swing(sword(12), null, 0f, NO_TARGETS, new EventBus()));
  }

  @Test
  void should_throw_when_the_targets_are_null() {
    assertThrows(
        NullPointerException.class, () -> new Swing(sword(12), holder, 0f, null, new EventBus()));
  }

  @Test
  void should_throw_when_the_events_are_null() {
    assertThrows(
        NullPointerException.class, () -> new Swing(sword(12), holder, 0f, NO_TARGETS, null));
  }

  @Test
  void should_hit_an_enemy_the_blade_touches() {
    Character enemy = anEnemyAt(110, 46, 20);

    runWholeSwing(aSwingAt(enemy));

    assertEquals(16, enemy.hp());
  }

  @Test
  void should_hit_an_enemy_only_once_per_swing() {
    Character enemy = anEnemyAt(110, 46, 20); // no i-frames: only the hitbox stops a second hit

    runWholeSwing(aSwingAt(enemy));

    assertEquals(16, enemy.hp());
  }

  @Test
  void should_not_hit_an_enemy_out_of_reach() {
    Character enemy = anEnemyAt(200, 200, 20);

    runWholeSwing(aSwingAt(enemy));

    assertEquals(20, enemy.hp());
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

    assertEquals(20, ally.hp());
  }

  @Test
  void should_not_hit_its_holder() {
    runWholeSwing(aSwingAt(holder));

    assertEquals(10, holder.hp());
  }

  @Test
  void should_hit_nothing_when_its_holder_is_dead() {
    Character enemy = anEnemyAt(110, 46, 20);
    Swing swing = aSwingAt(enemy);
    holder.takeDamage(10);

    runWholeSwing(swing);

    assertEquals(20, enemy.hp());
  }

  @Test
  void should_publish_the_damage_it_deals() {
    Character enemy = anEnemyAt(110, 46, 20);
    EventBus events = new EventBus();
    List<GameEvent> seen = new ArrayList<>();
    events.subscribeAll(seen::add);

    runWholeSwing(new Swing(sword(12), holder, 0f, () -> List.of(enemy), events));
    events.flush();

    assertEquals(List.of(new DamageDealt(holder, enemy, 4)), seen);
  }

  @Test
  void should_publish_the_death_of_the_enemy_it_kills() {
    Character enemy = anEnemyAt(110, 46, 4);
    EventBus events = new EventBus();
    List<GameEvent> seen = new ArrayList<>();
    events.subscribeAll(seen::add);

    runWholeSwing(new Swing(sword(12), holder, 0f, () -> List.of(enemy), events));
    events.flush();

    assertEquals(
        List.of(new DamageDealt(holder, enemy, 4), new EntityDied(enemy, Optional.of(holder))),
        seen);
  }

  @Test
  void should_end_the_sweep_half_an_arc_after_the_aim() {
    Swing swing = aSwing(sword(12), 0f);

    runWholeSwing(swing);

    assertEquals(45f, swing.shape().angleDegrees(), DELTA);
  }

  @Test
  void should_be_over_after_its_swing_ticks() {
    Swing swing = aSwing(sword(12), 0f);

    for (long tick = 1; tick <= 11; tick++) {
      swing.update(tick);
    }
    assertFalse(swing.isOver());

    swing.update(12);
    assertTrue(swing.isOver());
  }

  @Test
  void should_do_nothing_once_over() {
    Swing swing = aSwing(sword(12), 0f);
    runWholeSwing(swing);

    swing.update(13);

    assertEquals(12, swing.ticksElapsed());
  }

  private Swing aSwingAt(Character target) {
    return new Swing(sword(12), holder, 0f, () -> List.of(target), new EventBus());
  }

  private static Character anEnemyAt(float x, float y, int hp) {
    return aCharacter().withHp(hp).withDefense(3).withHurtbox(new Aabb(x, y, 8, 8)).build();
  }

  private static void runWholeSwing(Swing swing) {
    for (long tick = 1; tick <= 12; tick++) {
      swing.update(tick);
    }
  }

  private Swing aSwing(MeleeWeapon weapon, float aimDegrees) {
    return new Swing(weapon, holder, aimDegrees, NO_TARGETS, new EventBus());
  }

  /** Placeholder sword: range 24, width 8, arc 90 degrees, +2 attack. */
  private static MeleeWeapon sword(int swingTicks) {
    return new FakeMeleeWeapon(swingTicks);
  }

  /** Hand-written fake (Q117): a melee weapon with only the shared MeleeWeapon behaviour. */
  private static final class FakeMeleeWeapon extends MeleeWeapon {
    FakeMeleeWeapon(int swingTicks) {
      super(
          "sword_iron",
          "item.sword_iron.name",
          "sword",
          Rarity.COMMON,
          30,
          2,
          24f,
          8f,
          90f,
          swingTicks);
    }
  }
}
