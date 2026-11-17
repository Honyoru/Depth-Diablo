package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import org.junit.jupiter.api.Test;

/** DECISIONS §5: the all-around contact attack covers the body grown by its reach. */
class AroundContactAttackTest {

  @Test
  void should_cover_the_body_grown_by_its_reach() {
    Obb area = new AroundContactAttack(4f).area(new Aabb(0f, 0f, 16f, 16f), 0f);

    assertEquals(new Obb(8f, 8f, 0f, 24f, 24f), area);
  }

  @Test
  void should_cover_the_same_area_whatever_the_aim() {
    AroundContactAttack attack = new AroundContactAttack(4f);
    Aabb body = new Aabb(0f, 0f, 16f, 16f);

    assertEquals(attack.area(body, 0f), attack.area(body, 135f));
  }

  @Test
  void should_hit_on_every_side() {
    World world = new World();
    Character owner = aCharacterAt(world, 0f, 0f, Faction.ENEMY);
    Character right = aCharacterAt(world, 18f, 0f, Faction.PLAYER);
    Character left = aCharacterAt(world, -18f, 0f, Faction.PLAYER);
    Character above = aCharacterAt(world, 0f, 18f, Faction.PLAYER);
    Character below = aCharacterAt(world, 0f, -18f, Faction.PLAYER);

    new AroundContactAttack(4f).attack(owner, 0f, world);

    assertEquals(9, right.getHp());
    assertEquals(9, left.getHp());
    assertEquals(9, above.getHp());
    assertEquals(9, below.getHp());
  }

  private static Character aCharacterAt(World world, float x, float y, Faction faction) {
    Aabb box = new Aabb(x, y, 16f, 16f);
    Character character =
        aCharacter().withBodyBox(box).withHurtbox(box).withFaction(faction).build();
    world.add(character);
    return character;
  }
}
