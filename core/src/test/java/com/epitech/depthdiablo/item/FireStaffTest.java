package com.epitech.depthdiablo.item;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.combat.DotSpec;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Fireball;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: the fire staff casts a fireball that burns what it hurts. */
class FireStaffTest {
  private static final DotSpec BURN = new DotSpec(1, 30, 120);
  private final World world = new World();

  @Test
  void should_cast_one_fireball_centred_on_its_owner_when_it_attacks() {
    Character owner = aCharacter().withBodyBox(new Aabb(0f, 0f, 16f, 16f)).withWorld(world).build();

    aFireStaff(BURN).attack(owner, 0f, world);

    Fireball fireball =
        world.getEntities().stream()
            .filter(Fireball.class::isInstance)
            .map(Fireball.class::cast)
            .findFirst()
            .orElseThrow();
    Aabb box = fireball.getBodyBox();
    assertEquals(8f, box.x() + box.width() / 2f);
    assertEquals(8f, box.y() + box.height() / 2f);
    assertEquals(FireStaff.FIREBALL_SIZE, box.width());
  }

  @Test
  void should_give_its_fireballs_the_projectile_sprite_of_its_entry() {
    Character owner = aCharacter().withWorld(world).build();

    aFireStaff(BURN).attack(owner, 0f, world);

    Fireball fireball =
        world.getEntities().stream()
            .filter(Fireball.class::isInstance)
            .map(Fireball.class::cast)
            .findFirst()
            .orElseThrow();
    assertEquals("projectiles/fireball", fireball.getSpriteId());
  }

  @Test
  void should_keep_the_burn_of_its_entry() {
    assertEquals(BURN, aFireStaff(BURN).getBurn());
  }

  @Test
  void should_throw_when_its_burn_is_null() {
    assertThrows(NullPointerException.class, () -> aFireStaff(null));
  }

  private static FireStaff aFireStaff(DotSpec burn) {
    return new FireStaff(
        "fire_staff",
        "item.fire_staff.name",
        "items/staffs/fire_staff",
        Rarity.COMMON,
        0,
        0,
        1f,
        192f,
        300f,
        "projectiles/fireball",
        burn);
  }
}
