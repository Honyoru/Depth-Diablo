package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.item.Potion;
import com.epitech.depthdiablo.testdata.Items;
import org.junit.jupiter.api.Test;

/** GDD 1.4: an item lies on the ground until the player picks it up. */
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
  void should_throw_when_the_item_is_null() {
    Vector2 centre = new Vector2();

    assertThrows(NullPointerException.class, () -> new ItemDrop(null, centre));
  }

  @Test
  void should_throw_when_the_centre_is_null() {
    Potion potion = Items.potion();

    assertThrows(NullPointerException.class, () -> new ItemDrop(potion, null));
  }
}
