package com.epitech.depthdiablo.testdata;

import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.item.Consumable;
import com.epitech.depthdiablo.item.Item;

/** Hand-written item fakes (Q117) for inventory tests. */
public final class FakeItems {
  private FakeItems() {}

  /**
   * Returns an item that is not a consumable, like a weapon or a passive item.
   *
   * @param id the item id
   * @return a new non-consumable item
   */
  public static Item anItem(String id) {
    return new PlainItem(id);
  }

  /**
   * Returns a consumable that records how often it was used.
   *
   * @param id the item id
   * @return a new consumable
   */
  public static CountingConsumable aConsumable(String id) {
    return new CountingConsumable(id);
  }

  /** A consumable whose effect is only to count its uses. */
  public static final class CountingConsumable extends Consumable {
    private int uses;

    CountingConsumable(String id) {
      super(id);
    }

    @Override
    public boolean use(Character user) {
      uses++;
      return true;
    }

    /**
     * Returns how often this consumable was used.
     *
     * @return the number of uses
     */
    public int uses() {
      return uses;
    }
  }

  private static final class PlainItem extends Item {
    PlainItem(String id) {
      super(id);
    }
  }
}
