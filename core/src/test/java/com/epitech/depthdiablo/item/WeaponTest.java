package com.epitech.depthdiablo.item;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.combat.HitResult;
import com.epitech.depthdiablo.entity.BaseStats;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.CharacterBuild;
import com.epitech.depthdiablo.entity.Stat;
import com.epitech.depthdiablo.entity.StatSource;
import com.epitech.depthdiablo.testdata.Items;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1, 1.6, Q37: a weapon adds an attack bonus and hits within its range. */
class WeaponTest {

  @Test
  void should_keep_its_values_when_created() {
    Weapon weapon = aWeapon(Rarity.RARE, 4, 24f);

    assertEquals("sword", weapon.getId());
    assertEquals(Rarity.RARE, weapon.getRarity());
    assertEquals(4, weapon.getAttackBonus());
    assertEquals(24f, weapon.getRange());
  }

  @Test
  void should_be_an_attack_strategy() {
    Weapon weapon = aWeapon(Rarity.RARE, 4, 24f);

    assertInstanceOf(Attack.class, weapon);
  }

  @Test
  void should_accept_an_attack_bonus_of_0() {
    Weapon weapon = aWeapon(Rarity.COMMON, 0, 16f);

    assertEquals(0, weapon.getAttackBonus());
  }

  @Test
  void should_throw_when_the_attack_bonus_is_negative() {
    assertThrows(IllegalArgumentException.class, () -> aWeapon(Rarity.RARE, -1, 24f));
  }

  @Test
  void should_throw_when_the_range_is_0() {
    assertThrows(IllegalArgumentException.class, () -> aWeapon(Rarity.RARE, 4, 0f));
  }

  @Test
  void should_throw_when_the_range_is_not_a_number() {
    assertThrows(IllegalArgumentException.class, () -> aWeapon(Rarity.RARE, 4, Float.NaN));
  }

  @Test
  void should_throw_when_the_range_is_infinite() {
    assertThrows(
        IllegalArgumentException.class, () -> aWeapon(Rarity.RARE, 4, Float.POSITIVE_INFINITY));
  }

  @Test
  void should_throw_when_the_rarity_is_consumable() {
    assertThrows(IllegalArgumentException.class, () -> aWeapon(Rarity.CONSUMABLE, 4, 24f));
  }

  @Test
  void should_not_add_its_bonus_again_when_it_hits() {
    // The bonus is already in the holder's ATTACK through modify(); hit() adds nothing on top.
    Character attacker = aCharacter().withAttack(10).build();
    Character target = aCharacter().withHp(20).withDefense(3).build();
    Weapon weapon = aWeapon(Rarity.RARE, 2, 24f);

    HitResult result = weapon.hit(attacker, target);

    assertEquals(3, result.damage());
    assertFalse(result.killed());
    assertEquals(17, target.getHp());
  }

  @Test
  void should_tell_when_its_hit_kills_the_target() {
    Character attacker = aCharacter().withAttack(10).build();
    Character target = aCharacter().withHp(3).withDefense(3).build();
    Weapon weapon = aWeapon(Rarity.RARE, 2, 24f);

    HitResult result = weapon.hit(attacker, target);

    assertEquals(3, result.damage());
    assertTrue(result.killed());
    assertTrue(target.isDead());
  }

  @Test
  void should_deal_no_damage_when_the_target_is_invincible() {
    Character attacker = aCharacter().withAttack(10).build();
    Character target = aCharacter().withHp(20).build();
    target.grantInvincibility(30);
    Weapon weapon = aWeapon(Rarity.RARE, 2, 24f);

    HitResult result = weapon.hit(attacker, target);

    assertEquals(HitResult.NONE, result);
    assertEquals(20, target.getHp());
  }

  @Test
  void should_deal_no_damage_when_the_target_is_dead() {
    Character attacker = aCharacter().build();
    Character target = aCharacter().withHp(1).build();
    target.takeDamage(1);
    Weapon weapon = aWeapon(Rarity.RARE, 2, 24f);

    HitResult result = weapon.hit(attacker, target);

    assertEquals(HitResult.NONE, result);
  }

