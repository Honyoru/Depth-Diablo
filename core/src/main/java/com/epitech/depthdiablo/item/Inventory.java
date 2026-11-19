package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.entity.BuildModifier;
import com.epitech.depthdiablo.entity.Character;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The player's bag (GDD 1.4): 3 hotbar slots that take only consumables, 18 slots that take only
 * passive items, the equipped weapon and the worn armor. Each slot holds one item; items never
 * stack. The player always holds a weapon and wears an armor.
 */
public final class Inventory {

  /** The number of hotbar slots; they take only consumables. */
  public static final int HOTBAR_SIZE = 3;

  /**
   * What a free hotbar slot holds (Null Object): it is drawn as an empty picture and using it does
   * nothing.
   */
  public static final Consumable EMPTY_CONSUMABLE_SLOT =
      new Consumable("empty", "item.empty.name", "items/empty", 0) {
        @Override
        public boolean use(Character user) {
          return false;
        }
      };

  /** The number of slots that take only passive items. */
  public static final int PASSIVE_SIZE = 18;

  /**
   * What a free passive slot holds (Null Object): it is drawn as an empty picture and changes
   * nothing in the build.
   */
  public static final PassiveItem EMPTY_PASSIVE_SLOT =
      new PassiveItem("empty_passive", "item.empty.name", "items/empty", 0) {};

  private final Consumable[] hotbar = new Consumable[HOTBAR_SIZE];

  private final PassiveItem[] passives = new PassiveItem[PASSIVE_SIZE];

  private Weapon weapon;

  private Armor armor;

  /**
   * Creates an inventory with an empty hotbar.
   *
   * @param startingWeapon the weapon the player starts with
   * @param startingArmor the armor the player starts with
   * @throws NullPointerException if {@code startingWeapon} or {@code startingArmor} is {@code null}
   */
  public Inventory(Weapon startingWeapon, Armor startingArmor) {
    this.weapon = Objects.requireNonNull(startingWeapon, "startingWeapon is null");
    this.armor = Objects.requireNonNull(startingArmor, "startingArmor is null");
    Arrays.fill(hotbar, EMPTY_CONSUMABLE_SLOT);
    Arrays.fill(passives, EMPTY_PASSIVE_SLOT);
  }

  /**
   * Returns the equipped weapon.
   *
   * @return the weapon on the portrait; never null
   */
  public Weapon getWeapon() {
    return weapon;
  }

  /**
   * Returns the worn armor.
   *
   * @return the armor on the portrait; never null
   */
  public Armor getArmor() {
    return armor;
  }

  /**
   * Returns what changes the holder's build, in the order the character applies them (GDD 1.3.1):
   * the weapon, then the armor, then the passive items in slot order, so the player chooses their
   * order by swapping them.
   *
   * @return the modifiers in order; the list cannot be changed
   */
  public List<BuildModifier> getModifiers() {
    List<BuildModifier> modifiers = new ArrayList<>(List.of(weapon, armor));
    for (int i = 0; i < PASSIVE_SIZE; i++) {
      if (!isPassiveSlotFree(i)) {
        modifiers.add(passives[i]);
      }
    }
    return List.copyOf(modifiers);
  }

