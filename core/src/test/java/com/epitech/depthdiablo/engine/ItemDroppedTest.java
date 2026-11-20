package com.epitech.depthdiablo.engine;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.item.Potion;
import com.epitech.depthdiablo.testdata.Items;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.4: an item falls on the ground; the ItemDropper puts it there. */
class ItemDroppedTest {

  @Test
  void should_keep_who_dropped_what_and_where() {
    Character source = aCharacter().build();
    Potion potion = Items.potion();

    ItemDropped event = new ItemDropped(source, potion, new Vector2(10, 20));

    assertSame(source, event.source());
    assertSame(potion, event.item());
    assertEquals(new Vector2(10, 20), event.at());
  }

  @Test
  void should_not_follow_the_given_point_when_it_changes_later() {
    Vector2 at = new Vector2(10, 20);
    ItemDropped event = new ItemDropped(aCharacter().build(), Items.potion(), at);

    at.set(99, 99);

    assertEquals(new Vector2(10, 20), event.at());
  }

  @Test
  void should_have_no_receiver() {
    assertEquals(
        Optional.empty(),
        new ItemDropped(aCharacter().build(), Items.potion(), new Vector2()).receiver());
  }

  @Test
  void should_throw_when_the_item_is_null() {
    Character source = aCharacter().build();
    Vector2 at = new Vector2();

    assertThrows(NullPointerException.class, () -> new ItemDropped(source, null, at));
  }
}
