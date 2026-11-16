package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.entity.BuildModifier;
import com.epitech.depthdiablo.entity.Character;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The player's bag (GDD 1.4): 3 hotbar slots that take only consumables, and the equipped weapon.
 * Each slot holds one item; items never stack. The player always holds a weapon.
 */
public final class Inventory {
  /** The number of hotbar slots; they take only consumables. */
  public static final int HOTBAR_SIZE = 3;

  /**
   * What a free hotbar slot holds (Null Object): it is drawn as an empty picture and using it does
   * nothing.
   */
  public static final Consumable EMPTY =
      new Consumable("empty", "item.empty.name", "items/empty", 0) {
        @Override
        public boolean use(Character user) {
          return false;
        }
      };

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
    Arrays.fill(hotbar, EMPTY);
  }

  /**
   * Puts a consumable in the first free hotbar slot.
   *
   * @param consumable the consumable picked up
   * @return true if it was put in a slot; false if the hotbar is full, so it stays on the ground
   * @throws NullPointerException if {@code consumable} is {@code null}
   * @throws IllegalArgumentException if {@code consumable} is {@link #EMPTY}
   */
  public boolean add(Consumable consumable) {
    Objects.requireNonNull(consumable, "consumable is null");
    if (EMPTY.equals(consumable)) {
      throw new IllegalArgumentException("EMPTY marks a free slot; it cannot be added");
    }
    for (int i = 0; i < HOTBAR_SIZE; i++) {
      if (EMPTY.equals(hotbar[i])) {
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
   * @return the consumable in that slot, or {@link #EMPTY} if the slot is free
   * @throws IndexOutOfBoundsException if {@code index} is outside 0 to {@link #HOTBAR_SIZE} - 1
   */
  public Consumable hotbarSlot(int index) {
    Objects.checkIndex(index, HOTBAR_SIZE);
    return hotbar[index];
  }

  /**
   * Uses the consumable of a hotbar slot (GDD 1.4, Q37). If it is spent, its slot is emptied; using
   * an empty slot does nothing.
   *
   * @param hotbarIndex the slot, from 0 to {@link #HOTBAR_SIZE} - 1
   * @param user the character who uses it
   * @return true if a consumable was spent; false if the slot was empty or the consumable was kept
   * @throws NullPointerException if {@code user} is {@code null}
   * @throws IndexOutOfBoundsException if {@code hotbarIndex} is outside 0 to {@link #HOTBAR_SIZE} -
   *     1
   */
  public boolean use(int hotbarIndex, Character user) {
    Objects.checkIndex(hotbarIndex, HOTBAR_SIZE);
    Objects.requireNonNull(user, "user is null");
    if (!hotbar[hotbarIndex].use(user)) {
      return false;
    }
    hotbar[hotbarIndex] = EMPTY;
    return true;
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

  /**
   * Returns what changes the holder's build, in the order the character applies them (GDD 1.3.1):
   * the weapon, then the armor, then the passive items in pickup order. Armor and passive items
   * join this list when they exist.
   *
   * @return the modifiers in order; the list cannot be changed
   */
  public List<BuildModifier> modifiers() {
    return List.of(weapon);
  }
}
