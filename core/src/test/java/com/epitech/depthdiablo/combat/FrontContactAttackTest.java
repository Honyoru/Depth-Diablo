package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Character;
import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.geometry.Obb;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: the front contact attack hits towards the aim, as far past the body at any angle. */
class FrontContactAttackTest {
  private static final float DELTA = 0.001f;
  private static final float DIAGONAL = (float) Math.sqrt(2);

  @Test
  void should_point_its_area_towards_the_aim() {
    Obb area = new FrontContactAttack(4f).area(new Aabb(0f, 0f, 16f, 16f), 0f);

    assertEquals(14f, area.centreX(), DELTA);
    assertEquals(8f, area.centreY(), DELTA);
    assertEquals(0f, area.angleDegrees(), DELTA);
    assertEquals(12f, area.length(), DELTA);
    assertEquals(16f, area.width(), DELTA);
  }

  @Test
  void should_hit_in_front_and_not_behind() {
    World world = new World();
    Character owner = aCharacterAt(world, 0f, 0f, Faction.ENEMY);
    Character front = aCharacterAt(world, 18f, 0f, Faction.PLAYER);
    Character behind = aCharacterAt(world, -18f, 0f, Faction.PLAYER);

    new FrontContactAttack(4f).attack(owner, 0f, world);

    assertEquals(9, front.getHp());
    assertEquals(10, behind.getHp());
  }

  @Test
  void should_hit_above_when_aiming_up() {
    World world = new World();
    Character owner = aCharacterAt(world, 0f, 0f, Faction.ENEMY);
    Character above = aCharacterAt(world, 0f, 18f, Faction.PLAYER);
    Character below = aCharacterAt(world, 0f, -18f, Faction.PLAYER);

    new FrontContactAttack(4f).attack(owner, 90f, world);

    assertEquals(9, above.getHp());
    assertEquals(10, below.getHp());
  }

  @Test
  void should_hit_the_diagonal_it_aims_at() {
    World world = new World();
    Character owner = aCharacterAt(world, 0f, 0f, Faction.ENEMY);
    Character diagonal = aCharacterAt(world, 18f, 18f, Faction.PLAYER);
    Character opposite = aCharacterAt(world, -18f, -18f, Faction.PLAYER);

    new FrontContactAttack(4f).attack(owner, 45f, world);

    assertEquals(9, diagonal.getHp());
    assertEquals(10, opposite.getHp());
  }

  @Test
  void should_reach_as_far_past_the_body_at_45_degrees_as_at_0() {
    World straightWorld = new World();
    Character straightOwner = aCharacterAt(straightWorld, 0f, 0f, Faction.ENEMY);
    Character straight = aCharacterAt(straightWorld, 16f + 3f, 0f, Faction.PLAYER);
    World diagonalWorld = new World();
    Character diagonalOwner = aCharacterAt(diagonalWorld, 0f, 0f, Faction.ENEMY);
    float past = 16f + 3f / DIAGONAL;
    Character diagonal = aCharacterAt(diagonalWorld, past, past, Faction.PLAYER);

    new FrontContactAttack(4f).attack(straightOwner, 0f, straightWorld);
    new FrontContactAttack(4f).attack(diagonalOwner, 45f, diagonalWorld);

    assertEquals(9, straight.getHp());
    assertEquals(9, diagonal.getHp());
  }

  @Test
  void should_not_reach_further_than_its_reach() {
    World straightWorld = new World();
    Character straightOwner = aCharacterAt(straightWorld, 0f, 0f, Faction.ENEMY);
    Character straight = aCharacterAt(straightWorld, 16f + 5f, 0f, Faction.PLAYER);
    World diagonalWorld = new World();
    Character diagonalOwner = aCharacterAt(diagonalWorld, 0f, 0f, Faction.ENEMY);
    float past = 16f + 5f / DIAGONAL;
    Character diagonal = aCharacterAt(diagonalWorld, past, past, Faction.PLAYER);

    new FrontContactAttack(4f).attack(straightOwner, 0f, straightWorld);
    new FrontContactAttack(4f).attack(diagonalOwner, 45f, diagonalWorld);

    assertEquals(10, straight.getHp());
    assertEquals(10, diagonal.getHp());
  }

  @Test
  void should_touch_a_hurtbox_in_front_of_its_aim() {
    Aabb body = new Aabb(0f, 0f, 16f, 16f);

    assertTrue(new FrontContactAttack(4f).touches(body, 0f, new Aabb(18f, 0f, 16f, 16f)));
  }

  @Test
  void should_not_touch_a_hurtbox_behind_its_aim() {
    Aabb body = new Aabb(0f, 0f, 16f, 16f);

    assertFalse(new FrontContactAttack(4f).touches(body, 0f, new Aabb(-18f, 0f, 16f, 16f)));
  }

  private static Character aCharacterAt(World world, float x, float y, Faction faction) {
    Aabb box = new Aabb(x, y, 16f, 16f);
    Character character =
        aCharacter().withBodyBox(box).withHurtbox(box).withFaction(faction).build();
    world.add(character);
    return character;
  }
}
