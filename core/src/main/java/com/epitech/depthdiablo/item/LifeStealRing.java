package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.combat.LifeSteal;
import java.util.List;
import java.util.Objects;

/** A passive item that heals its holder by a part of the damage it deals (GDD 1.3.1). */
public final class LifeStealRing extends PassiveItem {
  private final float fraction;

  /**
   * Creates a life steal ring; {@link LifeSteal} checks the fraction when the attack is wrapped.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param price its shop price in coins; 0 means free
   * @param fraction the part of the damage healed, above 0 and at most 1
   * @throws IllegalArgumentException if a text is blank, the price is negative or the fraction is
   *     outside (0, 1]
   */
  public LifeStealRing(String id, String nameKey, String spriteId, int price, float fraction) {
    super(id, nameKey, spriteId, price);
    if (!(fraction > 0 && fraction <= 1)) {
      throw new IllegalArgumentException("Life steal fraction must be in (0, 1]: " + fraction);
    }
    this.fraction = fraction;
  }

  /**
   * Wraps the attack in a life steal.
   *
   * @param inner the attack so far
   * @return a life steal around {@code inner}
   */
  @Override
  public Attack decorateAttack(Attack inner) {
    return new LifeSteal(Objects.requireNonNull(inner, "inner is null"), fraction);
  }

  /**
   * Returns the part of the damage healed, in percent.
   *
   * @return {@code effect.life_steal}
   */
  @Override
  public List<ItemEffect> getEffects() {
    return List.of(new ItemEffect("effect.life_steal", fraction * 100));
  }
}
