package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.combat.AroundContactAttack;
import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.combat.Burn;
import com.epitech.depthdiablo.combat.BurnSpec;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.combat.HitResult;
import com.epitech.depthdiablo.combat.StatusEffect;
import com.epitech.depthdiablo.combat.Swing;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.EventBus;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.item.FireRing;
import com.epitech.depthdiablo.item.Sword;
import com.epitech.depthdiablo.testdata.CharacterBuilder;
import com.epitech.depthdiablo.testdata.Items;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1 and 1.5: HP, death, invincibility frames and the update template of a Character. */
class CharacterTest {

  @Test
  void should_start_with_full_hp_when_created() {
    Character character = aCharacter().withHp(12).build();

    int hp = character.getHp();

    assertEquals(12, hp);
    assertEquals(12, character.getMaxHp());
  }

  @Test
  void should_lose_hp_when_it_takes_damage() {
    Character character = aCharacter().withHp(10).build();

    character.takeDamage(3);

    assertEquals(7, character.getHp());
  }

  @Test
  void should_die_when_hp_reaches_0() {
    Character character = aCharacter().withHp(5).build();

    character.takeDamage(5);

    assertTrue(character.isDead());
  }

  @Test
  void should_stay_alive_when_hp_is_above_0() {
    Character character = aCharacter().withHp(5).build();

    character.takeDamage(4);

    assertFalse(character.isDead());
  }

  @Test
  void should_stop_at_0_hp_when_the_damage_is_higher_than_the_hp() {
    Character character = aCharacter().withHp(5).build();

    character.takeDamage(50);

    assertEquals(0, character.getHp());
  }

  @Test
  void should_take_no_more_damage_when_dead() {
    Character character = aCharacter().withHp(5).build();
    character.takeDamage(5);

    character.takeDamage(1);

    assertEquals(0, character.getHp());
  }

  @Test
  void should_not_be_invincible_when_created() {
    Character character = aCharacter().build();

    boolean invincible = character.isInvincible();

    assertFalse(invincible);
  }

  @Test
  void should_take_no_damage_when_invincible() {
    Character character = aCharacter().withHp(10).build();
    character.grantInvincibility(30);

    character.takeDamage(3);

    assertEquals(10, character.getHp());
  }

  @Test
  void should_ignore_a_second_hit_when_it_lands_during_the_i_frames() {
    Character character = aCharacter().withHp(10).withInvincibilityAfterHit(30).build();
    character.takeDamage(3);

    character.takeDamage(3);

    assertEquals(7, character.getHp());
  }

  @Test
  void should_take_damage_again_when_the_i_frames_are_over() {
    Character character = aCharacter().withHp(10).withInvincibilityAfterHit(2).build();
    character.takeDamage(3);
    character.update(1);
    character.update(2);

    character.takeDamage(3);

    assertEquals(4, character.getHp());
  }

  @Test
  void should_keep_the_longer_invincibility_when_a_shorter_one_is_granted() {
    Character character = aCharacter().build();
    character.grantInvincibility(10);
    character.grantInvincibility(2);
    character.update(1);
    character.update(2);

    boolean invincible = character.isInvincible();

    assertTrue(invincible);
  }

  @Property
  void should_be_invincible_for_exactly_the_granted_ticks(
      @ForAll @IntRange(min = 1, max = 120) int ticks) {
    Character character = aCharacter().build();
    character.grantInvincibility(ticks);

    for (int t = 1; t < ticks; t++) {
      character.update(t);
    }
    boolean stillInvincible = character.isInvincible();
    character.update(ticks);
    boolean invincibleAfter = character.isInvincible();

    assertTrue(stillInvincible);
    assertFalse(invincibleAfter);
  }

  @Test
  void should_call_think_once_per_update() {
    CountingCharacter character = new CountingCharacter();

    character.update(1);
    character.update(2);
    character.update(3);

    assertEquals(3, character.thinks);
    assertEquals(3, character.lastTick);
  }

  @Test
  void should_not_call_think_when_the_character_was_removed() {
    CountingCharacter character = new CountingCharacter();
    character.remove();

    character.update(1);

    assertEquals(0, character.thinks);
  }

