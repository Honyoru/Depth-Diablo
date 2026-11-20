package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aRat;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aZombie;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.engine.World;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** What every enemy shares, checked on real rats and zombies (GDD 1.3.1, 1.7). */
class EnemyTest {

  @Test
  void should_get_back_its_ai_state_and_target_when_its_state_is_read_back() {
    World world = new World();
    MeleeEnemy rat = aRat().withWorld(world).build();
    Character player = aPlayer();
    world.add(rat);
    world.add(player);
    StateBuffer photo = new StateBuffer();
    rat.writeState(photo);
    rat.alert(player);
    world.tick(); // IDLE sees the target and switches to CHASE

    photo.startReading();
    rat.readState(photo);

    assertEquals(EnemyState.IDLE, rat.getState());
    assertEquals(Optional.empty(), rat.getTarget());
  }

  @Test
  void should_take_the_ai_state_and_target_of_the_photo_before_its_first_update() {
    World world = new World();
    MeleeEnemy chasing = aRat().withWorld(world).build();
    Character player = aPlayer();
    world.add(chasing);
    world.add(player);
    chasing.alert(player);
    world.tick();
    StateBuffer photo = new StateBuffer();
    chasing.writeState(photo);
    MeleeEnemy fresh = aRat().build(); // never updated: it has no AI yet

    photo.startReading();
    fresh.readState(photo);

    assertEquals(EnemyState.CHASE, fresh.getState());
    assertSame(player, fresh.getTarget().orElseThrow());
  }

  private static final float DELTA = 0.001f;

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
  void should_carry_the_sprite_id_of_its_setup() {
    assertEquals("enemies/rat", aRat().build().getSpriteId());
    assertEquals("enemies/zombie", aZombie().build().getSpriteId());
  }

  @Test
  void should_reject_a_missing_target() {
    Enemy rat = aRat().build();

    assertThrows(NullPointerException.class, () -> rat.canSee(null));
    assertThrows(NullPointerException.class, () -> rat.alert(null));
  }

  @Test
  void should_aim_right_at_a_target_on_its_right() {
    Enemy rat = aRat().build();

    assertEquals(0f, rat.aimAt(aTargetAt(40f, 0f)), DELTA);
  }

  @Test
  void should_aim_up_at_a_target_above() {
    Enemy rat = aRat().build();

    assertEquals(90f, rat.aimAt(aTargetAt(0f, 40f)), DELTA);
  }

  @Test
  void should_aim_at_the_diagonal_of_a_target_up_and_right() {
    Enemy rat = aRat().build();

    assertEquals(45f, rat.aimAt(aTargetAt(40f, 40f)), DELTA);
  }

  @Test
  void should_aim_left_at_a_target_on_its_left() {
    Enemy rat = aRat().build();

    assertEquals(180f, rat.aimAt(aTargetAt(-40f, 0f)), DELTA);
  }

  @Test
  void should_throw_when_the_aimed_target_is_null() {
    Enemy rat = aRat().build();

    assertThrows(NullPointerException.class, () -> rat.aimAt(null));
  }

  private static Character aTargetAt(float x, float y) {
    return aCharacter().withBodyBox(new Aabb(x, y, 16f, 16f)).build();
  }

  private static Character aPlayer() {
    return aCharacter().withFaction(Faction.PLAYER).build();
  }
}