  @Test
  void should_throw_when_the_attacker_is_null() {
    Weapon weapon = aWeapon(Rarity.RARE, 2, 24f);
    Character target = aCharacter().build();

    assertThrows(NullPointerException.class, () -> weapon.hit(null, target));
  }

  @Test
  void should_throw_when_the_target_is_null() {
    Weapon weapon = aWeapon(Rarity.RARE, 2, 24f);
    Character attacker = aCharacter().build();

    assertThrows(NullPointerException.class, () -> weapon.hit(attacker, null));
  }

  @Test
  void should_keep_its_attack_speed() {
    assertEquals(1.5f, aWeapon(Rarity.RARE, 4, 1.5f, 24f).getAttackSpeed());
  }

  @Test
  void should_throw_when_the_attack_speed_is_0() {
    assertThrows(IllegalArgumentException.class, () -> aWeapon(Rarity.RARE, 4, 0f, 24f));
  }

  @Test
  void should_throw_when_the_attack_speed_is_not_a_number() {
    assertThrows(IllegalArgumentException.class, () -> aWeapon(Rarity.RARE, 4, Float.NaN, 24f));
  }

  @Test
  void should_throw_when_the_attack_speed_is_infinite() {
    assertThrows(
        IllegalArgumentException.class,
        () -> aWeapon(Rarity.RARE, 4, Float.POSITIVE_INFINITY, 24f));
  }

  @Test
  void should_become_the_attack_of_the_build() {
    Weapon weapon = Items.sword();

    CharacterBuild built = weapon.modify(aBuild(5f, 2f));

    assertSame(weapon, built.attack());
  }

  @Test
  void should_multiply_the_attack_speed_of_the_build_by_its_own() {
    Weapon weapon = aWeapon(Rarity.COMMON, 0, 1.5f, 24f);

    CharacterBuild built = weapon.modify(aBuild(5f, 2f));

    assertEquals(3f, built.stats().get(Stat.ATTACK_SPEED));
  }

  @Test
  void should_add_its_attack_bonus_to_the_attack_of_the_build() {
    Weapon weapon = aWeapon(Rarity.COMMON, 4, 1.5f, 24f);

    CharacterBuild built = weapon.modify(aBuild(5f, 2f));

    assertEquals(9f, built.stats().get(Stat.ATTACK));
  }

  @Test
  void should_keep_the_other_stats_of_the_build() {
    Weapon weapon = aWeapon(Rarity.COMMON, 4, 1.5f, 24f);

    CharacterBuild built = weapon.modify(aBuild(5f, 2f));

    assertEquals(1f, built.stats().get(Stat.DEFENSE));
  }

  @Test
  void should_leave_the_given_build_unchanged() {
    CharacterBuild build = aBuild(5f, 2f);
    Attack attackBefore = build.attack();
    StatSource statsBefore = build.stats();

    aWeapon(Rarity.COMMON, 0, 1.5f, 24f).modify(build);

    assertSame(attackBefore, build.attack());
    assertSame(statsBefore, build.stats());
    assertEquals(2f, build.stats().get(Stat.ATTACK_SPEED));
  }

  @Test
  void should_throw_when_the_build_is_null() {
    Weapon weapon = Items.sword();

    assertThrows(NullPointerException.class, () -> weapon.modify(null));
  }

  private static CharacterBuild aBuild(float attack, float attackSpeed) {
    Map<Stat, Float> values = new EnumMap<>(Stat.class);
    for (Stat stat : Stat.values()) {
      values.put(stat, 1f);
    }
    values.put(Stat.ATTACK, attack);
    values.put(Stat.ATTACK_SPEED, attackSpeed);
    return new CharacterBuild(Items.sword(), new BaseStats(values));
  }

  private static Weapon aWeapon(Rarity rarity, int attackBonus, float range) {
    return aWeapon(rarity, attackBonus, 1f, range);
  }

  private static Weapon aWeapon(Rarity rarity, int attackBonus, float attackSpeed, float range) {
    return new Sword(
        "sword",
        "item.sword.name",
        "items/sword",
        rarity,
        30,
        attackBonus,
        attackSpeed,
        range,
        8f,
        90f,
        12);
  }
}
