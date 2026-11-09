package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Character;

public record DamageDealt(Character source, Character target, int amount) implements GameEvent {
  @Override
  public Character source() {
    return source;
  }
}