  @Test
  void should_return_the_body_box_it_was_built_with() {
    Aabb box = new Aabb(4, 8, 16, 16);
    Character character = aCharacter().withBodyBox(box).build();

    Aabb bodyBox = character.getBodyBox();

    assertSame(box, bodyBox);
  }

  @Test
  void should_return_the_hurtbox_it_was_built_with() {
    Aabb box = new Aabb(4, 8, 16, 16);
    Character character = aCharacter().withHurtbox(box).build();

    Aabb hurtbox = character.getHurtbox();

    assertEquals(box, hurtbox);
  }

  @Test
  void should_reject_damage_below_1() {
    Character character = aCharacter().build();

    assertThrows(IllegalArgumentException.class, () -> character.takeDamage(0));
  }

  @Test
  void should_reject_a_negative_invincibility() {
    Character character = aCharacter().build();

    assertThrows(IllegalArgumentException.class, () -> character.grantInvincibility(-1));
  }

  @Test
  void should_reject_negative_i_frames() {
    int ticks = -1;

    CharacterBuilder builder = aCharacter().withInvincibilityAfterHit(ticks);

    assertThrows(IllegalArgumentException.class, builder::build);
  }

  @Test
  void should_reject_a_missing_body_box() {
    CharacterBuilder builder = aCharacter().withBodyBox(null);

    assertThrows(NullPointerException.class, builder::build);
  }

  @Test
  void should_reject_a_missing_hurtbox() {
    CharacterBuilder builder = aCharacter().withHurtbox(null);

    assertThrows(NullPointerException.class, builder::build);
  }

  @Test
  void should_gain_hp_when_it_heals() {
    Character character = aCharacter().withHp(10).build();
    character.takeDamage(6);

    character.heal(4);

    assertEquals(8, character.getHp());
  }

  @Test
  void should_stop_at_max_hp_when_the_heal_is_higher_than_the_missing_hp() {
    Character character = aCharacter().withHp(10).build();
    character.takeDamage(2);

    character.heal(50);

    assertEquals(10, character.getMaxHp());
  }

  @Test
  void should_stay_dead_when_a_dead_character_heals() {
    Character character = aCharacter().withHp(5).build();
    character.takeDamage(5);

    character.heal(3);

    assertTrue(character.isDead());
  }

  @Test
  void should_throw_when_the_heal_amount_is_below_1() {
    Character character = aCharacter().build();

    assertThrows(IllegalArgumentException.class, () -> character.heal(0));
  }

  @Test
  void should_keep_its_attack_and_defense_when_created() {
    Character character = aCharacter().withAttack(7).withDefense(3).build();

    assertEquals(7, character.getAttack());
    assertEquals(3, character.getDefense());
  }

  @Test
  void should_accept_an_attack_of_0() {
    Character character = aCharacter().withAttack(0).build();

    assertEquals(0, character.getAttack());
  }

  @Test
  void should_keep_its_speed_and_attack_speed_when_created() {
    Character character = aCharacter().withSpeed(120f).withAttackSpeed(1.5f).build();

    assertEquals(120f, character.getSpeed());
    assertEquals(1.5f, character.getAttackSpeed());
  }

  @Test
  void should_accept_a_speed_and_attack_speed_of_0() {
    Character character = aCharacter().withSpeed(0f).withAttackSpeed(0f).build();

    assertEquals(0f, character.getSpeed());
    assertEquals(0f, character.getAttackSpeed());
  }

  @Test
  void should_round_down_its_int_stats_when_the_source_gives_a_fraction() {
    StatSource stats = stat -> 3.7f;

    Character character = new CountingCharacter(stats);

    assertEquals(3, character.getMaxHp());
    assertEquals(3, character.getAttack());
    assertEquals(3, character.getDefense());
  }

  @Test
  void should_be_able_to_attack_when_created() {
    assertTrue(aCharacter().build().canAttack());
  }

  @Test
  void should_not_attack_again_during_its_cooldown() {
    Character character = aCharacter().withAttackSpeed(1f).build();

    character.startAttackCooldown(0);

    assertFalse(character.canAttack());
  }

