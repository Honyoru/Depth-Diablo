package com.epitech.depthdiablo.item;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.combat.Obb;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Character;
import org.junit.jupiter.api.Test;

/** GDD 1.6: a melee weapon sweeps a blade of a given size over an arc during a few ticks. */
class MeleeWeaponTest {
  private static final float DELTA = 0.001f;

  @Test
  void should_keep_its_blade_values_when_created() {
    MeleeWeapon weapon = aMeleeWeapon(8f, 90f, 12);

    assertEquals(24f, weapon.getRange());
    assertEquals(8f, weapon.getWidth());
    assertEquals(90f, weapon.getArcDegrees());
    assertEquals(12, weapon.getSwingTicks());
  }

  @Test
  void should_accept_an_arc_of_0_for_a_straight_thrust() {
    assertEquals(0f, aMeleeWeapon(8f, 0f, 12).getArcDegrees());
  }

  @Test
  void should_accept_an_arc_of_360_for_a_full_spin() {
    assertEquals(360f, aMeleeWeapon(8f, 360f, 12).getArcDegrees());
  }

  @Test
  void should_accept_a_swing_of_1_tick() {
    assertEquals(1, aMeleeWeapon(8f, 90f, 1).getSwingTicks());
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

  @Test
  void should_start_a_swing_towards_the_aim() {
    Character holder = aCharacter().build();

    aMeleeWeapon(8f, 90f, 12).attack(holder, 30f, new World());

    assertEquals(30f, holder.getCurrentSwing().orElseThrow().getAimDegrees());
  }

  @Test
  void should_stop_the_holder_from_attacking_again_at_once() {
    Character holder = aCharacter().build();

    aMeleeWeapon(8f, 90f, 12).attack(holder, 0f, new World());

    assertFalse(holder.canAttack());
  }

  @Test
  void should_let_the_holder_attack_again_after_the_swing_and_the_cooldown() {
    Character holder = aCharacter().withAttackSpeed(1f).build();
    aMeleeWeapon(8f, 90f, 12).attack(holder, 0f, new World());

    for (long tick = 1; tick <= 71; tick++) {
      holder.update(tick);
    }
    assertFalse(holder.canAttack());

    holder.update(72); // 12 swing ticks + 60 cooldown ticks
    assertTrue(holder.canAttack());
  }

  @Test
  void should_throw_when_the_holder_cannot_attack_now() {
    Character holder = aCharacter().build();
    MeleeWeapon weapon = aMeleeWeapon(8f, 90f, 12);
    weapon.attack(holder, 0f, new World());

    assertThrows(IllegalStateException.class, () -> weapon.attack(holder, 0f, new World()));
  }

  @Test
  void should_keep_the_holder_ready_when_the_aim_is_wrong() {
    Character holder = aCharacter().build();
    MeleeWeapon weapon = aMeleeWeapon(8f, 90f, 12);

    assertThrows(
        IllegalArgumentException.class, () -> weapon.attack(holder, Float.NaN, new World()));
    assertTrue(holder.canAttack());
  }

  @Test
  void should_throw_when_the_holder_is_null() {
    MeleeWeapon weapon = aMeleeWeapon(8f, 90f, 12);

    assertThrows(NullPointerException.class, () -> weapon.attack(null, 0f, new World()));
  }

  @Test
  void should_throw_when_the_world_is_null() {
    Character holder = aCharacter().build();
    MeleeWeapon weapon = aMeleeWeapon(8f, 90f, 12);

    assertThrows(NullPointerException.class, () -> weapon.attack(holder, 0f, null));
    assertTrue(holder.canAttack());
  }

  private static MeleeWeapon aMeleeWeapon(float width, float arcDegrees, int swingTicks) {
    return new Sword(
        "sword",
        "item.sword.name",
        "items/sword",
        Rarity.COMMON,
        30,
        2,
        1f,
        24f,
        width,
        arcDegrees,
        swingTicks);
  }
}
