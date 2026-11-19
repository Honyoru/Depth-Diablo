package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Q23: the "class" of an entry names a kind of any family. */
class CharacterKindTest {

  @Test
  void should_find_an_enemy_kind_by_its_name() {
    assertEquals(EnemyKind.RAT, CharacterKind.of("RAT"));
  }

  @Test
  void should_find_a_player_kind_by_its_name() {
    assertEquals(PlayerKind.KNIGHT, CharacterKind.of("KNIGHT"));
  }

  @Test
  void should_throw_when_no_family_has_the_name() {
    assertThrows(IllegalArgumentException.class, () -> CharacterKind.of("DRAGON"));
  }

  @Test
  void should_throw_when_the_name_is_null() {
    assertThrows(NullPointerException.class, () -> CharacterKind.of(null));
  }
}
