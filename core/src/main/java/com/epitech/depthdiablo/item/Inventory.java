package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.entity.Character;
import java.util.Objects;
import java.util.Optional;

/**
 * The player's 20 slots (GDD 1.4): slots 0 to 2 are the consumable hotbar, the 17 others take any
 * item. One item per slot, no stacking; when every fitting slot is taken, a pickup fails.
 */
public final class Inventory {
  /** Total number of slots (GDD 1.4). */
  public static final int CAPACITY = 20;

  /** Number of hotbar slots, at the start of the inventory; they hold consumables only. */
  public static final int HOTBAR_SIZE = 3;

  private final Item[] slots = new Item[CAPACITY];

  /**
   * Puts an item in the first free slot that accepts it. A consumable goes to the hotbar first,
   * then to storage; any other item goes to storage only.
   *
   * @param item the item to add
   * @return false if no fitting slot is free; the item then stays on the ground
   */
  public boolean add(Item item) {
    Objects.requireNonNull(item, "item");
    int first = item instanceof Consumable ? 0 : HOTBAR_SIZE;
    for (int slot = first; slot < CAPACITY; slot++) {
      if (slots[slot] == null) {
        slots[slot] = item;
        return true;
      }
    }
    return false;
  }

  /**
   * Uses the consumable in a hotbar slot. An empty slot does nothing.
   *
   * @param hotbarIndex the hotbar slot, from 0 to {@link #HOTBAR_SIZE} - 1
   * @param user who uses the item
   * @throws IndexOutOfBoundsException if the index is not a hotbar slot
   */
  @SuppressWarnings("PMD.NullAssignment") // null marks a free slot
  public void use(int hotbarIndex, Character user) {
    Objects.checkIndex(hotbarIndex, HOTBAR_SIZE);
    if (slots[hotbarIndex] instanceof Consumable consumable && consumable.use(user)) {
      slots[hotbarIndex] = null;
    }
  }

  /**
   * Returns the item in a slot.
   *
   * @param slot the slot, from 0 to {@link #CAPACITY} - 1
   * @return the item, or empty if the slot is free
   * @throws IndexOutOfBoundsException if the slot does not exist
   */
  public Optional<Item> item(int slot) {
    Objects.checkIndex(slot, CAPACITY);
    return Optional.ofNullable(slots[slot]);
  }

  /**
   * Tells whether every slot is taken.
   *
   * @return true if no slot is free
   */
  public boolean isFull() {
    for (Item item : slots) {
      if (item == null) {
        return false;
      }
    }
    return true;
  }
}
