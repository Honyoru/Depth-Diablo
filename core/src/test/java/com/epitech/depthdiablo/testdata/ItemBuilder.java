package com.epitech.depthdiablo.testdata;

import com.epitech.depthdiablo.item.Item;

/** Test Data Builder for items (Q116): {@code anItem().build()}. */
public final class ItemBuilder {
  private ItemBuilder() {}

  /**
   * Starts an item with a placeholder id.
   *
   * @return a new builder
   */
  public static ItemBuilder anItem() {
    return new ItemBuilder();
  }

  /**
   * Builds the item.
   *
   * @return an item with no effect of its own
   */
  public Item build() {
    return new PlainItem();
  }

  /** Hand-written fake (Q117): an item with no effect of its own. */
  private static final class PlainItem extends Item {
    PlainItem() {
      super("test-item");
    }
  }
}
