package com.epitech.depthdiablo.testdata;

import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Player;

/** Test Data Builder for players (Q116): {@code aPlayer().build()}. */
public final class PlayerBuilder {
  private PlayerBuilder() {}

  /**
   * Starts a player with 10 HP, a 16 x 16 hurtbox at the origin and no i-frames after a hit.
   *
   * @return a new builder
   */
  public static PlayerBuilder aPlayer() {
    return new PlayerBuilder();
  }

  /**
   * Builds the player.
   *
   * @return a player that does nothing on its own
   */
  public Player build() {
    return new IdlePlayer();
  }

  /** Hand-written fake (Q117): a player with no behaviour of its own. */
  private static final class IdlePlayer extends Player {
    IdlePlayer() {
      super(10, new Aabb(0, 0, 16, 16), 0);
    }

    @Override
    protected void think(long tick) {
      // An idle player never acts.
    }
  }
}
