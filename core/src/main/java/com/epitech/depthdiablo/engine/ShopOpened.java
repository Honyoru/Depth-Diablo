package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.entity.Player;
import com.epitech.depthdiablo.shop.Shopkeeper;
import java.util.Objects;
import java.util.Optional;

/**
 * A player talked to the shopkeeper while he trades (GDD 1.4): the shop page opens on it.
 *
 * @param source the player who talks to him
 * @param shopkeeper the shopkeeper he talks to
 */
public record ShopOpened(Player source, Shopkeeper shopkeeper) implements GameEvent {

  /**
   * Checks the values.
   *
   * @throws NullPointerException if a value is null
   */
  public ShopOpened {
    Objects.requireNonNull(source, "source is null");
    Objects.requireNonNull(shopkeeper, "shopkeeper is null");
  }

  /**
   * Returns the shopkeeper, where the event laser (F2) ends its line.
   *
   * @return the shopkeeper
   */
  @Override
  public Optional<Entity> receiver() {
    return Optional.of(shopkeeper);
  }
}
