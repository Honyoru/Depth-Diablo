package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.engine.World;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Projectiles (GDD 1.3.1, 1.5): they fly straight and hurt the first enemy they touch. */
class ProjectileTest {
  private static final float TEN_PIXELS_PER_TICK = 10F * GameConstants.TICKS_PER_SECOND;
  private static final int LIFETIME_TICKS = 30;
  private static final String ARROW_SPRITE = "projectiles/arrow";
  private final World world = new World();

  /** Shoots every arrow; on the ENEMY side, like the Skeleton Archer. */
  private final Character owner = aCharacter().withWorld(world).build();

  private Arrow arrowAt(float x, float y, float aimDegrees) {
    Arrow arrow =
        new Arrow(
            new Aabb(x, y, 16, 16),
            owner,
            aimDegrees,
            TEN_PIXELS_PER_TICK,
            LIFETIME_TICKS,
            ARROW_SPRITE,
            world);
    world.add(arrow);
    return arrow;
  }

  private final Character characterAt(float x, float y, Faction side) {
    Aabb box = new Aabb(x, y, 16, 16);
    Character character =
        aCharacter().withBodyBox(box).withHurtbox(box).withFaction(side).withWorld(world).build();
    world.add(character);
    return character;
  }

  @Test
  void should_move_by_its_speed_towards_its_aim_each_tick() {
    Arrow arrow = arrowAt(100, 100, 0);

    world.tick();

    assertEquals(new Vector2(110, 100), arrow.position());
  }

  @Test
  void should_move_up_when_it_is_aimed_at_90_degrees() {
    Arrow arrow = arrowAt(100, 100, 90);

    world.tick();

    assertEquals(100, arrow.position().x, 0.001f);
    assertEquals(110, arrow.position().y, 0.001f);
  }

  @Test
  void should_be_removed_when_a_wall_is_in_its_way() {
    world.add(new Wall(new Aabb(112, 90, 32, 32)));
    Arrow arrow = arrowAt(100, 100, 0);

    world.tick();

    assertTrue(arrow.isRemoved());
  }

  @Test
  void should_hurt_a_character_of_another_side_when_it_touches_it() {
    arrowAt(100, 100, 0);
    Character player = characterAt(115, 96, Faction.PLAYER);

    world.tick();

    assertTrue(player.getHp() < 10);
  }

  @Test
  void should_publish_the_damage_it_deals() {
    List<GameEvent> seen = new ArrayList<>();
    world.getEventBus().subscribeAll(seen::add);
    arrowAt(100, 100, 0);
    characterAt(115, 96, Faction.PLAYER);

    world.tick();

    assertTrue(seen.stream().anyMatch(DamageDealt.class::isInstance));
  }

  @Test
  void should_publish_a_death_when_it_kills() {
    List<GameEvent> seen = new ArrayList<>();
    world.getEventBus().subscribeAll(seen::add);
    arrowAt(100, 100, 0);
    Aabb box = new Aabb(115, 96, 16, 16);
    world.add(
        aCharacter()
            .withBodyBox(box)
            .withHurtbox(box)
            .withFaction(Faction.PLAYER)
            .withHp(1)
            .withWorld(world)
            .build());

    world.tick();

    assertTrue(seen.stream().anyMatch(EntityDied.class::isInstance));
  }

  @Test
  void should_stop_when_it_hurts_a_character() {
    Arrow arrow = arrowAt(100, 100, 0);
    characterAt(115, 96, Faction.PLAYER);

    world.tick();

    assertTrue(arrow.isRemoved());
  }

  @Test
  void should_hurt_only_the_first_character_when_two_stand_in_its_way() {
    arrowAt(100, 100, 0);
    Character first = characterAt(115, 96, Faction.PLAYER);
    Character second = characterAt(115, 96, Faction.PLAYER);

    world.tick();

    assertTrue(first.getHp() < 10);
    assertEquals(10, second.getHp());
  }

  @Test
  void should_fly_through_a_character_of_its_own_side() {
    Arrow arrow = arrowAt(100, 100, 0);
    Character ally = characterAt(115, 96, Faction.ENEMY);

    world.tick();

    assertEquals(10, ally.getHp());
    assertFalse(arrow.isRemoved());
  }

