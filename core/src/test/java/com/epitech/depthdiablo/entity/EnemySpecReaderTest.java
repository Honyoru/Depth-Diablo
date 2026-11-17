package com.epitech.depthdiablo.entity;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.data.DataRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Q23 and Q129: one entry of data/enemies.json becomes an EnemySpec. */
class EnemySpecReaderTest {

  private static final String RAT =
      """
      { "id": "rat", "spriteId": "enemies/rat",
        "stats": { "MAX_HP": 3, "ATTACK": 1, "DEFENSE": 2, "SPEED": 90,
                   "ATTACK_SPEED": 1.5, "LUCK": 0 },
        "width": 16, "height": 20 }
      """;

  @Test
  void should_read_the_values_of_the_entry() {
    EnemySpec rat = EnemySpecReader.read("rat", parse(RAT));

    assertEquals("rat", rat.id());
    assertEquals("enemies/rat", rat.spriteId());
    assertEquals(16f, rat.width());
    assertEquals(20f, rat.height());
  }

  @Test
  void should_read_every_stat_of_the_entry() {
    EnemySpec rat = EnemySpecReader.read("rat", parse(RAT));

    assertEquals(3f, rat.stats().get(Stat.MAX_HP));
    assertEquals(1f, rat.stats().get(Stat.ATTACK));
    assertEquals(2f, rat.stats().get(Stat.DEFENSE));
    assertEquals(90f, rat.stats().get(Stat.SPEED));
    assertEquals(1.5f, rat.stats().get(Stat.ATTACK_SPEED));
    assertEquals(0f, rat.stats().get(Stat.LUCK));
  }

  @Test
  void should_use_the_given_id() {
    EnemySpec spec = EnemySpecReader.read("rat_big", parse(RAT));

    assertEquals("rat_big", spec.id());
  }

  @Test
  void should_throw_when_the_entry_has_no_stats() {
    JsonValue entry = parse(RAT.replace("\"stats\"", "\"values\""));

    assertThrows(IllegalArgumentException.class, () -> EnemySpecReader.read("rat", entry));
  }

  @Test
  void should_throw_when_a_stat_is_missing() {
    JsonValue entry = parse(RAT.replace("\"LUCK\"", "\"CHARM\""));

    assertThrows(IllegalArgumentException.class, () -> EnemySpecReader.read("rat", entry));
  }

  @Test
  void should_throw_when_a_stat_is_invalid() {
    JsonValue entry = parse(RAT.replace("\"SPEED\": 90", "\"SPEED\": -1"));

    assertThrows(IllegalArgumentException.class, () -> EnemySpecReader.read("rat", entry));
  }

  @Test
  void should_throw_when_the_size_is_missing() {
    JsonValue entry = parse(RAT.replace("\"width\"", "\"wide\""));

    assertThrows(IllegalArgumentException.class, () -> EnemySpecReader.read("rat", entry));
  }

  @Test
  void should_throw_when_the_width_is_0() {
    JsonValue entry = parse(RAT.replace("\"width\": 16", "\"width\": 0"));

    assertThrows(IllegalArgumentException.class, () -> EnemySpecReader.read("rat", entry));
  }

  @Test
  void should_throw_when_the_id_is_null() {
    JsonValue entry = parse(RAT);

    assertThrows(NullPointerException.class, () -> EnemySpecReader.read(null, entry));
  }

  @Test
  void should_throw_when_the_entry_is_null() {
    assertThrows(NullPointerException.class, () -> EnemySpecReader.read("rat", null));
  }

  @Test
  void should_read_every_entry_of_the_data_file() throws IOException {
    Path file = Path.of(System.getProperty("assets.dir"), "data", "enemies.json");
    String text = Files.readString(file, UTF_8);
    DataRegistry enemies = new DataRegistry("enemies", () -> text);

    for (String id : enemies.getIds()) {
      assertEquals(id, EnemySpecReader.read(id, enemies.definition(id)).id());
    }
  }

  private static JsonValue parse(String json) {
    return new JsonReader().parse(json);
  }
}
