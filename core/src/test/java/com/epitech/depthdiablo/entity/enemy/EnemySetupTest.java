package com.epitech.depthdiablo.entity.enemy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.stats.BaseStats;
import com.epitech.depthdiablo.entity.stats.Stat;
import com.epitech.depthdiablo.entity.stats.StatSource;
import com.epitech.depthdiablo.geometry.Aabb;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** What every enemy is built from (GDD 1.3.1): its values are checked once, here. */
class EnemySetupTest {
  private static final Aabb BODY = new Aabb(0, 0, 16, 16);
  private final StatSource stats = stats();
  private final World world = new World();

  @Test
  void should_keep_the_values_it_was_given() {
    EnemySetup setup = new EnemySetup(BODY, stats, "enemies/rat", 18, true, world);

    assertEquals(BODY, setup.body());
    assertSame(stats, setup.stats());
    assertEquals("enemies/rat", setup.spriteId());
    assertEquals(18, setup.invincibilityTicks());
    assertTrue(setup.summoned());
    assertSame(world, setup.world());
  }

  @Test
  void should_accept_no_invincibility_after_a_hit() {
    EnemySetup setup = new EnemySetup(BODY, stats, "enemies/rat", 0, false, world);

    assertEquals(0, setup.invincibilityTicks());
  }

  @Test
  void should_reject_a_missing_value() {
    assertThrows(
        NullPointerException.class,
        () -> new EnemySetup(null, stats, "enemies/rat", 0, false, world));
    assertThrows(
        NullPointerException.class,
        () -> new EnemySetup(BODY, null, "enemies/rat", 0, false, world));
    assertThrows(
        NullPointerException.class, () -> new EnemySetup(BODY, stats, null, 0, false, world));
    assertThrows(
        NullPointerException.class,
        () -> new EnemySetup(BODY, stats, "enemies/rat", 0, false, null));
  }

  @Test
  void should_reject_a_blank_sprite_id() {
    assertThrows(
        IllegalArgumentException.class, () -> new EnemySetup(BODY, stats, " ", 0, false, world));
  }

  @Test
  void should_reject_a_negative_invincibility() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new EnemySetup(BODY, stats, "enemies/rat", -1, false, world));
  }

  private static StatSource stats() {
    Map<Stat, Float> values = new EnumMap<>(Stat.class);
    for (Stat stat : Stat.values()) {
      values.put(stat, 1f);
    }
    return new BaseStats(values);
  }
}
