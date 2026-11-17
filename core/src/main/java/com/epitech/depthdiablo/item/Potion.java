package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.entity.Character;
import java.util.Objects;

/** A consumable that restores HP to the character who drinks it (GDD 1.3.1, 1.4). */
public final class Potion extends Consumable {

  private final int healAmount;

  /**
   * Creates a potion. The heal amount comes from {@code data/items.json} (Q93), so potions of
   * different sizes are the same class with different data.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param price its shop price in coins; 0 means free
   * @param healAmount the HP restored when drunk; must be at least 1
   * @throws NullPointerException if a text is {@code null}
   * @throws IllegalArgumentException if a text is blank, the price is negative or {@code
   *     healAmount} is less than 1
   */
  public Potion(String id, String nameKey, String spriteId, int price, int healAmount) {
    super(id, nameKey, spriteId, price);
    if (healAmount < 1) {
      throw new IllegalArgumentException(
          "The heal amount of a potion must be at least 1, was " + healAmount);
    }
    this.healAmount = healAmount;
  }

  /**
   * Returns the HP this potion restores.
   *
   * @return the heal amount, at least 1
   */
  public int getHealAmount() {
    return healAmount;
  }

  /**
   * Heals the user by {@link #getHealAmount()}, up to their max HP. The potion is always spent,
   * even at full HP.
   *
   * @param user the character who drinks it
   * @return always true: the hotbar slot is emptied
   * @throws NullPointerException if {@code user} is {@code null}
   */
  @Override
  public boolean use(Character user) {
    Objects.requireNonNull(user, "The character drinking the potion must not be null");
    user.heal(healAmount);
    return true;
  }
}
