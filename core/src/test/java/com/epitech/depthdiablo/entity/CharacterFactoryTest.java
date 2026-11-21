package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.input.PlayerOrders;
import com.epitech.depthdiablo.testdata.CharacterBuilder;
import com.epitech.depthdiablo.testdata.Items;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Building any character of data/characters.json from its id (Q23, Q129). */
class CharacterFactoryTest {
  private static final String CHARACTERS_TEST =
      """
      { "characters": [
        { "id": "knight", "class": "KNIGHT", "spriteId": "player/knight",
          "stats": { "MAX_HP": 100, "ATTACK": 5, "DEFENSE": 1, "SPEED": 120,
                     "ATTACK_SPEED": 1, "LUCK": 0 },
          "width": 28, "height": 28, "invincibilityTicks": 30, "weapon": "sword",
          "armor": "leather_armor" },
        { "id": "rat", "class": "RAT", "spriteId": "enemies/rat",
          "stats": { "MAX_HP": 3, "ATTACK": 1, "DEFENSE": 1, "SPEED": 90,
                     "ATTACK_SPEED": 1, "LUCK": 0 },
          "width": 16, "height": 16, "invincibilityTicks": 18, "reach": 4 }
      ] }
      """;
  private final World world = new World();

  /** The text F5 reads again: a test changes it, then calls reload(). */
  private String file = CHARACTERS_TEST;

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

  @Test
  void should_give_the_new_stats_to_every_live_character_when_reloaded() {
    CharacterFactory factory = reloadingFactory();
    Enemy first = factory.create("rat", new Vector2(), Enemy.class);
    Enemy second = factory.create("rat", new Vector2(), Enemy.class);
    file = CHARACTERS_TEST.replace("\"ATTACK\": 1,", "\"ATTACK\": 7,");

    factory.reload();

    assertEquals(7, first.getAttack());
    assertEquals(7, second.getAttack());
  }

  @Test
  void should_keep_the_weapon_bonus_on_the_new_stats_when_reloaded() {
    CharacterFactory factory = reloadingFactory();
    Player knight = factory.create("knight", new Vector2(), Player.class);
    file = CHARACTERS_TEST.replace("\"ATTACK\": 5,", "\"ATTACK\": 8,");

    factory.reload();

    assertEquals(8 + Items.sword().getAttackBonus(), knight.getAttack());
  }

  @Test
  void should_give_the_new_stats_to_characters_created_after_a_reload() {
    CharacterFactory factory = reloadingFactory();
    factory.create("rat", new Vector2(), Enemy.class);
    file = CHARACTERS_TEST.replace("\"ATTACK\": 1,", "\"ATTACK\": 7,");

    factory.reload();

    assertEquals(7, factory.create("rat", new Vector2(), Enemy.class).getAttack());
  }

  @Test
  void should_keep_the_old_stats_when_a_reloaded_value_is_invalid() {
    CharacterFactory factory = reloadingFactory();
    Enemy rat = factory.create("rat", new Vector2(), Enemy.class);
    file = CHARACTERS_TEST.replace("\"SPEED\": 90,", "\"SPEED\": -5,");

    assertThrows(IllegalArgumentException.class, factory::reload);

    assertEquals(90f, rat.getSpeed());
    assertEquals(90f, factory.create("rat", new Vector2(), Enemy.class).getSpeed());
  }

  @Test
  void should_keep_the_old_stats_of_a_character_removed_from_the_file_when_reloaded() {
    CharacterFactory factory = reloadingFactory();
    Enemy rat = factory.create("rat", new Vector2(), Enemy.class);
    file =
        CHARACTERS_TEST
            .replace("\"id\": \"rat\"", "\"id\": \"rat_renamed\"")
            .replace("\"ATTACK\": 1,", "\"ATTACK\": 7,");

    factory.reload();

    assertEquals(1, rat.getAttack());
  }

  @Test
  void should_build_the_king_of_the_arena_from_the_real_file() throws IOException {
    CharacterFactory factory = factory(realCharactersFile());

    Enemy king = factory.create("king", new Vector2(), Enemy.class);

    assertInstanceOf(Zombie.class, king);
  }

