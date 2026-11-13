package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.combat.Obb;
import org.junit.jupiter.api.Test;

/** GDD 1.6: a melee weapon sweeps a blade of a given size over an arc during a few ticks. */
class MeleeWeaponTest {
  private static final float DELTA = 0.001f;

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

  @Test
  void should_point_the_blade_at_the_aim_in_the_middle_of_the_swing() {
    Obb blade = aMeleeWeapon(8f, 90f, 12).swingShape(100, 50, 0, 0.5f);

    assertEquals(112f, blade.centreX(), DELTA);
    assertEquals(50f, blade.centreY(), DELTA);
    assertEquals(0f, blade.angleDegrees(), DELTA);
  }

  @Test
  void should_start_the_swing_half_an_arc_before_the_aim() {
    Obb blade = aMeleeWeapon(8f, 90f, 12).swingShape(100, 50, 0, 0f);

    assertEquals(-45f, blade.angleDegrees(), DELTA);
  }

  @Test
  void should_end_the_swing_half_an_arc_after_the_aim() {
    Obb blade = aMeleeWeapon(8f, 90f, 12).swingShape(100, 50, 0, 1f);

    assertEquals(45f, blade.angleDegrees(), DELTA);
  }

  @Test
  void should_put_the_blade_above_the_pivot_when_aiming_up() {
    Obb blade = aMeleeWeapon(8f, 90f, 12).swingShape(100, 50, 90, 0.5f);

    assertEquals(100f, blade.centreX(), DELTA);
    assertEquals(62f, blade.centreY(), DELTA);
  }

  @Test
  void should_give_the_blade_the_weapon_size() {
    Obb blade = aMeleeWeapon(8f, 90f, 12).swingShape(100, 50, 0, 0.5f);

    assertEquals(24f, blade.length());
    assertEquals(8f, blade.width());
  }

  @Test
  void should_throw_when_the_progress_is_negative() {
    MeleeWeapon weapon = aMeleeWeapon(8f, 90f, 12);

    assertThrows(IllegalArgumentException.class, () -> weapon.swingShape(100, 50, 0, -0.1f));
  }

  @Test
  void should_throw_when_the_progress_is_above_1() {
    MeleeWeapon weapon = aMeleeWeapon(8f, 90f, 12);

    assertThrows(IllegalArgumentException.class, () -> weapon.swingShape(100, 50, 0, 1.1f));
  }

  @Test
  void should_throw_when_the_progress_is_not_a_number() {
    MeleeWeapon weapon = aMeleeWeapon(8f, 90f, 12);

    assertThrows(IllegalArgumentException.class, () -> weapon.swingShape(100, 50, 0, Float.NaN));
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