  @Test
  void should_attack_again_after_60_ticks_at_one_attack_per_second() {
    Character character = aCharacter().withAttackSpeed(1f).build();
    character.startAttackCooldown(0);

    for (long tick = 1; tick <= 59; tick++) {
      character.update(tick);
    }
    assertFalse(character.canAttack());

    character.update(60);
    assertTrue(character.canAttack());
  }

  @Test
  void should_wait_half_as_long_at_two_attacks_per_second() {
    Character character = aCharacter().withAttackSpeed(2f).build();
    character.startAttackCooldown(0);

    for (long tick = 1; tick <= 30; tick++) {
      character.update(tick);
    }

    assertTrue(character.canAttack());
  }

  @Test
  void should_never_attack_with_an_attack_speed_of_0() {
    assertFalse(aCharacter().withAttackSpeed(0f).build().canAttack());
  }

  @Test
  void should_not_attack_when_dead() {
    Character character = aCharacter().withHp(1).build();
    character.takeDamage(1);

    assertFalse(character.canAttack());
  }

  @Test
  void should_throw_when_starting_a_cooldown_it_cannot_start() {
    Character character = aCharacter().build();
    character.startAttackCooldown(0);

    assertThrows(IllegalStateException.class, () -> character.startAttackCooldown(0));
  }

  @Test
  void should_not_attack_again_during_its_swing() {
    Character character = aCharacter().withAttackSpeed(1f).build();
    character.startAttackCooldown(12);

    for (long tick = 1; tick <= 11; tick++) {
      character.update(tick);
    }

    assertFalse(character.canAttack());
  }

  @Test
  void should_count_the_cooldown_only_once_the_swing_is_over() {
    Character character = aCharacter().withAttackSpeed(1f).build();
    character.startAttackCooldown(12);

    for (long tick = 1; tick <= 71; tick++) {
      character.update(tick);
    }
    assertFalse(character.canAttack());

    character.update(72);
    assertTrue(character.canAttack());
  }

  @Test
  void should_throw_when_the_swing_ticks_are_negative() {
    Character character = aCharacter().build();

    assertThrows(IllegalArgumentException.class, () -> character.startAttackCooldown(-1));
  }

  @Test
  void should_keep_its_cooldown_when_an_attack_is_refused() {
    Character character = aCharacter().withAttackSpeed(1f).build();
    character.startAttackCooldown(0);
    for (long tick = 1; tick <= 30; tick++) {
      character.update(tick);
    }

    assertThrows(IllegalStateException.class, () -> character.startAttackCooldown(0));

    for (long tick = 31; tick <= 60; tick++) {
      character.update(tick);
    }
    assertTrue(character.canAttack()); // still ready at tick 60, not at 90
  }

  @Test
  void should_not_swing_when_created() {
    assertTrue(new CountingCharacter().getCurrentSwing().isEmpty());
  }

  @Test
  void should_hold_the_swing_it_starts() {
    CountingCharacter character = new CountingCharacter();

    character.startAttack(Items.sword(), 30f, new World());

    assertEquals(30f, character.getCurrentSwing().orElseThrow().getAimDegrees());
  }

  @Test
  void should_run_its_swing_once_per_update() {
    CountingCharacter character = new CountingCharacter();
    character.startAttack(Items.sword(), 0f, new World());
    Swing swing = character.getCurrentSwing().orElseThrow();

    character.update(1);
    character.update(2);

    assertEquals(2, swing.getTicksElapsed());
  }

  @Test
  void should_drop_its_swing_once_it_is_over() {
    CountingCharacter character = new CountingCharacter();
    Sword sword = Items.sword();
    character.startAttack(sword, 0f, new World());

    for (long tick = 1; tick <= sword.getSwingTicks(); tick++) {
      character.update(tick);
    }

    assertTrue(character.getCurrentSwing().isEmpty());
  }

  @Test
  void should_keep_its_swing_when_a_second_one_is_refused() {
    CountingCharacter character = new CountingCharacter();
    Sword sword = Items.sword();
    World world = new World();
    character.startAttack(sword, 30f, world);

    assertThrows(IllegalStateException.class, () -> character.startAttack(sword, 90f, world));
    assertEquals(30f, character.getCurrentSwing().orElseThrow().getAimDegrees());
  }

