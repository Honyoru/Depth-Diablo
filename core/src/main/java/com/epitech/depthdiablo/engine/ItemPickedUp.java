package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.item.Item;
import java.util.Objects;

/**
 * A character takes an item off the ground and keeps it (GDD 1.4). Not published when the item does
 * not fit and falls back. The desktop plays the pickup sound on it (GDD 4.2.2).
 *
 * @param source the character that picks the item up
 * @param item the item taken off the ground
 */
public record ItemPickedUp(Character source, Item item) implements GameEvent {

  /**
   * Checks the values.
   *
   * @throws NullPointerException if a value is null
   */
  public ItemPickedUp {
    Objects.requireNonNull(source, "source is null");
    Objects.requireNonNull(item, "item is null");
  }

  /**
   * Returns the character that picks the item up.
   *
   * @return the source
   */
  @Override
  public Character source() {
    return source;
  }
}
