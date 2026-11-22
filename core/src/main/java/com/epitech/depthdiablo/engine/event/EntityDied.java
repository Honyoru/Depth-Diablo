package com.epitech.depthdiablo.engine.event;

import com.epitech.depthdiablo.entity.character.Character;
import java.util.Optional;

/**
 * A character died (GDD 1.1.2, F2 shows it): the renderer plays its death, the sound plays, the
 * loot may drop.
 *
 * @param source the character that died
 * @param killer the character that killed it, or nothing when no character did
 */
public record EntityDied(Character source, Optional<Character> killer) implements GameEvent {
  @Override
  public Character source() {
    return source;
  }
}