  @Test
  void should_throw_when_the_swung_weapon_is_null() {
    CountingCharacter character = new CountingCharacter();
    World world = new World();

    assertThrows(NullPointerException.class, () -> character.startAttack(null, 0f, world));
  }

  @Test
  void should_refuse_a_swing_that_belongs_to_another_character() {
    CountingCharacter character = new CountingCharacter();
    Swing othersSwing =
        new Swing(Items.sword(), aCharacter().build(), 0f, List::of, new EventBus());

    assertThrows(IllegalArgumentException.class, () -> character.holdSwing(othersSwing));
  }

  @Test
  void should_throw_when_the_held_swing_is_null() {
    CountingCharacter character = new CountingCharacter();

    assertThrows(NullPointerException.class, () -> character.holdSwing(null));
  }

  @Test
  void should_throw_when_the_stat_source_is_null() {
    assertThrows(NullPointerException.class, () -> new CountingCharacter(null));
  }

  @Test
  void should_start_with_its_base_attack() {
    Sword sword = Items.sword();

    Character character = new CountingCharacter(stat -> 1f, sword);

    assertSame(sword, character.getBuild().attack());
  }

  @Test
  void should_read_its_stats_from_its_build() {
    StatSource stats = stat -> stat == Stat.ATTACK ? 7f : 1f;

    Character character = new CountingCharacter(stats, Items.sword());

    assertSame(stats, character.getBuild().stats());
    assertEquals(7, character.getAttack());
  }

  @Test
  void should_throw_when_the_base_attack_is_null() {
    StatSource stats = stat -> 1f;

    assertThrows(NullPointerException.class, () -> new CountingCharacter(stats, null));
  }

  @Test
  void should_tick_its_status_effects_once_per_update() {
    Character character = aCharacter().build();
    CountingEffect effect = new CountingEffect(5, aCharacter().build());
    character.applyStatus(effect);

    character.update(1);
    character.update(2);

    assertEquals(2, effect.steps);
  }

  @Test
  void should_ignore_a_status_effect_applied_when_dead() {
    Character character = aCharacter().withHp(5).build();
    character.takeDamage(5);
    CountingEffect effect = new CountingEffect(5, aCharacter().build());

    character.applyStatus(effect);
    character.update(1);

    assertEquals(0, effect.steps);
  }

  @Test
  void should_stop_its_status_effects_when_it_dies() {
    Character character = aCharacter().withHp(10).build();
    CountingEffect effect = new CountingEffect(5, aCharacter().build());
    character.applyStatus(new Burn(new BurnSpec(10, 1, 5), aCharacter().build()));
    character.applyStatus(effect);

    character.update(1);
    character.update(2);

    assertTrue(character.isDead());
    assertEquals(1, effect.steps);
  }

  @Test
  void should_throw_when_the_status_effect_is_null() {
    Character character = aCharacter().build();

    assertThrows(NullPointerException.class, () -> character.applyStatus(null));
  }

  @Test
  void should_lose_hp_when_it_takes_status_damage() {
    Character character = aCharacter().withHp(10).build();

    character.takeStatusDamage(3, aCharacter().build());

    assertEquals(7, character.getHp());
  }

  @Test
  void should_stop_at_0_hp_when_the_status_damage_is_higher_than_the_hp() {
    Character character = aCharacter().withHp(5).build();

    character.takeStatusDamage(50, aCharacter().build());

    assertEquals(0, character.getHp());
    assertTrue(character.isDead());
  }

  @Test
  void should_take_status_damage_when_invincible() {
    Character character = aCharacter().withHp(10).build();
    character.grantInvincibility(100);

    character.takeStatusDamage(3, aCharacter().build());

    assertEquals(7, character.getHp());
  }

  @Test
  void should_not_become_invincible_when_it_takes_status_damage() {
    Character character = aCharacter().withHp(10).withInvincibilityAfterHit(30).build();

    character.takeStatusDamage(3, aCharacter().build());

    assertFalse(character.isInvincible());
  }

