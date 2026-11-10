package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.entity.Character;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1, 1.6, Q37: a weapon adds an attack bonus and hits within its range. */
class WeaponTest {

  @Test
  void should_keep_its_values_when_created() {
    Weapon weapon = aWeapon(Rarity.RARE, 4, 24f);

    assertEquals("sword_iron", weapon.id());
    assertEquals(Rarity.RARE, weapon.rarity());
    assertEquals(4, weapon.attackBonus());
    assertEquals(24f, weapon.range());
  }

  @Test
  void should_be_an_attack_strategy() {
    Weapon weapon = aWeapon(Rarity.RARE, 4, 24f);

    assertInstanceOf(Attack.class, weapon);
  }

  @Test
  void should_accept_an_attack_bonus_of_0() {
    Weapon weapon = aWeapon(Rarity.COMMON, 0, 16f);

    assertEquals(0, weapon.attackBonus());
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

  private static Weapon aWeapon(Rarity rarity, int attackBonus, float range) {
    return new FakeWeapon(
        "sword_iron", "item.sword_iron.name", "sword", rarity, 30, attackBonus, range);
  }

  /** Hand-written fake (Q117): a weapon whose hit is not used by these tests. */
  private static final class FakeWeapon extends Weapon {
    FakeWeapon(
        String id,
        String nameKey,
        String spriteId,
        Rarity rarity,
        int price,
        int attackBonus,
        float range) {
      super(id, nameKey, spriteId, rarity, price, attackBonus, range);
    }

    @Override
    public int hit(Character attacker, Character target) {
      throw new UnsupportedOperationException("Not used by the constructor tests");
    }
  }
}
