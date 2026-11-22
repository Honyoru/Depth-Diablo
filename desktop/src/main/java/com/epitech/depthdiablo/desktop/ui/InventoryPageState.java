package com.epitech.depthdiablo.desktop.ui;

import java.util.Objects;
import java.util.Optional;

/**
 * Pure-Java model of the inventory page (GDD 1.4, Q58): whether it is open, which slot is selected
 * and whether a Switch is pending. The game is paused while it is open; closing it cancels the
 * selection and a pending Switch.
 */
public final class InventoryPageState {
  private boolean open;

  /** The selected slot; empty when nothing is selected. */
  private Optional<PageSlot> selected = Optional.empty();

  private boolean switching;

  /** Creates the inventory page state: closed, nothing selected, no Switch pending. */
  public InventoryPageState() {
    // its fields start with their declared values
  }

  /**
   * Tells whether the page is open, so the game is paused.
   *
   * @return true while the page is shown
   */
  public boolean isOpen() {
    return open;
  }

  /**
   * Returns the slot whose item is selected, shown in the details panel.
   *
   * @return the selected slot, or nothing
   */
  public Optional<PageSlot> getSelected() {
    return selected;
  }

  /**
   * Tells whether a Switch is pending: the next passive slot clicked receives the selected item.
   *
   * @return true while a Switch waits for its slot
   */
  public boolean isSwitching() {
    return switching;
  }

  /** Opens the page if it is closed, closes it otherwise (key I). */
  public void toggle() {
    if (open) {
      close();
    } else {
      open = true;
    }
  }

  /** Closes the page and cancels the selection and a pending Switch. */
  public void close() {
    open = false;
    selected = Optional.empty();
    switching = false;
  }

  /**
   * A click on a slot. During a Switch, a passive slot completes it and any other slot cancels it;
   * otherwise the clicked item is selected, and a free slot selects nothing.
   *
   * @param slot the slot clicked
   * @param free true if the slot holds no item
   * @return the swap to do when a Switch is completed, or nothing
   * @throws NullPointerException if {@code slot} is null
   */
  public Optional<PassiveSwap> click(PageSlot slot, boolean free) {
    Objects.requireNonNull(slot, "slot is null");
    if (switching) {
      switching = false;
      Optional<PageSlot> from = selected;
      selected = Optional.empty();
      return from.filter(start -> slot.isPassive())
          .map(start -> new PassiveSwap(start.index(), slot.index()));
    }
    selected = free ? Optional.empty() : Optional.of(slot);
    return Optional.empty();
  }

  /**
   * Starts a Switch of the selected passive item, or cancels the pending one (the item stays
   * selected); does nothing for any other selection.
   */
  public void toggleSwitch() {
    switching = !switching && selected.filter(PageSlot::isPassive).isPresent();
  }
}
