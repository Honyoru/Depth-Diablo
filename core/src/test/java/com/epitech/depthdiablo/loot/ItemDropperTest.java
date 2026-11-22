package com.epitech.depthdiablo.loot;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.engine.event.EntityDied;
import com.epitech.depthdiablo.engine.event.ItemDropped;
import com.epitech.depthdiablo.entity.character.Character;
import com.epitech.depthdiablo.item.FireRing;
import com.epitech.depthdiablo.item.Potion;
import com.epitech.depthdiablo.testdata.Items;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.4: monsters, rooms and the player put items on the ground through one dropper. */
class ItemDropperTest {
  private final World world = new World();
  private final ItemDropper dropper = new ItemDropper(Items.factory(), world);

  @Test
  void should_put_the_given_item_on_the_ground() {
    Potion potion = Items.potion();

    ItemDrop drop = dropper.drop(potion, new Vector2(100, 50));

    assertSame(potion, drop.getItem());
    assertEquals(new Vector2(92, 42), drop.position());
  }

  @Test
  void should_add_the_dropped_item_to_the_world() {
    ItemDrop drop = dropper.drop(Items.potion(), new Vector2(100, 50));

    assertTrue(world.getEntities().contains(drop));
  }

  @Test
  void should_spawn_the_item_of_the_data_file_on_the_ground() {
    ItemDrop drop = dropper.spawn("fire_ring", new Vector2(100, 50));

    assertInstanceOf(FireRing.class, drop.getItem());
    assertTrue(world.getEntities().contains(drop));
  }

  @Test
  void should_throw_when_the_spawned_item_is_unknown() {
    Vector2 at = new Vector2();

    assertThrows(IllegalArgumentException.class, () -> dropper.spawn("dragon_egg", at));
  }

  @Test
  void should_throw_when_the_dropped_item_is_null() {
    Vector2 at = new Vector2();

    assertThrows(NullPointerException.class, () -> dropper.drop(null, at));
  }

  @Test
  void should_put_on_the_ground_the_item_of_a_dropped_event() {
    Potion potion = Items.potion();

    dropper.onEvent(new ItemDropped(aCharacter().build(), potion, new Vector2(100, 50)));

    assertTrue(
        world.getEntities().stream()
            .anyMatch(e -> e instanceof ItemDrop drop && drop.getItem() == potion));
  }

  @Test
  void should_ignore_the_other_events() {
    Character dead = aCharacter().build();

    dropper.onEvent(new EntityDied(dead, Optional.empty()));

    assertTrue(world.getEntities().isEmpty());
  }
}
