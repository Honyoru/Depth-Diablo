package com.epitech.depthdiablo.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** GDD 1.5: an attack never hurts its own side; a trap hurts everyone. */
class FactionTest {

  @ParameterizedTest(name = "{0} hurts {1}: {2}")
  @CsvSource({
    "PLAYER,  PLAYER,  false",
    "PLAYER,  ENEMY,   true",
    "PLAYER,  NEUTRAL, true",
    "ENEMY,   PLAYER,  true",
    "ENEMY,   ENEMY,   false",
    "ENEMY,   NEUTRAL, true",
    "NEUTRAL, PLAYER,  true",
    "NEUTRAL, ENEMY,   true",
    "NEUTRAL, NEUTRAL, true"
  })
  void should_never_hurt_its_own_side_unless_neutral(
      Faction attacker, Faction target, boolean expected) {
    assertEquals(expected, attacker.canHurt(target));
  }

  @Test
  void should_throw_when_the_target_is_null() {
    NullPointerException error =
        assertThrows(NullPointerException.class, () -> Faction.PLAYER.canHurt(null));

    assertEquals("target is null", error.getMessage());
  }
}
