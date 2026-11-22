package com.epitech.depthdiablo.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/** What an Endless floor holds (Q47, Q17): more enemies, of more kinds, with better stats. */
class FloorPlanTest {
  private static final float TOLERANCE = 0.0001f;

  @Test
  void should_keep_the_base_stats_on_the_first_floor() {
    assertEquals(1f, FloorPlan.statMultiplier(1), TOLERANCE);
  }

  @Test
  void should_add_ten_percent_of_the_base_stats_per_floor() {
    assertEquals(1.1f, FloorPlan.statMultiplier(2), TOLERANCE);
    assertEquals(2f, FloorPlan.statMultiplier(11), TOLERANCE);
    assertEquals(3f, FloorPlan.statMultiplier(21), TOLERANCE);
  }

  @Test
  void should_put_one_rat_per_enemy_tile_on_the_first_floor() {
    assertEquals(Collections.nCopies(10, "rat"), FloorPlan.enemiesOf(1, 10));
  }

  @Test
  void should_add_two_enemies_per_floor() {
    assertEquals(12, FloorPlan.enemiesOf(2, 10).size());
    assertEquals(16, FloorPlan.enemiesOf(4, 10).size());
  }

  @Test
  void should_never_put_more_than_two_enemies_per_tile() {
    assertEquals(20, FloorPlan.enemiesOf(6, 10).size());
    assertEquals(20, FloorPlan.enemiesOf(50, 10).size());
  }

  @Test
  void should_bring_zombies_from_the_second_floor() {
    List<String> enemies = FloorPlan.enemiesOf(2, 10);

    assertEquals(6, Collections.frequency(enemies, "rat"));
    assertEquals(6, Collections.frequency(enemies, "zombie"));
    assertFalse(enemies.contains("skeleton_archer"));
  }

  @Test
  void should_bring_skeleton_archers_from_the_third_floor() {
    List<String> enemies = FloorPlan.enemiesOf(3, 3); // 3 + 4 = 7, capped at 6

    assertEquals(
        List.of("rat", "zombie", "skeleton_archer", "rat", "zombie", "skeleton_archer"), enemies);
    assertFalse(enemies.contains("rat_nest"));
  }

  @Test
  void should_make_every_eighth_enemy_a_rat_nest_from_the_fourth_floor() {
    List<String> enemies = FloorPlan.enemiesOf(4, 10);

    assertEquals(2, Collections.frequency(enemies, "rat_nest"));
    assertEquals("rat_nest", enemies.get(7));
    assertEquals("rat_nest", enemies.get(15));
  }

  @Test
  void should_leave_the_king_out_of_the_enemies() {
    assertFalse(FloorPlan.enemiesOf(10, 30).contains(FloorPlan.BOSS_ID));
    assertEquals("king", FloorPlan.BOSS_ID);
  }

  @Test
  void should_give_the_same_enemies_for_the_same_floor() {
    assertEquals(FloorPlan.enemiesOf(7, 25), FloorPlan.enemiesOf(7, 25));
  }

  @Test
  void should_give_no_enemy_to_a_map_without_enemy_tiles() {
    assertTrue(FloorPlan.enemiesOf(5, 0).isEmpty());
  }

  @Test
  void should_reject_a_floor_below_one() {
    assertThrows(IllegalArgumentException.class, () -> FloorPlan.statMultiplier(0));
    assertThrows(IllegalArgumentException.class, () -> FloorPlan.enemiesOf(0, 10));
  }

  @Test
  void should_reject_a_negative_number_of_enemy_tiles() {
    assertThrows(IllegalArgumentException.class, () -> FloorPlan.enemiesOf(1, -1));
  }
}
