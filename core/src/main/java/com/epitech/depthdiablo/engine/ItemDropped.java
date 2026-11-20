package com.epitech.depthdiablo.engine;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.item.Item;
import java.util.Objects;

/**
 * An item falls on the ground (GDD 1.4): the player left it (a swapped weapon, a full bag, the Drop
 * button) or a monster dropped it. The {@link com.epitech.depthdiablo.entity.ItemDropper} puts it
 * in the world.
 *
 * @param source the character it falls from
 * @param item the item that falls
 * @param at where it lands, the centre of its spot in pixels; copied, never changed
 */
public record ItemDropped(Character source, Item item, Vector2 at) implements GameEvent {

  /**
   * Checks the values and copies the point, so the event does not follow it.
   *
   * @throws NullPointerException if a value is null
   */
  public ItemDropped {
    Objects.requireNonNull(source, "source is null");
    Objects.requireNonNull(item, "item is null");
    at = new Vector2(Objects.requireNonNull(at, "at is null"));
  }

  /**
   * Returns the character the item falls from.
   *
   * @return the source
   */
  @Override
  public Character source() {
    return source;
  }

  /**
   * Returns where the item lands, as a copy so the event cannot be changed.
   *
   * @return the centre of its spot, in pixels
   */
  @Override
  public Vector2 at() {
    return new Vector2(at);
  }
}
