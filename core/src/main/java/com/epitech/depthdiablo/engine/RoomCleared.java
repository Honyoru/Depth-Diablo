package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Enemy;

public record RoomCleared(Enemy source, int roomId) implements GameEvent {
  @Override
  public Enemy source() {
    return source;
  }
}
