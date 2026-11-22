package com.epitech.depthdiablo.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** The maps of an Endless run (Q77): Floor1 to Floor206, then back to Floor1. */
class FloorSequenceTest {

  @Test
  void should_play_the_first_floor_on_the_first_map() {
    assertEquals("Floor1.tmx", FloorSequence.mapOf(1));
  }

  @Test
  void should_go_from_the_hand_made_floors_to_the_generated_ones() {
    assertEquals("Floor6.tmx", FloorSequence.mapOf(6));
    assertEquals("Floor7.tmx", FloorSequence.mapOf(7));
  }

  @Test
  void should_play_the_last_floor_on_the_last_map() {
    assertEquals("Floor206.tmx", FloorSequence.mapOf(206));
  }

  @Test
  void should_count_206_maps() {
    assertEquals(206, FloorSequence.MAP_COUNT);
  }

  @Test
  void should_loop_back_to_the_first_map_after_the_last_one() {
    assertEquals("Floor1.tmx", FloorSequence.mapOf(207));
    assertEquals("Floor1.tmx", FloorSequence.mapOf(413));
  }

  @Test
  void should_keep_the_order_on_the_second_loop() {
    assertEquals("Floor3.tmx", FloorSequence.mapOf(209));
  }

  @Test
  void should_reject_a_floor_below_one() {
    assertThrows(IllegalArgumentException.class, () -> FloorSequence.mapOf(0));
  }
}
