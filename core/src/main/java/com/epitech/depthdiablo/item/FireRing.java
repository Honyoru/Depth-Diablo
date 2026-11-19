package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.combat.BurnSpec;
import com.epitech.depthdiablo.combat.FireTouch;
import java.util.List;
import java.util.Objects;

/**
 * A passive item that makes every landed hit of its holder burn the target (GDD 1.3.1, 1.4.1). It
 * wraps the holder's attack in a {@link FireTouch} and leaves the stats alone.
 */
public final class FireRing extends PassiveItem {

  private final BurnSpec burn;

  /**
   * Creates a fire ring. The burn comes from {@code data/items.json} (Q93), so rings of different
   * strengths are the same class with different data.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param price its shop price in coins; 0 means free
   * @param burn the burn each landed hit applies
   * @throws NullPointerException if a text or {@code burn} is {@code null}
   * @throws IllegalArgumentException if a text is blank or the price is negative
   */
  public FireRing(String id, String nameKey, String spriteId, int price, BurnSpec burn) {
    super(id, nameKey, spriteId, price);
    this.burn = Objects.requireNonNull(burn, "The burn spec of a fire ring must not be null");
  }

  /**
   * Wraps the attack so that every landed hit burns the target.
   *
   * @param inner the attack so far
   * @return a fire touch around {@code inner}
   * @throws NullPointerException if {@code inner} is {@code null}
   */
  @Override
  public Attack decorateAttack(Attack inner) {
    return new FireTouch(Objects.requireNonNull(inner, "inner is null"), burn);
  }

  /**
   * Returns the burn this ring applies.
   *
   * @return its burn values
   */
  public BurnSpec getBurn() {
    return burn;
  }

  /**
   * Returns the burn each landed hit applies, its times in ticks (60 per second).
   *
   * @return {@code effect.burn_damage}, {@code effect.burn_interval_ticks} then {@code
   *     effect.burn_duration_ticks}
   */
  @Override
  public List<ItemEffect> getEffects() {
    return List.of(
        new ItemEffect("effect.burn_damage", burn.damage()),
        new ItemEffect("effect.burn_interval_ticks", burn.intervalTicks()),
        new ItemEffect("effect.burn_duration_ticks", burn.durationTicks()));
  }
}
