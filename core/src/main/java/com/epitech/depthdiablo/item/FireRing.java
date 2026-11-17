package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.combat.BurnSpec;
import com.epitech.depthdiablo.combat.FireTouch;
import java.util.Objects;

/** A consumable that restores HP to the character who drinks it (GDD 1.3.1, 1.4). */
public final class FireRing extends PassiveItem {

  private final BurnSpec burn;

  /**
   * Creates a potion. The heal amount comes from {@code data/items.json} (Q93), so potions of
   * different sizes are the same class with different data.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param price its shop price in coins; 0 means free
   *     <p>healAmount} is less than 1
   */
  public FireRing(String id, String nameKey, String spriteId, int price, BurnSpec burn) {
    super(id, nameKey, spriteId, price);
    this.burn = Objects.requireNonNull(burn, "The burn spec of a fire ring must not be null");
  }

  @Override
  public Attack decorateAttack(Attack inner) {
    return new FireTouch(Objects.requireNonNull(inner, "inner is null"), burn);
  }

  public BurnSpec getBurn() {
    return burn;
  }
}
