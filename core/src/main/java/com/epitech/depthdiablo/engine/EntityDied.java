package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Character;
import java.util.Optional;

public record EntityDied(Character source, Optional<Character> killer) implements GameEvent {
  @Override
  public Character source() {
    return source;
  }
}
