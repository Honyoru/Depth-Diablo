package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.engine.EventBus;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.item.MeleeWeapon;
import com.epitech.depthdiablo.item.Rarity;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** GDD 1.6: a swing sweeps the blade of a melee weapon around its holder. */
class SwingTest {
  private static final float DELTA = 0.001f;
  private static final Supplier<List<Entity>> NO_TARGETS = List::of;

  /** Centre (100, 50). */
  private final Character holder =
      aCharacter().withBodyBox(new Aabb(92, 42, 16, 16)).withFaction(Faction.PLAYER).build();

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
