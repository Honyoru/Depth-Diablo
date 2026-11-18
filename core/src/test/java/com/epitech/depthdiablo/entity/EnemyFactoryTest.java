package com.epitech.depthdiablo.entity;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.engine.World;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Reading enemy types from data/enemies.json and building enemies from them (Q23, Q129). */
class EnemyFactoryTest {
  private static final String ONE_RAT =
      """
      { "enemies": [ {"id": "rat","class": "RAT", "spriteId": "enemies/rat",
        "stats": {
        "MAX_HP": 3,
        "ATTACK": 1,
        "DEFENSE": 1,
        "SPEED": 90,
        "ATTACK_SPEED": 1,
        "LUCK": 0 },
        "width": 16,
        "height": 16,
        "reach": 4,
        "invincibilityTicks": 18 } ] }
      """;
  private final World world = new World();

  @Test
  void should_give_the_values_of_the_entry_when_asked_for_a_spec() {
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> ONE_RAT), world);

    EnemySpec rat = factory.spec("rat");

    assertEquals("rat", rat.id());
    assertEquals("enemies/rat", rat.spriteId());
    assertEquals(16, rat.width());
    assertEquals(16, rat.height());
  }

  @Test
  void should_read_every_stat_of_the_entry_when_asked_for_a_spec() {
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> ONE_RAT), world);

    StatSource stats = factory.spec("rat").stats();

    assertEquals(3, stats.get(Stat.MAX_HP));
    assertEquals(1, stats.get(Stat.ATTACK));
    assertEquals(1, stats.get(Stat.DEFENSE));
    assertEquals(90, stats.get(Stat.SPEED));
    assertEquals(1, stats.get(Stat.ATTACK_SPEED));
    assertEquals(0, stats.get(Stat.LUCK));
  }

  @Test
  void should_reject_an_entry_when_its_speed_is_negative() {
    String file = ONE_RAT.replace("\"SPEED\": 90", "\"SPEED\": -1");
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> file), world);

    assertThrows(IllegalArgumentException.class, () -> factory.spec("rat"));
  }

  @Test
  void should_reject_an_entry_when_a_stat_is_missing() {
    String file = ONE_RAT.replace("\"LUCK\": 0", "\"LUK\": 0");
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> file), world);

    assertThrows(IllegalArgumentException.class, () -> factory.spec("rat"));
  }

  @Test
  void should_reject_an_entry_when_its_width_is_zero() {
    String file = ONE_RAT.replace("\"width\": 16", "\"width\": 0");
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> file), world);

    assertThrows(IllegalArgumentException.class, () -> factory.spec("rat"));
  }

  @Test
  void should_reject_an_entry_when_it_has_no_stats() {
    String file = "{ \"enemies\": [ { \"id\": \"rat\", \"spriteId\": \"enemies/rat\" } ] }";
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> file), world);

    assertThrows(IllegalArgumentException.class, () -> factory.spec("rat"));
  }

  @Test
  void should_read_every_enemy_of_the_data_file() throws IOException {
    String file = Files.readString(dataFile("enemies.json"), UTF_8);
    DataRegistry enemies = new DataRegistry("enemies", () -> file);
    EnemyFactory factory = new EnemyFactory(enemies, world);

    for (String id : enemies.getIds()) {
      assertEquals(id, factory.spec(id).id());
      assertEquals(EnemyState.IDLE, factory.create(id, 1, false, new Vector2()).getState());
    }
  }

  @Test
  void should_create_a_rat_with_the_values_of_its_entry() {
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> ONE_RAT), world);

    Enemy rat = factory.create("rat", 1, false, new Vector2(64, 32));

    assertInstanceOf(Rat.class, rat);
    assertEquals(new Aabb(64, 32, 16, 16), rat.getBodyBox());
    assertEquals(new Aabb(64, 32, 16, 16), rat.getHurtbox());
    assertEquals(3, rat.getHp());
    assertEquals(1, rat.getAttack());
    assertEquals(90f, rat.getSpeed());
    assertEquals(4f, ((MeleeEnemy) rat).getReach());
    assertFalse(rat.isSummoned());
  }

  @Test
  void should_make_the_enemy_invincible_after_a_hit_when_its_entry_says_so() {
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> ONE_RAT), world);
    Enemy rat = factory.create("rat", 1, false, new Vector2());

    rat.takeDamage(1);

    assertTrue(rat.isInvincible());
  }

  @Test
  void should_create_a_summoned_enemy_when_asked() {
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> ONE_RAT), world);

    Enemy rat = factory.create("rat", 1, true, new Vector2());

    assertTrue(rat.isSummoned());
  }

  @Test
  void should_build_the_class_of_the_entry_when_its_id_is_another_name() {
    String file = ONE_RAT.replace("\"id\": \"rat\"", "\"id\": \"rat_fast\"");
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> file), world);

    Enemy rat = factory.create("rat_fast", 1, false, new Vector2());

    assertInstanceOf(Rat.class, rat);
  }

  @Test
  void should_build_a_zombie_when_its_entry_says_so() {
    String file = ONE_RAT.replace("\"RAT\"", "\"ZOMBIE\"");
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> file), world);

    Enemy zombie = factory.create("rat", 1, false, new Vector2());

    assertInstanceOf(Zombie.class, zombie);
  }

  @Test
  void should_reject_a_floor_when_it_is_below_1() {
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> ONE_RAT), world);

    assertThrows(
        IllegalArgumentException.class, () -> factory.create("rat", 0, false, new Vector2()));
  }

  private static Path dataFile(String name) {
    return Path.of(System.getProperty("assets.dir"), "data", name);
  }
}
