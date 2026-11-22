package com.epitech.depthdiablo.item;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Character;
import com.epitech.depthdiablo.entity.projectile.Arrow;
import com.epitech.depthdiablo.geometry.Aabb;
import java.util.List;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: a bow fires one arrow per attack, which flies the bow's range. */
class BowTest {
  private final World world = new World();

  @Test
  void should_fly_its_range_at_its_speed_when_it_gives_the_arrow_lifetime() {
    Bow bow = aBow(120f, 240f); // 120 px at 240 px/s: half a second

    assertEquals(30, bow.getProjectileLifetimeTicks());
  }

  @Test
  void should_fly_at_least_1_tick_when_its_arrow_is_very_fast() {
    Bow bow = aBow(1f, 10_000f); // would round to 0 ticks

    assertEquals(1, bow.getProjectileLifetimeTicks());
  }

  @Test
  void should_throw_when_the_arrow_speed_is_0() {
    assertThrows(IllegalArgumentException.class, () -> aBow(120f, 0f));
  }

  @Test
  void should_throw_when_the_arrow_speed_is_not_a_number() {
    assertThrows(IllegalArgumentException.class, () -> aBow(120f, Float.NaN));
  }

  @Test
  void should_throw_when_the_arrow_speed_is_infinite() {
    assertThrows(IllegalArgumentException.class, () -> aBow(120f, Float.POSITIVE_INFINITY));
  }

  @Test
  void should_fire_one_arrow_centred_on_its_owner_when_it_attacks() {
    Character owner = aCharacter().withWorld(world).build(); // body (0, 0, 16, 16)

    aBow(120f, 240f).attack(owner, 0f, world);

    List<Arrow> arrows = arrowsIn(world);
    assertEquals(1, arrows.size());
    assertEquals(new Aabb(0f, 6f, 16f, 4f), arrows.get(0).getBodyBox());
  }

  @Test
  void should_start_its_owner_cooldown_when_it_attacks() {
    Character owner = aCharacter().withWorld(world).build();

    aBow(120f, 240f).attack(owner, 0f, world);

    assertFalse(owner.canAttack());
  }

  @Test
  void should_leave_its_owner_ready_and_fire_nothing_when_the_aim_is_not_a_number() {
    Character owner = aCharacter().withWorld(world).build();
    Bow bow = aBow(120f, 240f);

    assertThrows(IllegalArgumentException.class, () -> bow.attack(owner, Float.NaN, world));

    assertTrue(owner.canAttack());
    assertTrue(arrowsIn(world).isEmpty());
  }

  @Test
  void should_fire_nothing_when_its_owner_cannot_attack_yet() {
    Character owner = aCharacter().withWorld(world).build();
    Bow bow = aBow(120f, 240f);
    bow.attack(owner, 0f, world); // the first shot starts the cooldown

    assertThrows(IllegalStateException.class, () -> bow.attack(owner, 0f, world));

    assertEquals(1, arrowsIn(world).size());
  }

  @Test
  void should_give_its_arrows_the_projectile_sprite_of_its_entry() {
    Character owner = aCharacter().withWorld(world).build();
    Bow bow = aBow(120f, 240f);

    bow.attack(owner, 0f, world);

    assertEquals(bow.getProjectileSpriteId(), arrowsIn(world).get(0).getSpriteId());
  }

  @Test
  void should_throw_when_its_projectile_sprite_is_blank() {
    assertThrows(IllegalArgumentException.class, () -> new TestBow(120f, 240f, " "));
  }

  /** A bow with the given range and arrow speed; the other values do not matter here. */
  private static Bow aBow(float range, float projectileSpeed) {
    return new TestBow(range, projectileSpeed, "projectiles/arrow");
  }

  /** The arrows in the world, in the order they were added. */
  private static List<Arrow> arrowsIn(World world) {
    return world.getEntities().stream()
        .filter(Arrow.class::isInstance)
        .map(Arrow.class::cast)
        .toList();
  }

  /** The smallest real bow: the family with no value of its own. */
  private static final class TestBow extends Bow {
    TestBow(float range, float projectileSpeed, String projectileSpriteId) {
      super(
          "test_bow",
          "item.test_bow.name",
          "items/bows/test",
          Rarity.COMMON,
          0,
          1,
          1f,
          range,
          projectileSpeed,
          projectileSpriteId);
    }
  }
}
