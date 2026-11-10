package com.epitech.depthdiablo.item;

/**
 * How rare an item is (GDD 1.3.1). Loot tables use it to choose what drops.
 *
 * <p>{@link #CONSUMABLE} is a tier of its own: every consumable has it, so a drop can ask for "a
 * consumable" the same way it asks for "an epic item".
 */
public enum Rarity {
  /** The most frequent equipment. */
  COMMON,
  /** Less frequent equipment. */
  RARE,
  /** Uncommon, strong equipment. */
  EPIC,
  /** The rarest equipment. */
  LEGENDARY,
  /** Every consumable, such as potions and roulette tickets. */
  CONSUMABLE
}