  @Test
  void should_make_the_king_bigger_and_tougher_than_a_rat() throws IOException {
    CharacterFactory factory = factory(realCharactersFile());

    Enemy king = factory.create("king", new Vector2(), Enemy.class);
    Enemy rat = factory.create("rat", new Vector2(), Enemy.class);

    assertTrue(king.getMaxHp() > rat.getMaxHp());
    assertTrue(king.getBodyBox().width() > rat.getBodyBox().width());
  }

  @Test
  void should_give_each_enemy_the_sprite_id_of_its_entry() {
    CharacterFactory factory = factory(CHARACTERS_TEST);

    assertEquals("enemies/rat", factory.create("rat", new Vector2(), Enemy.class).getSpriteId());
  }

  @Test
  void should_create_enemies_that_are_not_summoned() {
    CharacterFactory factory = factory(CHARACTERS_TEST);

    assertFalse(factory.create("rat", new Vector2(), Enemy.class).isSummoned());
  }

  @Test
  void should_summon_an_enemy_marked_as_summoned() {
    CharacterFactory factory = factory(CHARACTERS_TEST);

    Enemy rat = factory.summon("rat", new Vector2());

    assertInstanceOf(Rat.class, rat);
    assertTrue(rat.isSummoned());
  }

  @Test
  void should_summon_an_enemy_centred_on_the_given_point() {
    CharacterFactory factory = factory(CHARACTERS_TEST);

    Enemy rat = factory.summon("rat", new Vector2(50, 60));

    assertEquals(new Aabb(42, 52, 16, 16), rat.getBodyBox());
  }

  @Test
  void should_refuse_to_summon_a_player() {
    CharacterFactory factory = factory(CHARACTERS_TEST);

    assertThrows(IllegalArgumentException.class, () -> factory.summon("knight", new Vector2()));
  }

  @Test
  void should_build_the_rat_nest_of_the_real_file_as_a_still_summoner() throws IOException {
    CharacterFactory factory = factory(realCharactersFile());

    Enemy nest = factory.create("rat_nest", new Vector2(), Enemy.class);

    assertInstanceOf(RatNest.class, nest);
    assertEquals(0f, nest.getSpeed());
    assertEquals("enemies/rat_nest", nest.getSpriteId());
  }

  @Test
  void should_let_the_rat_nest_of_the_real_file_summon_summoned_rats() throws IOException {
    CharacterFactory factory = factory(realCharactersFile());
    Enemy nest = factory.create("rat_nest", new Vector2(100, 100), Enemy.class);
    world.add(nest);
    nest.alert(CharacterBuilder.aCharacter().withFaction(Faction.PLAYER).build());

    world.tick(); // IDLE -> SUMMON
    world.tick(); // the first summon, added to the world at the end of the tick

    Enemy rat =
        world.getEntities().stream()
            .filter(Rat.class::isInstance)
            .map(Enemy.class::cast)
            .findFirst()
            .orElseThrow();
    assertTrue(rat.isSummoned());
  }

  @Test
  void should_build_the_assassin_from_the_real_file() throws IOException {
    CharacterFactory factory = factory(realCharactersFile());

    Player assassin = factory.create("assassin", new Vector2(), Player.class);

    assertInstanceOf(Assassin.class, assassin);
  }

  @Test
  void should_make_the_assassin_faster_and_more_fragile_than_the_knight() throws IOException {
    CharacterFactory factory = factory(realCharactersFile());

    Player assassin = factory.create("assassin", new Vector2(), Player.class);
    Player knight = factory.create("knight", new Vector2(), Player.class);

    assertTrue(assassin.getSpeed() > knight.getSpeed());
    assertTrue(assassin.getMaxHp() < knight.getMaxHp());
  }

  private static String realCharactersFile() throws IOException {
    return Files.readString(
        Path.of(System.getProperty("assets.dir"), "data", "characters.json"),
        StandardCharsets.UTF_8);
  }

  private CharacterFactory factory(String file) {
    return new CharacterFactory(
        new DataRegistry("characters", () -> file),
        new PlayerFactory(Items.factory(), new PlayerOrders(), world),
        new EnemyFactory(world));
  }

  /** A factory reading the {@code file} field each time, so a test can change it before F5. */
  private CharacterFactory reloadingFactory() {
    return new CharacterFactory(
        new DataRegistry("characters", () -> file),
        new PlayerFactory(Items.factory(), new PlayerOrders(), world),
        new EnemyFactory(world));
  }
}
