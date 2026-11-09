package com.epitech.depthdiablo.engine;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.anEnemy;
import static com.epitech.depthdiablo.testdata.ItemBuilder.anItem;
import static com.epitech.depthdiablo.testdata.PlayerBuilder.aPlayer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Enemy;
import com.epitech.depthdiablo.entity.Player;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.1.2: every event names its emitter, and a switch over events covers every type. */
class GameEventTest {
  private final Character character = aCharacter().build();

  @Test
  void should_return_the_attacker_when_a_damage_event_gives_its_source() {
    GameEvent event = new DamageDealt(character, aCharacter().build(), 1);

    assertSame(character, event.source());
  }

  @Test
  void should_return_the_victim_when_a_death_event_gives_its_source() {
    GameEvent event = new EntityDied(character, Optional.empty());

    assertSame(character, event.source());
  }

  @Test
  void should_return_the_player_when_a_pickup_event_gives_its_source() {
    Player player = aPlayer().build();
    GameEvent event = new ItemPickedUp(player, anItem().build());

    assertSame(player, event.source());
  }

  @Test
  void should_return_the_enemy_when_a_room_cleared_event_gives_its_source() {
    Enemy enemy = anEnemy().build();
    GameEvent event = new RoomCleared(enemy, 1);

    assertSame(enemy, event.source());
  }

  @Test
  void should_identify_each_type_when_a_switch_covers_every_event() {
    GameEvent damage = new DamageDealt(character, aCharacter().build(), 1);
    GameEvent death = new EntityDied(character, Optional.empty());
    GameEvent item = new ItemPickedUp(aPlayer().build(), anItem().build());
    GameEvent room = new RoomCleared(anEnemy().build(), 1);

    String damageKind = kindOf(damage);
    String deathKind = kindOf(death);
    String itemKind = kindOf(item);
    String roomKind = kindOf(room);

    assertEquals("damage", damageKind);
    assertEquals("death", deathKind);
    assertEquals("item", itemKind);
    assertEquals("room", roomKind);
  }

  /** No default branch: a new event type stops this compiling until it is handled here. */
  private static String kindOf(GameEvent event) {
    return switch (event) {
      case DamageDealt damage -> "damage";
      case EntityDied death -> "death";
      case ItemPickedUp item -> "item";
      case RoomCleared death -> "room";
    };
  }
}
