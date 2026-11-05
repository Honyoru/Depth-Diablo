package com.epitech.depthdiablo.rules;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.combat.DamageFormula;
import com.epitech.depthdiablo.combat.Hitbox;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

/**
 * GDD 1.4.1, "Combat and stats", the rules the Arena slice needs. Still to add when their classes
 * land: floor multiplier (EnemyFactory), weapon range (MeleeWeapon), status effects (Burn, Poison),
 * dodge roll (Player) and summoned enemies dropping no loot (Enemy, LootTable).
 */
class CombatRulesTest {

  @Property
  void should_deal_at_least_1_damage_when_any_stats_are_used(
      @ForAll @IntRange(min = -100, max = 1000) int attack,
      @ForAll @IntRange(min = -100, max = 1000) int bonus,
      @ForAll @DoubleRange(min = 0.0, max = 10.0) double buff,
      @ForAll @IntRange(min = 1, max = 1000) int resistance) {

    int damage = DamageFormula.compute(attack, bonus, buff, resistance);

    assertTrue(damage >= 1);
  }

  @Test
  void should_apply_the_gdd_formula_when_the_result_is_above_1() {
    int attack = 10;
    int bonus = 2;
    double buff = 1.5;
    int resistance = 3;

    int damage = DamageFormula.compute(attack, bonus, buff, resistance);

    assertEquals(6, damage);
  }

  @Test
  void should_reject_a_resistance_of_0() {
    int resistance = 0;

    assertThrows(
        IllegalArgumentException.class, () -> DamageFormula.compute(5, 0, 1.0, resistance));
  }

  @Test
  void should_hit_a_target_when_the_hitbox_overlaps_its_hurtbox() {
    Character attacker = aCharacter().build();
    Character target = aCharacter().withHurtbox(new Aabb(10, 0, 16, 16)).build();
    Hitbox hitbox = new Hitbox(new Aabb(0, 0, 12, 12), attacker);

    boolean hit = hitbox.canHit(target);

    assertTrue(hit);
  }

  @Test
  void should_not_hit_a_target_when_the_hitbox_misses_its_hurtbox() {
    Character attacker = aCharacter().build();
    Character target = aCharacter().withHurtbox(new Aabb(40, 0, 16, 16)).build();
    Hitbox hitbox = new Hitbox(new Aabb(0, 0, 12, 12), attacker);

    boolean hit = hitbox.canHit(target);

    assertFalse(hit);
  }

  @Test
  void should_not_hit_its_owner_when_the_hitbox_overlaps_the_owner() {
    Character attacker = aCharacter().withHurtbox(new Aabb(0, 0, 16, 16)).build();
    Hitbox hitbox = new Hitbox(new Aabb(0, 0, 12, 12), attacker);

    boolean hit = hitbox.canHit(attacker);

    assertFalse(hit);
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
}
