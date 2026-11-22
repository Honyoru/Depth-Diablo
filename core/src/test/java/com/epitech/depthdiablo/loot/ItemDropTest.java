package com.epitech.depthdiablo.loot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.item.Potion;
import com.epitech.depthdiablo.testdata.Items;
import org.junit.jupiter.api.Test;

/** GDD 1.4: an item lies on the ground until the player picks it up, or until it vanishes. */
class ItemDropTest {

  @Test
  void should_hold_its_item() {
    Potion potion = Items.potion();

    assertSame(potion, new ItemDrop(potion, new Vector2(100, 50)).getItem());
  }

  @Test
  void should_centre_its_box_on_the_given_point() {
    ItemDrop drop = new ItemDrop(Items.potion(), new Vector2(100, 50));

    assertEquals(new Aabb(92, 42, ItemDrop.SIZE, ItemDrop.SIZE), drop.getBodyBox());
  }

  @Test
  void should_let_characters_walk_over_it() {
    assertFalse(new ItemDrop(Items.potion(), new Vector2()).blocks());
  }

  @Test
  void should_stay_where_it_lies_over_time() {
    ItemDrop drop = new ItemDrop(Items.potion(), new Vector2(100, 50));

    drop.update(1);

    assertEquals(new Vector2(92, 42), drop.position());
  }

  @Test
  void should_still_lie_there_just_before_its_lifetime_ends() {
    World world = new World();
    ItemDrop drop = aDropIn(world);

    ticks(world, ItemDrop.LIFETIME_TICKS - 1);

    assertFalse(drop.isRemoved());
    assertTrue(world.getEntities().contains(drop));
  }

  @Test
  void should_vanish_once_its_lifetime_is_over() {
    World world = new World();
    ItemDrop drop = aDropIn(world);

    ticks(world, ItemDrop.LIFETIME_TICKS);

    assertTrue(drop.isRemoved());
    assertFalse(world.getEntities().contains(drop));
  }

  @Test
  void should_get_its_time_back_when_the_world_is_rewound() {
    World world = new World();
    ItemDrop drop = aDropIn(world);
    StateBuffer photo = new StateBuffer();
    world.writeState(photo);
    ticks(world, ItemDrop.LIFETIME_TICKS);

    photo.startReading();
    world.readState(photo);

    assertFalse(drop.isRemoved());
    assertEquals(ItemDrop.LIFETIME_TICKS, drop.getTicksLeft());
  }

  @Test
  void should_throw_when_the_item_is_null() {
    Vector2 centre = new Vector2();

    assertThrows(NullPointerException.class, () -> new ItemDrop(null, centre));
  }

  @Test
  void should_throw_when_the_centre_is_null() {
    Potion potion = Items.potion();

    assertThrows(NullPointerException.class, () -> new ItemDrop(potion, null));
  }

  private static ItemDrop aDropIn(World world) {
    ItemDrop drop = new ItemDrop(Items.potion(), new Vector2(100, 50));
    world.add(drop);
    return drop;
  }

  private static void ticks(World world, int count) {
    for (int i = 0; i < count; i++) {
      world.tick();
    }
  }
}
