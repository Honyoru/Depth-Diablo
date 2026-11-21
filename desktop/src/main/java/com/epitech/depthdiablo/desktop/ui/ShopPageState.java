package com.epitech.depthdiablo.desktop.ui;

import com.epitech.depthdiablo.shop.Shopkeeper;
import java.util.Objects;
import java.util.Optional;

/**
 * Pure-Java model of the shop page (GDD 1.4): whether it is open, and the shopkeeper it trades
 * with. The game is paused while it is open. Leaving the page keeps the shop open: the next round
 * starts only when the player asks for it (GDD 1.2).
 */
public final class ShopPageState {
  private Optional<Shopkeeper> shopkeeper = Optional.empty();

  /**
   * Tells whether the page is open.
   *
   * @return true while the player trades
   */
  public boolean isOpen() {
    return shopkeeper.isPresent();
  }

  /**
   * Opens the page on the shopkeeper the player talks to.
   *
   * @param keeper the shopkeeper
   * @throws NullPointerException if {@code keeper} is {@code null}
   */
  public void open(Shopkeeper keeper) {
    shopkeeper = Optional.of(Objects.requireNonNull(keeper, "keeper is null"));
  }

  /** Closes the page; the shopkeeper keeps trading until the next round starts. */
  public void close() {
    shopkeeper = Optional.empty();
  }

  /**
   * Starts the next round through the shopkeeper, then closes the page.
   *
   * @return true if the next round started; false if the page was closed
   */
  public boolean startNextRound() {
    boolean started = shopkeeper.map(Shopkeeper::startNextRound).orElse(false);
    close();
    return started;
  }
}
