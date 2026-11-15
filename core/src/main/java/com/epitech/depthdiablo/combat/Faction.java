package com.epitech.depthdiablo.combat;

import java.util.Objects;

/**
 * The side a character fights for (GDD 1.5). An attack never hurts its own side, so enemies never
 * hit each other; a trap belongs to no side and hurts everyone. A status effect such as charm can
 * change a character's faction while it lasts.
 */
public enum Faction {
  /** The player, and later anything fighting for the player (a charmed enemy). */
  PLAYER,
  /** Every enemy and boss. */
  ENEMY,
  /** No side: traps and other hazards, which hurt everyone. */
  NEUTRAL;

  /**
   * Tells whether an attack from this faction can hurt a target of the given faction. A neutral
   * attack hurts everyone; any other attack hurts every faction except its own.
   *
   * @param target the faction of the one that could be hit
   * @return true if the attack can hurt the target
   * @throws NullPointerException if {@code target} is {@code null}
   */
  public boolean canHurt(Faction target) {
    Objects.requireNonNull(target, "The faction of the target must not be null");
    return this == NEUTRAL || this != target;
  }
}
