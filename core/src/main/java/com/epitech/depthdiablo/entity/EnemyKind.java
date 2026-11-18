package com.epitech.depthdiablo.entity;

/**
 * The class an entry of {@code data/enemies.json} builds, read from its {@code "class"} field
 * (Q23). Several ids can share one kind: they then only differ by their values (Type Object).
 */
public enum EnemyKind {
  /** Built as a {@link Rat}. */
  RAT,
  /** Built as a {@link Zombie}. */
  ZOMBIE
}
