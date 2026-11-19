package com.epitech.depthdiablo.arena;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

/** Arena mode (GDD 1.1): which enemies each wave holds, the king on every boss wave. */
class WavePlanTest {

  private final WavePlan plan = new WavePlan();

  @Test
  void should_send_6_rats_in_wave_1() {
    assertEquals(Collections.nCopies(6, "rat"), plan.enemiesOf(1, false));
  }

  @Test
  void should_add_one_rat_per_wave() {
    assertEquals(7, Collections.frequency(plan.enemiesOf(2, false), "rat"));
    assertEquals(9, Collections.frequency(plan.enemiesOf(4, false), "rat"));
  }

  @Test
  void should_send_no_zombie_in_wave_1() {
    assertFalse(plan.enemiesOf(1, false).contains("zombie"));
  }

  @Test
  void should_add_one_zombie_per_wave_from_wave_2() {
    assertEquals(1, Collections.frequency(plan.enemiesOf(2, false), "zombie"));
    assertEquals(3, Collections.frequency(plan.enemiesOf(4, false), "zombie"));
  }

  @Test
  void should_start_a_boss_wave_with_the_king() {
    List<String> enemies = plan.enemiesOf(5, true);

    assertEquals(WavePlan.KING_ID, enemies.get(0));
    assertEquals(1, Collections.frequency(enemies, WavePlan.KING_ID));
  }

  @Test
  void should_send_an_escort_of_6_rats_with_the_king() {
    List<String> enemies = plan.enemiesOf(5, true);

    assertEquals(7, enemies.size());
    assertEquals(6, Collections.frequency(enemies, "rat"));
  }

  @Property
  void should_send_no_king_in_a_normal_wave(@ForAll @IntRange(min = 1, max = 50) int wave) {
    assertFalse(plan.enemiesOf(wave, false).contains(WavePlan.KING_ID));
  }

  @Property
  void should_never_send_fewer_enemies_than_the_wave_before(
      @ForAll @IntRange(min = 1, max = 50) int wave) {
    assertTrue(plan.enemiesOf(wave + 1, false).size() >= plan.enemiesOf(wave, false).size());
  }

  @Test
  void should_give_a_list_that_cannot_be_changed() {
    List<String> enemies = plan.enemiesOf(1, false);

    assertThrows(UnsupportedOperationException.class, () -> enemies.add("rat"));
  }

  @Test
  void should_reject_a_wave_below_1() {
    assertThrows(IllegalArgumentException.class, () -> plan.enemiesOf(0, false));
  }
}