  @Test
  void should_take_a_hit_right_after_status_damage() {
    Character character = aCharacter().withHp(10).withInvincibilityAfterHit(30).build();
    character.takeStatusDamage(3, aCharacter().build());

    character.takeDamage(2);

    assertEquals(5, character.getHp());
  }

  @Test
  void should_publish_the_status_damage_with_its_source() {
    World world = new World();
    Character character = aCharacter().withHp(10).withWorld(world).build();
    Character source = aCharacter().build();
    List<GameEvent> events = new ArrayList<>();
    world.getEventBus().subscribeAll(events::add);

    character.takeStatusDamage(3, source);
    world.getEventBus().flush();

    assertEquals(List.of(new DamageDealt(source, character, 3)), events);
  }

  @Test
  void should_publish_its_death_with_the_source_as_killer_when_status_damage_kills_it() {
    World world = new World();
    Character character = aCharacter().withHp(3).withWorld(world).build();
    Character source = aCharacter().build();
    List<GameEvent> events = new ArrayList<>();
    world.getEventBus().subscribe(EntityDied.class, events::add);

    character.takeStatusDamage(3, source);
    world.getEventBus().flush();

    assertEquals(List.of(new EntityDied(character, Optional.of(source))), events);
  }

  @Test
  void should_publish_its_death_once_when_two_burns_tick_on_the_killing_update() {
    World world = new World();
    Character character = aCharacter().withHp(2).withWorld(world).build();
    Character source = aCharacter().build();
    character.applyStatus(new Burn(new BurnSpec(2, 1, 5), source));
    character.applyStatus(new Burn(new BurnSpec(2, 1, 5), source));
    List<GameEvent> deaths = new ArrayList<>();
    world.getEventBus().subscribe(EntityDied.class, deaths::add);

    character.update(1);
    world.getEventBus().flush();

    assertEquals(1, deaths.size());
  }

  @Test
  void should_take_no_status_damage_and_publish_nothing_when_dead() {
    World world = new World();
    Character character = aCharacter().withHp(5).withWorld(world).build();
    character.takeDamage(5);
    List<GameEvent> events = new ArrayList<>();
    world.getEventBus().subscribeAll(events::add);

    character.takeStatusDamage(3, aCharacter().build());
    world.getEventBus().flush();

    assertEquals(0, character.getHp());
    assertTrue(events.isEmpty());
  }

  @Test
  void should_throw_when_the_status_damage_is_below_1() {
    Character character = aCharacter().build();
    Character source = aCharacter().build();

    assertThrows(IllegalArgumentException.class, () -> character.takeStatusDamage(0, source));
  }

  @Test
  void should_throw_when_the_status_damage_source_is_null() {
    Character character = aCharacter().build();

    assertThrows(NullPointerException.class, () -> character.takeStatusDamage(3, null));
  }

  @Test
  void should_take_the_attack_of_the_last_weapon_when_rebuilt() {
    Sword first = Items.sword();
    Sword last = Items.sword();
    Character character = new CountingCharacter(stat -> 1f, Items.sword());

    character.rebuild(List.of(first, last));

    assertSame(last, character.getBuild().attack());
  }

  @Test
  void should_rebuild_from_its_base_each_time() {
    Character character = new CountingCharacter(stat -> 1f, Items.sword());
    BuildModifier faster = attackSpeedTimes(1.5f);

    character.rebuild(List.of(faster));
    character.rebuild(List.of(faster));

    assertEquals(1.5f, character.getAttackSpeed());
  }

  @Test
  void should_go_back_to_its_base_build_when_rebuilt_with_no_modifier() {
    Sword base = Items.sword();
    Character character = new CountingCharacter(stat -> 1f, base);
    character.rebuild(List.of(attackSpeedTimes(1.5f)));

    character.rebuild(List.of());

    assertSame(base, character.getBuild().attack());
    assertEquals(1f, character.getAttackSpeed());
  }

  @Test
  void should_throw_when_the_modifiers_are_null() {
    Character character = new CountingCharacter(stat -> 1f, Items.sword());

    assertThrows(NullPointerException.class, () -> character.rebuild(null));
  }

