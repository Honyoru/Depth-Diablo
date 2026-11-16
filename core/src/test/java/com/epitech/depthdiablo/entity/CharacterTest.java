package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.combat.Swing;
import com.epitech.depthdiablo.engine.EventBus;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.item.Sword;
import com.epitech.depthdiablo.testdata.Items;
import java.util.List;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1 and 1.5: HP, death, invincibility frames and the update template of a Character. */
class CharacterTest {

  @Test
  void should_start_with_full_hp_when_created() {
    Character character = aCharacter().withHp(12).build();

    int hp = character.hp();

    assertEquals(12, hp);
    assertEquals(12, character.maxHp());
  }

  @Test
  void should_lose_hp_when_it_takes_damage() {
    Character character = aCharacter().withHp(10).build();

    character.takeDamage(3);

    assertEquals(7, character.hp());
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

    assertEquals(0, character.hp());
  }

  @Test
  void should_take_no_more_damage_when_dead() {
    Character character = aCharacter().withHp(5).build();
    character.takeDamage(5);

    character.takeDamage(1);

    assertEquals(0, character.hp());
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

    assertEquals(10, character.hp());
  }

  @Test
  void should_ignore_a_second_hit_when_it_lands_during_the_i_frames() {
    Character character = aCharacter().withHp(10).withInvincibilityAfterHit(30).build();
    character.takeDamage(3);

    character.takeDamage(3);

    assertEquals(7, character.hp());
  }

  @Test
  void should_take_damage_again_when_the_i_frames_are_over() {
    Character character = aCharacter().withHp(10).withInvincibilityAfterHit(2).build();
    character.takeDamage(3);
    character.update(1);
    character.update(2);

    character.takeDamage(3);

    assertEquals(4, character.hp());
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

    Aabb bodyBox = character.bodyBox();

    assertSame(box, bodyBox);
  }

  @Test
  void should_return_the_hurtbox_it_was_built_with() {
    Aabb box = new Aabb(4, 8, 16, 16);
    Character character = aCharacter().withHurtbox(box).build();

    Aabb hurtbox = character.hurtbox();

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

    assertThrows(
        IllegalArgumentException.class,
        () -> aCharacter().withInvincibilityAfterHit(ticks).build());
  }

  @Test
  void should_reject_a_missing_body_box() {
    assertThrows(NullPointerException.class, () -> aCharacter().withBodyBox(null).build());
  }

  @Test
  void should_reject_a_missing_hurtbox() {
    assertThrows(NullPointerException.class, () -> aCharacter().withHurtbox(null).build());
  }

  @Test
  void should_gain_hp_when_it_heals() {
    Character character = aCharacter().withHp(10).build();
    character.takeDamage(6);

    character.heal(4);

    assertEquals(8, character.hp());
  }

  @Test
  void should_stop_at_max_hp_when_the_heal_is_higher_than_the_missing_hp() {
    Character character = aCharacter().withHp(10).build();
    character.takeDamage(2);

    character.heal(50);

    assertEquals(10, character.hp());
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

    assertEquals(7, character.attack());
    assertEquals(3, character.defense());
  }

  @Test
  void should_accept_an_attack_of_0() {
    Character character = aCharacter().withAttack(0).build();

    assertEquals(0, character.attack());
  }

  @Test
  void should_keep_its_speed_and_attack_speed_when_created() {
    Character character = aCharacter().withSpeed(120f).withAttackSpeed(1.5f).build();

    assertEquals(120f, character.speed());
    assertEquals(1.5f, character.attackSpeed());
  }

  @Test
  void should_accept_a_speed_and_attack_speed_of_0() {
    Character character = aCharacter().withSpeed(0f).withAttackSpeed(0f).build();

    assertEquals(0f, character.speed());
    assertEquals(0f, character.attackSpeed());
  }

  @Test
  void should_round_down_its_int_stats_when_the_source_gives_a_fraction() {
    StatSource stats = stat -> 3.7f;

    Character character = new CountingCharacter(stats);

    assertEquals(3, character.maxHp());
    assertEquals(3, character.attack());
    assertEquals(3, character.defense());
  }

  @Test
  void should_be_able_to_attack_when_created() {
    assertTrue(aCharacter().build().canAttack());
  }

  @Test
  void should_not_attack_again_during_its_cooldown() {
    Character character = aCharacter().withAttackSpeed(1f).build();

    character.startAttackCooldown(0, 1f);

    assertFalse(character.canAttack());
  }

