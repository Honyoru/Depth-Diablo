package com.epitech.depthdiablo.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

/** GDD 1.6, Q26: damage = max(1, floor((attack + bonus) x buff / resistance)). */
class DamageFormulaTest {

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
  void should_round_down_when_the_result_is_not_whole() {
    int attack = 10;
    int resistance = 3;

    int damage = DamageFormula.compute(attack, 0, 1.0, resistance);

    assertEquals(3, damage);
  }

  @Test
  void should_deal_1_damage_when_the_resistance_is_much_higher_than_the_attack() {
    int attack = 1;
    int resistance = 100;

    int damage = DamageFormula.compute(attack, 0, 1.0, resistance);

    assertEquals(1, damage);
  }

  @Test
  void should_deal_1_damage_when_the_buff_is_0() {
    double buff = 0.0;

    int damage = DamageFormula.compute(50, 10, buff, 1);

    assertEquals(1, damage);
  }

  @Property
  void should_deal_at_least_1_damage_when_any_stats_are_used(
      @ForAll @IntRange(min = -100, max = 1000) int attack,
      @ForAll @IntRange(min = -100, max = 1000) int bonus,
      @ForAll @DoubleRange(min = 0.0, max = 10.0) double buff,
      @ForAll @IntRange(min = 1, max = 1000) int resistance) {

    int damage = DamageFormula.compute(attack, bonus, buff, resistance);

    assertTrue(damage >= 1);
  }

  @Property
  void should_never_deal_less_damage_when_the_attack_is_higher(
      @ForAll @IntRange(min = 0, max = 1000) int attack,
      @ForAll @IntRange(min = 1, max = 100) int extra,
      @ForAll @DoubleRange(min = 0.0, max = 10.0) double buff,
      @ForAll @IntRange(min = 1, max = 1000) int resistance) {

    int weaker = DamageFormula.compute(attack, 0, buff, resistance);
    int stronger = DamageFormula.compute(attack + extra, 0, buff, resistance);

    assertTrue(stronger >= weaker);
  }

  @Test
  void should_reject_a_resistance_of_0() {
    int resistance = 0;

    assertThrows(
        IllegalArgumentException.class, () -> DamageFormula.compute(5, 0, 1.0, resistance));
  }

  @Test
  void should_reject_a_negative_resistance() {
    int resistance = -1;

    assertThrows(
        IllegalArgumentException.class, () -> DamageFormula.compute(5, 0, 1.0, resistance));
  }

  @Test
  void should_reject_a_negative_buff() {
    double buff = -0.5;

    assertThrows(IllegalArgumentException.class, () -> DamageFormula.compute(5, 0, buff, 1));
  }

  @Test
  void should_reject_a_buff_that_is_not_a_number() {
    double buff = Double.NaN;

    assertThrows(IllegalArgumentException.class, () -> DamageFormula.compute(5, 0, buff, 1));
  }

  @Test
  void should_reject_an_infinite_buff() {
    double buff = Double.POSITIVE_INFINITY;

    assertThrows(IllegalArgumentException.class, () -> DamageFormula.compute(5, 0, buff, 1));
  }
}
