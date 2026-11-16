package com.epitech.depthdiablo.item;

import java.util.Objects;
import java.util.Optional;

/**
 * The player's bag (GDD 1.4): 3 hotbar slots that take only consumables, and the equipped weapon.
 * Each slot holds one item; items never stack. The player always holds a weapon.
 */
public final class Inventory {
  /** The number of hotbar slots; they take only consumables. */
  public static final int HOTBAR_SIZE = 3;

  private final Consumable[] hotbar = new Consumable[HOTBAR_SIZE];
  private Weapon weapon;

  /**
   * Creates an inventory with an empty hotbar.
   *
   * @param startingWeapon the weapon the player starts with
   * @throws NullPointerException if {@code startingWeapon} is {@code null}
   */
  public Inventory(Weapon startingWeapon) {
    this.weapon = Objects.requireNonNull(startingWeapon, "startingWeapon is null");
  }

  /**
   * Puts a consumable in the first free hotbar slot.
   *
   * @param consumable the consumable picked up
   * @return true if it was put in a slot; false if the hotbar is full, so it stays on the ground
   * @throws NullPointerException if {@code consumable} is {@code null}
   */
  public boolean add(Consumable consumable) {
    Objects.requireNonNull(consumable, "consumable is null");
    for (int i = 0; i < HOTBAR_SIZE; i++) {
      if (hotbar[i] == null) {
        hotbar[i] = consumable;
        return true;
      }
    }
    return false;
  }

  /**
   * Returns what a hotbar slot holds.
   *
   * @param index the slot, from 0 to {@link #HOTBAR_SIZE} - 1
   * @return the consumable in that slot, or empty if the slot is free
   * @throws IndexOutOfBoundsException if {@code index} is outside 0 to {@link #HOTBAR_SIZE} - 1
   */
  public Optional<Consumable> hotbarSlot(int index) {
    Objects.checkIndex(index, HOTBAR_SIZE);
    return Optional.ofNullable(hotbar[index]);
  }

  /**
   * Returns the equipped weapon.
   *
   * @return the weapon on the portrait; never null
   */
  public Weapon weapon() {
    return weapon;
  }

  /**
   * Swaps the equipped weapon for a new one (GDD 1.4). The player always holds a weapon, so the old
   * one is given back for the world to drop on the ground.
   *
   * @param newWeapon the weapon to equip
   * @return the weapon that was equipped before
   * @throws NullPointerException if {@code newWeapon} is {@code null}
   */
  public Weapon equip(Weapon newWeapon) {
    Objects.requireNonNull(newWeapon, "newWeapon is null");
    Weapon old = weapon;
    weapon = newWeapon;
    return old;
  }
}
