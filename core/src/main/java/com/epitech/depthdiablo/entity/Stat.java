package com.epitech.depthdiablo.entity;

/**
 * The stats shared by the player and the enemies (GDD 1.6, Q44, Q45). A {@code StatSource} gives
 * each one a value; passive items change them by wrapping it (Decorator, GDD 1.3.1).
 */
public enum Stat {
  /** Health when full; at least 1. */
  MAX_HP,
  /** Base damage of a hit, the attack in the damage formula; 0 or more. */
  ATTACK,
  /** Divisor of the damage taken, the resistance in the damage formula; at least 1. */
  DEFENSE,
  /** Movement speed in pixels per second; 0 or more. */
  SPEED,
  /** Attacks per second; 0 or more. */
  ATTACK_SPEED,
  /** Drop chances and roulette odds (GDD 1.6, TBD); 0 or more. */
  LUCK
}
