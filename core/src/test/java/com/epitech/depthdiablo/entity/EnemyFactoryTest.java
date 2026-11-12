package com.epitech.depthdiablo.entity;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.data.DataRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Reading enemy types from data/enemies.json (Q23, Q129). */
class EnemyFactoryTest {
  private static final String ONE_RAT =
      """
      { "enemies": [ {
          "id": "rat", "spriteId": "enemies/rat", "maxHp": 3, "attack": 1, "resistance": 1,
          "speed": 90, "width": 16, "height": 16 } ] }
      """;

  @Test
  void should_give_the_values_of_the_entry_when_asked_for_a_spec() {
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> ONE_RAT));

    EnemySpec rat = factory.spec("rat");

    assertEquals(new EnemySpec("rat", "enemies/rat", 3, 1, 1, 90, 16, 16), rat);
  }

  @Test
  void should_reject_an_entry_when_its_speed_is_negative() {
    String file = ONE_RAT.replace("\"speed\": 90", "\"speed\": -1");
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> file));

    assertThrows(IllegalArgumentException.class, () -> factory.spec("rat"));
  }

  @Test
  void should_reject_an_entry_when_its_width_is_zero() {
    String file = ONE_RAT.replace("\"width\": 16", "\"width\": 0");
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> file));

    assertThrows(IllegalArgumentException.class, () -> factory.spec("rat"));
  }

  @Test
  void should_reject_an_entry_when_a_value_is_missing() {
    String file = "{ \"enemies\": [ { \"id\": \"rat\" } ] }";
    EnemyFactory factory = new EnemyFactory(new DataRegistry("enemies", () -> file));

    assertThrows(IllegalArgumentException.class, () -> factory.spec("rat"));
  }

  @Test
  void should_read_every_enemy_of_the_data_file() throws IOException {
    String file = Files.readString(dataFile("enemies.json"), UTF_8);
    DataRegistry enemies = new DataRegistry("enemies", () -> file);
    EnemyFactory factory = new EnemyFactory(enemies);

    for (String id : enemies.ids()) {
      assertEquals(id, factory.spec(id).id());
    }
  }

  private static Path dataFile(String name) {
    return Path.of(System.getProperty("assets.dir"), "data", name);
  }
}
