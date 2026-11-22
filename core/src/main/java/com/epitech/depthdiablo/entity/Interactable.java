package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.entity.character.Player;

/**
 * What the interact key (E) uses when no item lies in reach (GDD 2.4): the shopkeeper, later the
 * chests and the locked doors.
 */
public interface Interactable {

  /**
   * Reacts to a player who presses the interact key next to it.
   *
   * @param player the player who interacts
   * @throws NullPointerException if {@code player} is {@code null}
   */
  void interact(Player player);
}
