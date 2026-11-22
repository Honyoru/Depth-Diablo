package com.epitech.depthdiablo.entity.spawn;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Knight;
import com.epitech.depthdiablo.entity.character.Player;
import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.input.PlayerOrders;
import com.epitech.depthdiablo.item.LeatherArmor;
import com.epitech.depthdiablo.item.Sword;
import com.epitech.depthdiablo.testdata.Items;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Building the players of data/characters.json from their entry (Q23, Q129). */
class PlayerFactoryTest {
  private static final String KNIGHT_TEST =
      """
      { "id": "knight", "class": "KNIGHT", "spriteId": "player/knight",
        "stats": { "MAX_HP": 100, "ATTACK": 5, "DEFENSE": 1, "SPEED": 120,
                   "ATTACK_SPEED": 1, "LUCK": 0 },
        "width": 28, "height": 28, "invincibilityTicks": 30, "weapon": "sword",
        "armor": "leather_armor" }
      """;
  private static final Aabb BODY = new Aabb(64, 32, 28, 28);
  private final PlayerFactory factory =
      new PlayerFactory(Items.factory(), new PlayerOrders(), new World());

  @Test
  void should_create_a_knight_with_the_values_of_its_entry() {
    Player knight = create(KNIGHT_TEST);

    assertInstanceOf(Knight.class, knight);
    assertEquals(BODY, knight.getBodyBox());
    assertEquals(BODY, knight.getHurtbox());
    assertEquals(100, knight.getHp());
    assertEquals(120f, knight.getSpeed());
  }

  @Test
  void should_give_the_knight_the_weapon_of_its_entry() {
    Player knight = create(KNIGHT_TEST);

    assertInstanceOf(Sword.class, knight.getInventory().getWeapon());
  }

  @Test
  void should_give_the_knight_the_armor_of_its_entry() {
    Player knight = create(KNIGHT_TEST);

    assertInstanceOf(LeatherArmor.class, knight.getInventory().getArmor());
  }

  @Test
  void should_make_the_player_invincible_after_a_hit_when_its_entry_says_so() {
    Player knight = create(KNIGHT_TEST);

    knight.takeDamage(1);

    assertTrue(knight.isInvincible());
  }

  @Test
  void should_throw_when_the_weapon_is_missing() {
    String entry = KNIGHT_TEST.replace("\"weapon\"", "\"sword\"");

    assertThrows(IllegalArgumentException.class, () -> create(entry));
  }

  @Test
  void should_throw_when_the_weapon_is_unknown() {
    String entry = KNIGHT_TEST.replace("\"weapon\": \"sword\"", "\"weapon\": \"laser\"");

    assertThrows(IllegalArgumentException.class, () -> create(entry));
  }

  @Test
  void should_throw_when_the_weapon_is_not_a_weapon() {
    String entry = KNIGHT_TEST.replace("\"weapon\": \"sword\"", "\"weapon\": \"potion_small\"");

    assertThrows(IllegalArgumentException.class, () -> create(entry));
  }

  @Test
  void should_throw_when_the_armor_is_missing() {
    String entry = KNIGHT_TEST.replace("\"armor\"", "\"coat\"");

    assertThrows(IllegalArgumentException.class, () -> create(entry));
  }

  @Test
  void should_throw_when_the_armor_is_not_an_armor() {
    String entry = KNIGHT_TEST.replace("\"armor\": \"leather_armor\"", "\"armor\": \"sword\"");

    assertThrows(IllegalArgumentException.class, () -> create(entry));
  }

  @Test
  void should_create_every_player_of_the_data_file() throws IOException {
    String text = Files.readString(dataFile("characters.json"), UTF_8);
    DataRegistry characters = new DataRegistry("characters", () -> text);

    for (String id : characters.getIds()) {
      JsonValue entry = characters.definition(id);
      CharacterSpec spec = CharacterSpecReader.read(id, entry);
      if (spec.kind() instanceof PlayerKind kind) {
        assertInstanceOf(Player.class, factory.create(kind, spec, entry, BODY));
      }
    }
  }

  /** Reads the entry with the real reader, then builds it on {@link #BODY}. */
  private Player create(String json) {
    JsonValue entry = new JsonReader().parse(json);
    CharacterSpec spec = CharacterSpecReader.read("knight", entry);
    return factory.create((PlayerKind) spec.kind(), spec, entry, BODY);
  }

  private static Path dataFile(String name) {
    return Path.of(System.getProperty("assets.dir"), "data", name);
  }
}
