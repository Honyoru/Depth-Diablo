package com.epitech.depthdiablo.desktop.ui;

import java.util.Objects;

/**
 * A slot of the inventory page.
 *
 * @param kind what the slot holds
 * @param index its place among the slots of its kind; 0 for the weapon and the armor
 */
public record PageSlot(Kind kind, int index) {

  /** What a slot of the page holds. */
  public enum Kind {
    /** The equipped weapon. */
    WEAPON,
    /** The worn armor. */
    ARMOR,
    /** One of the 3 consumable slots. */
    HOTBAR,
    /** One of the 18 passive slots. */
    PASSIVE
  }

  /**
   * Checks the kind.
   *
   * @throws NullPointerException if {@code kind} is null
   */
  public PageSlot {
    Objects.requireNonNull(kind, "kind is null");
  }

  /**
   * Tells whether the slot holds a passive item, the only kind that can be switched.
   *
   * @return true for a passive slot
   */
  public boolean isPassive() {
    return kind == Kind.PASSIVE;
  }
}
