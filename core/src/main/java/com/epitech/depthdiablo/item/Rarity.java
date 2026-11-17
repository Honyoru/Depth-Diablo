package com.epitech.depthdiablo.item;

/**
 * How rare an item is (GDD 1.3.1). Loot tables use it to choose what drops.
 *
 * <p>{@link #CONSUMABLE} and {@link #PASSIVE} are tiers of their own: every consumable or passive
 * item has one, so a drop can ask for "a consumable" or a chest for "a passive item" the same way
 * it asks for "an epic item".
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
  CONSUMABLE,
  /** Every passive item, such as the fire ring; only chests hold them (GDD 1.4). */
  PASSIVE
}
