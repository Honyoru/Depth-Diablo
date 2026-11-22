package com.epitech.depthdiablo.entity.spawn;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.enemy.Enemy;
import com.epitech.depthdiablo.entity.enemy.EnemyState;
import com.epitech.depthdiablo.entity.enemy.MeleeEnemy;
import com.epitech.depthdiablo.entity.enemy.Rat;
import com.epitech.depthdiablo.entity.enemy.Summoner;
import com.epitech.depthdiablo.entity.enemy.Zombie;
import com.epitech.depthdiablo.geometry.Aabb;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Building the enemies of data/characters.json from their entry (Q23, Q129). */
class EnemyFactoryTest {
  private static final String RAT =
      """
      { "id": "rat", "class": "RAT", "spriteId": "enemies/rat",
        "stats": { "MAX_HP": 3, "ATTACK": 1, "DEFENSE": 1, "SPEED": 90,
                   "ATTACK_SPEED": 1, "LUCK": 0 },
        "width": 16, "height": 16, "invincibilityTicks": 18, "reach": 4 }
      """;
  private static final Aabb BODY = new Aabb(64, 32, 16, 16);

  /** Fails the test if it is ever asked: building an enemy must not summon anything. */
  private static final Summoner NO_SUMMONER =
      (id, centre) -> {
        throw new AssertionError("Building an enemy must not summon " + id);
      };

  private final EnemyFactory factory = new EnemyFactory(new World());

  @Test
  void should_create_a_rat_with_the_values_of_its_entry() {
    Enemy rat = create(RAT);

    assertInstanceOf(Rat.class, rat);
    assertEquals(BODY, rat.getBodyBox());
    assertEquals(BODY, rat.getHurtbox());
    assertEquals(3, rat.getHp());
    assertEquals(1, rat.getAttack());
    assertEquals(90f, rat.getSpeed());
    assertEquals(4f, ((MeleeEnemy) rat).getReach());
    assertFalse(rat.isSummoned());
  }

  @Test
  void should_make_the_enemy_invincible_after_a_hit_when_its_entry_says_so() {
    Enemy rat = create(RAT);

    rat.takeDamage(1);

    assertTrue(rat.isInvincible());
  }

  @Test
  void should_build_a_zombie_when_its_entry_says_so() {
    assertInstanceOf(Zombie.class, create(RAT.replace("\"RAT\"", "\"ZOMBIE\"")));
  }

  @Test
  void should_throw_when_the_reach_is_missing() {
    String entry = RAT.replace("\"reach\"", "\"range\"");

    assertThrows(IllegalArgumentException.class, () -> create(entry));
  }

  @Test
  void should_throw_when_the_reach_is_negative() {
    String entry = RAT.replace("\"reach\": 4", "\"reach\": -1");

    assertThrows(IllegalArgumentException.class, () -> create(entry));
  }

  @Test
  void should_create_every_enemy_of_the_data_file() throws IOException {
    Path file = Path.of(System.getProperty("assets.dir"), "data", "characters.json");
    String text = Files.readString(file, UTF_8);
    DataRegistry characters = new DataRegistry("characters", () -> text);

    for (String id : characters.getIds()) {
      JsonValue entry = characters.definition(id);
      CharacterSpec spec = CharacterSpecReader.read(id, entry);
      if (spec.kind() instanceof EnemyKind kind) {
        assertEquals(
            EnemyState.IDLE,
            factory.create(kind, spec, entry, BODY, false, NO_SUMMONER).getState());
      }
    }
  }

  @Test
  void should_multiply_the_hp_and_attack_of_an_enemy_on_a_deeper_floor() {
    Enemy rat = create(new EnemyFactory(new World(), 2f), RAT);

    assertEquals(6, rat.getHp());
    assertEquals(6, rat.getMaxHp());
    assertEquals(2, rat.getAttack());
  }

  @Test
  void should_keep_the_other_stats_of_an_enemy_on_a_deeper_floor() {
    Enemy rat = create(new EnemyFactory(new World(), 2f), RAT);

    assertEquals(1, rat.getDefense());
    assertEquals(90f, rat.getSpeed());
    assertEquals(1f, rat.getAttackSpeed());
  }

  @Test
  void should_round_the_multiplied_stats() {
    Enemy rat = create(new EnemyFactory(new World(), 1.5f), RAT); // 3 HP x 1.5 = 4.5

    assertEquals(5, rat.getHp());
  }

  @Test
  void should_reject_a_stat_multiplier_that_is_not_above_zero() {
    assertThrows(IllegalArgumentException.class, () -> new EnemyFactory(new World(), 0f));
  }

  /** Reads the entry with the real reader, then builds it on {@link #BODY}. */
  private Enemy create(String json) {
    return create(factory, json);
  }

  private static Enemy create(EnemyFactory builder, String json) {
    JsonValue entry = new JsonReader().parse(json);
    CharacterSpec spec = CharacterSpecReader.read("rat", entry);
    return builder.create((EnemyKind) spec.kind(), spec, entry, BODY, false, NO_SUMMONER);
  }
}