  @Test
  void should_fly_through_a_character_while_it_is_invincible() {
    Arrow arrow = arrowAt(100, 100, 0);
    Aabb box = new Aabb(115, 96, 16, 16);
    Character dodging =
        aCharacter()
            .withBodyBox(box)
            .withHurtbox(box)
            .withFaction(Faction.PLAYER)
            .withInvincibilityAfterHit(60)
            .withWorld(world)
            .build();
    world.add(dodging);
    dodging.takeDamage(1);

    world.tick();

    assertEquals(9, dodging.getHp());
    assertFalse(arrow.isRemoved());
  }

  @Test
  void should_be_removed_when_its_lifetime_ends() {
    Arrow arrow = arrowAt(100, 100, 0);
    for (int tick = 1; tick < LIFETIME_TICKS; tick++) {
      world.tick();
    }
    assertFalse(arrow.isRemoved());

    world.tick();

    assertTrue(arrow.isRemoved());
  }

  @Test
  void should_reject_a_speed_of_zero() {
    Aabb body = new Aabb(0, 0, 8, 4);

    assertThrows(
        IllegalArgumentException.class,
        () -> new Arrow(body, owner, 0, 0f, LIFETIME_TICKS, ARROW_SPRITE, world));
  }

  @Test
  void should_reject_an_aim_that_is_not_a_number() {
    Aabb body = new Aabb(0, 0, 8, 4);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Arrow(
                body, owner, Float.NaN, TEN_PIXELS_PER_TICK, LIFETIME_TICKS, ARROW_SPRITE, world));
  }

  @Test
  void should_reject_a_lifetime_of_zero() {
    Aabb body = new Aabb(0, 0, 8, 4);

    assertThrows(
        IllegalArgumentException.class,
        () -> new Arrow(body, owner, 0, TEN_PIXELS_PER_TICK, 0, ARROW_SPRITE, world));
  }

  private StateBuffer photoOfTheWorld() {
    StateBuffer photo = new StateBuffer();
    world.writeState(photo);
    return photo;
  }

  private void rewindTo(StateBuffer photo) {
    photo.startReading();
    world.readState(photo);
  }

  @Test
  void should_get_its_whole_lifetime_back_when_the_world_is_rewound() {
    Arrow arrow = arrowAt(100, 100, 0);
    StateBuffer photo = photoOfTheWorld();
    for (int tick = 1; tick < LIFETIME_TICKS; tick++) {
      world.tick();
    }
    rewindTo(photo);

    for (int tick = 1; tick < LIFETIME_TICKS; tick++) {
      world.tick();
    }

    assertFalse(arrow.isRemoved());
  }

  @Test
  void should_hurt_again_when_the_world_is_rewound_to_before_its_hit() {
    arrowAt(100, 100, 0);
    Character player = characterAt(115, 96, Faction.PLAYER);
    StateBuffer photo = photoOfTheWorld();
    world.tick();
    rewindTo(photo);

    world.tick();

    assertTrue(player.getHp() < 10);
  }

  @Test
  void should_point_where_it_flies() {
    Projectile arrow = arrowAt(0f, 0f, 135f);

    assertEquals(135f, arrow.getAngleDegrees(), 0.01f);
  }

  @Test
  void should_keep_the_sprite_it_was_given() {
    assertEquals(ARROW_SPRITE, arrowAt(0f, 0f, 0f).getSpriteId());
  }

  @Test
  void should_throw_when_its_sprite_is_null() {
    Aabb body = new Aabb(0, 0, 8, 4);

    assertThrows(
        NullPointerException.class,
        () -> new Arrow(body, owner, 0, TEN_PIXELS_PER_TICK, LIFETIME_TICKS, null, world));
  }

  @Test
  void should_throw_when_its_sprite_is_blank() {
    Aabb body = new Aabb(0, 0, 8, 4);

    assertThrows(
        IllegalArgumentException.class,
        () -> new Arrow(body, owner, 0, TEN_PIXELS_PER_TICK, LIFETIME_TICKS, " ", world));
  }
}