  @Test
  void should_attack_again_after_60_ticks_at_one_attack_per_second() {
    Character character = aCharacter().withAttackSpeed(1f).build();
    character.startAttackCooldown(0, 1f);

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
    character.startAttackCooldown(0, 1f);

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
    character.startAttackCooldown(0, 1f);

    assertThrows(IllegalStateException.class, () -> character.startAttackCooldown(0, 1f));
  }

  @Test
  void should_not_attack_again_during_its_swing() {
    Character character = aCharacter().withAttackSpeed(1f).build();
    character.startAttackCooldown(12, 1f);

    for (long tick = 1; tick <= 11; tick++) {
      character.update(tick);
    }

    assertFalse(character.canAttack());
  }

  @Test
  void should_count_the_cooldown_only_once_the_swing_is_over() {
    Character character = aCharacter().withAttackSpeed(1f).build();
    character.startAttackCooldown(12, 1f);

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

    assertThrows(IllegalArgumentException.class, () -> character.startAttackCooldown(-1, 1f));
  }

  @Test
  void should_multiply_the_weapon_attack_speed_by_its_own() {
    Character character = aCharacter().withAttackSpeed(2f).build();
    character.startAttackCooldown(0, 1f); // 1 x 2 = 2 attacks per second: 30 ticks

    for (long tick = 1; tick <= 29; tick++) {
      character.update(tick);
    }
    assertFalse(character.canAttack());

    character.update(30);
    assertTrue(character.canAttack());
  }

  @Test
  void should_throw_when_the_weapon_attack_speed_is_0() {
    Character character = aCharacter().build();

    assertThrows(IllegalArgumentException.class, () -> character.startAttackCooldown(0, 0f));
  }

  @Test
  void should_throw_when_the_weapon_attack_speed_is_not_a_number() {
    Character character = aCharacter().build();

    assertThrows(IllegalArgumentException.class, () -> character.startAttackCooldown(0, Float.NaN));
  }

  @Test
  void should_keep_its_cooldown_when_an_attack_is_refused() {
    Character character = aCharacter().withAttackSpeed(1f).build();
    character.startAttackCooldown(0, 1f);
    for (long tick = 1; tick <= 30; tick++) {
      character.update(tick);
    }

    assertThrows(IllegalStateException.class, () -> character.startAttackCooldown(0, 1f));

    for (long tick = 31; tick <= 60; tick++) {
      character.update(tick);
    }
    assertTrue(character.canAttack()); // still ready at tick 60, not at 90
  }

  @Test
  void should_not_swing_when_created() {
    assertTrue(new CountingCharacter().currentSwing().isEmpty());
  }

  @Test
  void should_hold_the_swing_it_starts() {
    CountingCharacter character = new CountingCharacter();

    character.startAttack(Items.sword(), 30f, new World());

    assertEquals(30f, character.currentSwing().orElseThrow().aimDegrees());
  }

  @Test
  void should_run_its_swing_once_per_update() {
    CountingCharacter character = new CountingCharacter();
    character.startAttack(Items.sword(), 0f, new World());
    Swing swing = character.currentSwing().orElseThrow();

    character.update(1);
    character.update(2);

    assertEquals(2, swing.ticksElapsed());
  }

  @Test
  void should_drop_its_swing_once_it_is_over() {
    CountingCharacter character = new CountingCharacter();
    Sword sword = Items.sword();
    character.startAttack(sword, 0f, new World());

    for (long tick = 1; tick <= sword.swingTicks(); tick++) {
      character.update(tick);
    }

    assertTrue(character.currentSwing().isEmpty());
  }

  @Test
  void should_keep_its_swing_when_a_second_one_is_refused() {
    CountingCharacter character = new CountingCharacter();
    Sword sword = Items.sword();
    character.startAttack(sword, 30f, new World());

    assertThrows(IllegalStateException.class, () -> character.startAttack(sword, 90f, new World()));
    assertEquals(30f, character.currentSwing().orElseThrow().aimDegrees());
  }

  @Test
  void should_throw_when_the_swung_weapon_is_null() {
    CountingCharacter character = new CountingCharacter();

    assertThrows(NullPointerException.class, () -> character.startAttack(null, 0f, new World()));
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

  /** Hand-written fake (Q117): counts the calls to think(). */
  private static final class CountingCharacter extends Character {
    private int thinks;
    private long lastTick;

    CountingCharacter() {
      this(stat -> stat == Stat.MAX_HP ? 10f : 1f);
    }

    CountingCharacter(StatSource stats) {
      super(new Aabb(0, 0, 16, 16), stats, new Aabb(0, 0, 16, 16), 0);
    }

    @Override
    protected void think(long tick) {
      thinks++;
      lastTick = tick;
    }

    @Override
    public Faction faction() {
      return Faction.ENEMY;
    }
  }
}
