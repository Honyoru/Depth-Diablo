package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aRat;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aZombie;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.combat.Faction;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** What every enemy shares, checked on real rats and zombies (GDD 1.3.1, 1.7). */
class EnemyTest {

  @Test
  void should_start_idle_on_the_enemy_side_with_no_target() {
    Enemy rat = aRat().build();

    assertEquals(EnemyState.IDLE, rat.getState());
    assertEquals(Faction.ENEMY, rat.getFaction());
    assertEquals(Optional.empty(), rat.getTarget());
  }

  @Test
  void should_see_a_living_player() {
    Enemy rat = aRat().build();

    assertTrue(rat.canSee(aPlayer()));
  }

  @Test
  void should_not_see_a_dead_player() {
    Enemy rat = aRat().build();
    Character player = aPlayer();
    player.takeDamage(player.getHp());

    assertFalse(rat.canSee(player));
  }

  @Test
  void should_never_see_another_enemy() {
    Enemy rat = aRat().build();

    assertFalse(rat.canSee(aZombie().build()));
  }

  @Test
  void should_take_the_player_as_its_target_when_alerted() {
    Enemy zombie = aZombie().build();
    Character player = aPlayer();

    zombie.alert(player);

    assertEquals(Optional.of(player), zombie.getTarget());
  }

  @Test
  void should_ignore_an_alert_about_its_own_side() {
    Enemy rat = aRat().build();

    rat.alert(aZombie().build());

    assertEquals(Optional.empty(), rat.getTarget());
  }

  @Test
  void should_ignore_an_alert_about_a_dead_player() {
    Enemy rat = aRat().build();
    Character player = aPlayer();
    player.takeDamage(player.getHp());

    rat.alert(player);

    assertEquals(Optional.empty(), rat.getTarget());
  }

  @Test
  void should_remember_whether_it_was_summoned() {
    assertTrue(aRat().summoned().build().isSummoned());
    assertFalse(aRat().build().isSummoned());
  }

  @Test
  void should_reject_a_missing_target() {
    Enemy rat = aRat().build();

    assertThrows(NullPointerException.class, () -> rat.canSee(null));
    assertThrows(NullPointerException.class, () -> rat.alert(null));
  }

  private static Character aPlayer() {
    return aCharacter().withFaction(Faction.PLAYER).build();
  }
}