  @Test
  void should_cut_its_hp_down_to_the_new_max_hp_when_rebuilt() {
    Character character = new CountingCharacter(stat -> 10f, Items.sword());
    BuildModifier lowerMaxHp = build -> build.withStats(stat -> stat == Stat.MAX_HP ? 5f : 10f);

    character.rebuild(List.of(lowerMaxHp));

    assertEquals(5, character.getHp());
  }

  @Test
  void should_keep_its_hp_when_the_new_max_hp_is_higher() {
    Character character = new CountingCharacter(stat -> 10f, Items.sword());
    BuildModifier higherMaxHp = build -> build.withStats(stat -> stat == Stat.MAX_HP ? 20f : 10f);

    character.rebuild(List.of(higherMaxHp));

    assertEquals(10, character.getHp());
  }

  @Test
  void should_deal_the_formula_damage_when_it_hits() {
    Character attacker = aCharacter().withAttack(12).build();
    Character target = aCharacter().withHp(20).withDefense(3).build();

    HitResult result = attacker.hit(target);

    assertEquals(4, result.damage());
    assertFalse(result.killed());
    assertEquals(16, target.getHp());
  }

  @Test
  void should_tell_when_its_hit_kills_the_target() {
    Character attacker = aCharacter().withAttack(12).build();
    Character target = aCharacter().withHp(4).withDefense(3).build();

    assertTrue(attacker.hit(target).killed());
    assertTrue(target.isDead());
  }

  @Test
  void should_deal_no_damage_when_the_target_is_invincible() {
    Character attacker = aCharacter().withAttack(12).build();
    Character target = aCharacter().withHp(20).build();
    target.grantInvincibility(30);

    assertEquals(HitResult.NONE, attacker.hit(target));
    assertEquals(20, target.getHp());
  }

  @Test
  void should_deal_no_damage_when_the_target_is_dead() {
    Character attacker = aCharacter().build();
    Character target = aCharacter().withHp(1).build();
    target.takeDamage(1);

    assertEquals(HitResult.NONE, attacker.hit(target));
  }

  @Test
  void should_throw_when_the_hit_target_is_null() {
    Character attacker = aCharacter().build();

    assertThrows(NullPointerException.class, () -> attacker.hit(null));
  }

  @Test
  void should_hit_through_the_attack_of_its_build() {
    Character attacker =
        new CountingCharacter(stat -> stat == Stat.ATTACK ? 12f : 1f, Items.sword());
    attacker.rebuild(List.of(aFireRing()));
    Character target = aCharacter().withHp(20).withDefense(3).build();

    attacker.hit(target);
    target.update(1);

    assertEquals(14, target.getHp());
  }

  private static FireRing aFireRing() {
    return new FireRing(
        "fire_ring", "item.fire_ring.name", "items/fire_ring", 0, new BurnSpec(2, 1, 3));
  }

  private static BuildModifier attackSpeedTimes(float factor) {
    return build -> {
      StatSource base = build.stats();
      return build.withStats(
          stat -> stat == Stat.ATTACK_SPEED ? base.get(stat) * factor : base.get(stat));
    };
  }

  /** Hand-written fake (Q117): counts the calls to its step. */
  private static final class CountingEffect extends StatusEffect {
    private int steps;

    CountingEffect(int durationTicks, Character source) {
      super(durationTicks, source);
    }

    @Override
    protected void onTick(Character target) {
      steps++;
    }
  }

  /** Hand-written fake (Q117): counts the calls to think(). */
  private static final class CountingCharacter extends Character {
    private int thinks;
    private long lastTick;

    CountingCharacter() {
      this(stat -> stat == Stat.MAX_HP ? 10f : 1f);
    }

    CountingCharacter(StatSource stats) {
      this(stats, new AroundContactAttack(0f));
    }

    CountingCharacter(StatSource stats, Attack baseAttack) {
      super(new Aabb(0, 0, 16, 16), stats, baseAttack, new Aabb(0, 0, 16, 16), 0, new World());
    }

    @Override
    protected void think(long tick) {
      thinks++;
      lastTick = tick;
    }

    @Override
    public Faction getFaction() {
      return Faction.ENEMY;
    }
  }
}
