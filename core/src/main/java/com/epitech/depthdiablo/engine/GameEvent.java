package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Entity;
import java.util.Optional;

/**
 * Something that happened in the game. Sealed, so a switch over events is checked by the compiler.
 * The event list is still open (Q24).
 */
public sealed interface GameEvent
    permits AttackStarted, DamageDealt, EntityDied, ItemDropped, ItemPickedUp, ShopOpened {
  /**
   * Returns the emitter, where the event laser (F2) starts its line.
   *
   * @return the entity that emitted this event
   */
  Entity source();

  /**
   * Returns the receiver, where the event laser (F2) ends its line.
   *
   * @return the entity the event is aimed at, or empty when it has none
   */
  default Optional<Entity> receiver() {
    return Optional.empty();
  }
}
