package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.Attack;

/**
 * The equipped weapon: the player's attack (GDD 1.3.1, 1.6). It adds its attack bonus to the attack
 * side of the damage formula and only hits targets within its range (Q37).
 *
 * <p>Pattern: Strategy. Each weapon is one {@link Attack}; the character calls it without knowing
 * which weapon it is, and equipping another weapon swaps the strategy.
 */
public abstract class Weapon extends Equipment implements Attack {
  private final int attackBonus;
  private final float range;

  /**
   * Creates a weapon.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE}
   * @param price its shop price in coins; 0 means free
   * @param attackBonus the flat bonus added to the attack in the damage formula; 0 or more
   * @param range how far its hit reaches, in pixels; above 0 and finite
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a text is blank, the price or the attack bonus is negative,
   *     the rarity is CONSUMABLE, or the range is not a finite number above 0
   */
  protected Weapon(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float range) {
    super(id, nameKey, spriteId, rarity, price);
    if (attackBonus < 0) {
      throw new IllegalArgumentException("Attack bonus must not be negative: " + attackBonus);
    }
    if (!(range > 0) || Float.isInfinite(range)) {
      throw new IllegalArgumentException("Range must be a finite number above 0: " + range);
    }
    this.attackBonus = attackBonus;
    this.range = range;
  }

  /**
   * Returns the flat bonus this weapon adds to the attack in the damage formula.
   *
   * @return the attack bonus, 0 or more
   */
  public final int attackBonus() {
    return attackBonus;
  }

  /**
   * Returns how far this weapon's hit reaches.
   *
   * @return the range in pixels, above 0
   */
  public final float range() {
    return range;
  }
}
