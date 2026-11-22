package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.entity.character.Character;

/**
 * An item used once from the hotbar, such as a potion (GDD 1.3.1, 1.4).
 *
 * <p>Pattern: Command. Each consumable is one action the player triggers.
 */
public abstract class Consumable extends Item {

  /**
   * Creates a consumable. Every consumable has the {@link Rarity#CONSUMABLE} rarity, so loot tables
   * can pick consumables by that tier.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param price its shop price in coins; 0 means free
   * @throws NullPointerException if a text is {@code null}
   * @throws IllegalArgumentException if a text is blank or the price is negative
   */
  protected Consumable(String id, String nameKey, String spriteId, int price) {
    super(id, nameKey, spriteId, Rarity.CONSUMABLE, price);
  }

  /**
   * Applies this consumable's effect. The user is who used it, not always who it affects.
   *
   * @param user the character who uses it
   * @return true if the consumable was spent and its hotbar slot must be emptied; false if nothing
   *     happened and the player keeps it
   * @throws NullPointerException if {@code user} is {@code null}
   */
  public abstract boolean use(Character user);
}
