package com.epitech.depthdiablo.item;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Arrow;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Projectile;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: a staff fires the spell its real staff picks, at the staff's speed. */
class StaffTest {
  private final World world = new World();

  @Test
  void should_fire_the_projectile_its_real_staff_picks_when_it_attacks() {
    Character owner = aCharacter().withWorld(world).build();

    new TestStaff().attack(owner, 0f, world);

    assertEquals(1, world.getEntities().stream().filter(Arrow.class::isInstance).count());
  }

  @Test
  void should_keep_its_projectile_speed_when_created() {
    assertEquals(240f, new TestStaff().getProjectileSpeed());
  }

  /** The smallest real staff: it picks a plain arrow, which needs no spell data. */
  private static final class TestStaff extends Staff {
    TestStaff() {
      super(
          "test_staff",
          "item.test_staff.name",
          "items/staffs/test",
          Rarity.COMMON,
          0,
          1,
          1f,
          120f,
          240f,
          "projectiles/arrow");
    }

    @Override
    protected Projectile createProjectile(Character owner, float aimDegrees, World world) {
      return new Arrow(
          new Aabb(0f, 0f, 8f, 8f),
          owner,
          aimDegrees,
          getProjectileSpeed(),
          getProjectileLifetimeTicks(),
          getProjectileSpriteId(),
          world);
    }
  }
}
