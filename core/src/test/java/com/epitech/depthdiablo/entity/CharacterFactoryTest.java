package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.input.PlayerOrders;
import com.epitech.depthdiablo.testdata.Items;
import org.junit.jupiter.api.Test;

/** Building any character of data/characters.json from its id (Q23, Q129). */
class CharacterFactoryTest {
  private static final String CHARACTERS_TEST =
      """
      { "characters": [
        { "id": "knight", "class": "KNIGHT", "spriteId": "player/knight",
          "stats": { "MAX_HP": 100, "ATTACK": 5, "DEFENSE": 1, "SPEED": 120,
                     "ATTACK_SPEED": 1, "LUCK": 0 },
          "width": 28, "height": 28, "invincibilityTicks": 30, "weapon": "sword" },
        { "id": "rat", "class": "RAT", "spriteId": "enemies/rat",
          "stats": { "MAX_HP": 3, "ATTACK": 1, "DEFENSE": 1, "SPEED": 90,
                     "ATTACK_SPEED": 1, "LUCK": 0 },
          "width": 16, "height": 16, "invincibilityTicks": 18, "reach": 4 }
      ] }
      """;
  private final World world = new World();

  @Test
  void should_create_a_player_when_its_entry_is_a_player_kind() {
    Player knight = factory(CHARACTERS_TEST).create("knight", new Vector2(64, 32), Player.class);

    assertInstanceOf(Knight.class, knight);
  }

  @Test
  void should_create_an_enemy_when_its_entry_is_an_enemy_kind() {
    Enemy rat = factory(CHARACTERS_TEST).create("rat", new Vector2(64, 32), Enemy.class);

    assertInstanceOf(Rat.class, rat);
  }

  @Test
  void should_centre_the_body_of_its_entry_size_on_the_given_point() {
    Enemy rat = factory(CHARACTERS_TEST).create("rat", new Vector2(64, 32), Enemy.class);

    assertEquals(new Aabb(56, 24, 16, 16), rat.getBodyBox());
  }

  @Test
  void should_build_the_class_of_the_entry_when_its_id_is_another_name() {
    String file = CHARACTERS_TEST.replace("\"id\": \"rat\"", "\"id\": \"rat_fast\"");

    Enemy rat = factory(file).create("rat_fast", new Vector2(), Enemy.class);

    assertInstanceOf(Rat.class, rat);
  }

  @Test
  void should_accept_any_supertype_of_the_character() {
    Character rat = factory(CHARACTERS_TEST).create("rat", new Vector2(), Character.class);

    assertInstanceOf(Rat.class, rat);
  }

  @Test
  void should_throw_when_the_character_is_not_of_the_asked_type() {
    CharacterFactory factory = factory(CHARACTERS_TEST);
    Vector2 position = new Vector2();

    assertThrows(
        IllegalArgumentException.class, () -> factory.create("rat", position, Player.class));
  }

  @Test
  void should_throw_when_the_id_is_unknown() {
    CharacterFactory factory = factory(CHARACTERS_TEST);
    Vector2 position = new Vector2();

    assertThrows(
        IllegalArgumentException.class, () -> factory.create("dragon", position, Enemy.class));
  }

  @Test
  void should_throw_when_the_position_is_null() {
    CharacterFactory factory = factory(CHARACTERS_TEST);

    assertThrows(NullPointerException.class, () -> factory.create("rat", null, Enemy.class));
  }

  @Test
  void should_throw_when_the_type_is_null() {
    CharacterFactory factory = factory(CHARACTERS_TEST);
    Vector2 position = new Vector2();

    assertThrows(NullPointerException.class, () -> factory.create("rat", position, null));
  }

  private CharacterFactory factory(String file) {
    return new CharacterFactory(
        new DataRegistry("characters", () -> file),
        new PlayerFactory(Items.factory(), new PlayerOrders(), world),
        new EnemyFactory(world));
  }
}