  /**
   * Puts a consumable in the first free hotbar slot.
   *
   * @param consumable the consumable picked up
   * @return true if it was put in a slot; false if the hotbar is full, so it stays on the ground
   * @throws NullPointerException if {@code consumable} is {@code null}
   * @throws IllegalArgumentException if {@code consumable} is {@link #EMPTY_CONSUMABLE_SLOT}
   */
  public boolean add(Consumable consumable) {
    Objects.requireNonNull(consumable, "consumable is null");
    if (EMPTY_CONSUMABLE_SLOT.equals(consumable)) {
      throw new IllegalArgumentException(
          "EMPTY_CONSUMABLE_SLOT marks a free slot; it cannot be added");
    }
    for (int i = 0; i < HOTBAR_SIZE; i++) {
      if (isHotbarSlotFree(i)) {
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
   * @return the consumable in that slot, or {@link #EMPTY_CONSUMABLE_SLOT} if the slot is free
   * @throws IndexOutOfBoundsException if {@code index} is outside 0 to {@link #HOTBAR_SIZE} - 1
   */
  public Consumable hotbarSlot(int index) {
    Objects.checkIndex(index, HOTBAR_SIZE);
    return hotbar[index];
  }

  /**
   * Tells whether a hotbar slot is free, so it holds {@link #EMPTY_CONSUMABLE_SLOT}.
   *
   * @param index the slot, from 0 to {@link #HOTBAR_SIZE} - 1
   * @return true if the slot holds no consumable
   * @throws IndexOutOfBoundsException if {@code index} is outside 0 to {@link #HOTBAR_SIZE} - 1
   */
  public boolean isHotbarSlotFree(int index) {
    return EMPTY_CONSUMABLE_SLOT.equals(hotbarSlot(index));
  }

  /**
   * Takes the consumable out of a hotbar slot, to drop it on the ground.
   *
   * @param index the slot, from 0 to {@link #HOTBAR_SIZE} - 1
   * @return the consumable that was in the slot, or nothing if the slot was free
   * @throws IndexOutOfBoundsException if {@code index} is outside 0 to {@link #HOTBAR_SIZE} - 1
   */
  public Optional<Consumable> removeConsumable(int index) {
    if (isHotbarSlotFree(index)) {
      return Optional.empty();
    }
    Consumable consumable = hotbar[index];
    hotbar[index] = EMPTY_CONSUMABLE_SLOT;
    return Optional.of(consumable);
  }

  /**
   * Puts a passive item in the first free passive slot.
   *
   * @param item the passive item picked up
   * @return true if it was put in a slot; false if the 18 slots are full, so it stays on the ground
   * @throws NullPointerException if {@code item} is {@code null}
   * @throws IllegalArgumentException if {@code item} is {@link #EMPTY_PASSIVE_SLOT}
   */
  public boolean add(PassiveItem item) {
    Objects.requireNonNull(item, "item is null");
    if (EMPTY_PASSIVE_SLOT.equals(item)) {
      throw new IllegalArgumentException(
          "EMPTY_PASSIVE_SLOT marks a free slot; it cannot be added");
    }
    for (int i = 0; i < PASSIVE_SIZE; i++) {
      if (isPassiveSlotFree(i)) {
        passives[i] = item;
        return true;
      }
    }
    return false;
  }

  /**
   * Returns what a passive slot holds.
   *
   * @param index the slot, from 0 to {@link #PASSIVE_SIZE} - 1
   * @return the passive item in that slot, or {@link #EMPTY_PASSIVE_SLOT} if the slot is free
   * @throws IndexOutOfBoundsException if {@code index} is outside 0 to {@link #PASSIVE_SIZE} - 1
   */
  public PassiveItem passiveSlot(int index) {
    Objects.checkIndex(index, PASSIVE_SIZE);
    return passives[index];
  }

  /**
   * Tells whether a passive slot is free, so it holds {@link #EMPTY_PASSIVE_SLOT}.
   *
   * @param index the slot, from 0 to {@link #PASSIVE_SIZE} - 1
   * @return true if the slot holds no passive item
   * @throws IndexOutOfBoundsException if {@code index} is outside 0 to {@link #PASSIVE_SIZE} - 1
   */
  public boolean isPassiveSlotFree(int index) {
    return EMPTY_PASSIVE_SLOT.equals(passiveSlot(index));
  }

  /**
   * Takes the passive item out of a slot, to drop it on the ground; its modifier is gone.
   *
   * @param index the slot, from 0 to {@link #PASSIVE_SIZE} - 1
   * @return the item that was in the slot, or nothing if the slot was free
   * @throws IndexOutOfBoundsException if {@code index} is outside 0 to {@link #PASSIVE_SIZE} - 1
   */
  public Optional<PassiveItem> removePassive(int index) {
    if (isPassiveSlotFree(index)) {
      return Optional.empty();
    }
    PassiveItem item = passives[index];
    passives[index] = EMPTY_PASSIVE_SLOT;
    return Optional.of(item);
  }

  /**
   * Exchanges what two passive slots hold; with a free slot, the item moves there. The passive
   * items apply in slot order, so swapping two of them changes the build.
   *
   * @param first one slot, from 0 to {@link #PASSIVE_SIZE} - 1
   * @param second the other slot, from 0 to {@link #PASSIVE_SIZE} - 1
   * @throws IndexOutOfBoundsException if a slot is outside 0 to {@link #PASSIVE_SIZE} - 1
   */
  public void swapPassiveSlots(int first, int second) {
    Objects.checkIndex(first, PASSIVE_SIZE);
    Objects.checkIndex(second, PASSIVE_SIZE);
    PassiveItem held = passives[first];
    passives[first] = passives[second];
    passives[second] = held;
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
    hotbar[hotbarIndex] = EMPTY_CONSUMABLE_SLOT;
    return true;
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
   * Swaps the worn armor for a new one (GDD 1.4). The player always wears an armor, so the old one
   * is given back for the world to drop on the ground.
   *
   * @param newArmor the armor to wear
   * @return the armor that was worn before
   * @throws NullPointerException if {@code newArmor} is {@code null}
   */
  public Armor equip(Armor newArmor) {
    Objects.requireNonNull(newArmor, "newArmor is null");
    Armor old = armor;
    armor = newArmor;
    return old;
  }
}
