package com.epitech.depthdiablo.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** GDD 1.6: a hit lands when it deals damage, and it may kill the target. */
class HitResultTest {

  @Test
  void should_land_when_it_deals_damage() {
    assertTrue(new HitResult(3, false).landed());
  }

  @Test
  void should_not_land_when_it_deals_no_damage() {
    assertFalse(HitResult.NONE.landed());
  }

  @Test
  void should_keep_its_damage_and_kill() {
    HitResult result = new HitResult(5, true);

    assertEquals(5, result.damage());
    assertTrue(result.killed());
  }

  @Test
  void should_throw_when_the_damage_is_negative() {
    assertThrows(IllegalArgumentException.class, () -> new HitResult(-1, false));
  }

  @Test
  void should_throw_when_it_kills_without_damage() {
    assertThrows(IllegalArgumentException.class, () -> new HitResult(0, true));
  }
}
