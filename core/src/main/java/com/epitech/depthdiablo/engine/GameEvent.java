package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Entity;

/**
 * Something that happened in the game. Sealed, so a switch over events is checked by the compiler.
 * The event list is still open (Q24).
 */
public sealed interface GameEvent permits DamageDealt, EntityDied, ItemPickedUp, RoomCleared {
  /**
   * Returns the emitter, where the event laser (F2) starts its line.
   *
   * @return the entity that emitted this event
   */
  Entity source();
}
