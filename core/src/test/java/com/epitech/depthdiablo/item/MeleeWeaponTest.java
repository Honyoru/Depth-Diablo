package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** GDD 1.6: a melee weapon sweeps a blade of a given size over an arc during a few ticks. */
class MeleeWeaponTest {

  @Test
  void should_keep_its_blade_values_when_created() {
    MeleeWeapon weapon = aMeleeWeapon(8f, 90f, 12);

    assertEquals(24f, weapon.range());
    assertEquals(8f, weapon.width());
    assertEquals(90f, weapon.arcDegrees());
    assertEquals(12, weapon.swingTicks());
  }

  @Test
  void should_accept_an_arc_of_0_for_a_straight_thrust() {
    assertEquals(0f, aMeleeWeapon(8f, 0f, 12).arcDegrees());
  }

  @Test
  void should_accept_an_arc_of_360_for_a_full_spin() {
    assertEquals(360f, aMeleeWeapon(8f, 360f, 12).arcDegrees());
  }

  @Test
  void should_accept_a_swing_of_1_tick() {
    assertEquals(1, aMeleeWeapon(8f, 90f, 1).swingTicks());
  }

  @Test
  void should_throw_when_the_width_is_0() {
    assertThrows(IllegalArgumentException.class, () -> aMeleeWeapon(0f, 90f, 12));
  }

  @Test
  void should_throw_when_the_width_is_not_a_number() {
    assertThrows(IllegalArgumentException.class, () -> aMeleeWeapon(Float.NaN, 90f, 12));
  }

  @Test
  void should_throw_when_the_width_is_infinite() {
    assertThrows(
        IllegalArgumentException.class, () -> aMeleeWeapon(Float.POSITIVE_INFINITY, 90f, 12));
  }

  @Test
  void should_throw_when_the_arc_is_negative() {
    assertThrows(IllegalArgumentException.class, () -> aMeleeWeapon(8f, -1f, 12));
  }

  @Test
  void should_throw_when_the_arc_is_above_360() {
    assertThrows(IllegalArgumentException.class, () -> aMeleeWeapon(8f, 361f, 12));
  }

  @Test
  void should_throw_when_the_arc_is_not_a_number() {
    assertThrows(IllegalArgumentException.class, () -> aMeleeWeapon(8f, Float.NaN, 12));
  }

  @Test
  void should_throw_when_the_swing_lasts_0_ticks() {
    assertThrows(IllegalArgumentException.class, () -> aMeleeWeapon(8f, 90f, 0));
  }

  private static MeleeWeapon aMeleeWeapon(float width, float arcDegrees, int swingTicks) {
    return new FakeMeleeWeapon(width, arcDegrees, swingTicks);
  }

  /** Hand-written fake (Q117): a melee weapon with only the shared MeleeWeapon behaviour. */
  private static final class FakeMeleeWeapon extends MeleeWeapon {
    FakeMeleeWeapon(float width, float arcDegrees, int swingTicks) {
      super(
          "sword_iron",
          "item.sword_iron.name",
          "sword",
          Rarity.COMMON,
          30,
          2,
          24f,
          width,
          arcDegrees,
          swingTicks);
    }
  }
}
