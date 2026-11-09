package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Player;
import com.epitech.depthdiablo.item.Item;

public record ItemPickedUp(Player source, Item item) implements GameEvent {
  @Override
  public Player source() {
    return source;
  }
}
