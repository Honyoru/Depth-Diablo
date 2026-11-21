package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.combat.Execute;
import java.util.List;
import java.util.Objects;

/** A passive item whose hits finish targets left under a part of their max HP (GDD 1.3.1). */
public final class ExecutionerRing extends PassiveItem {
  private final float threshold;

  /**
   * Creates an executioner ring.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param price its shop price in coins; 0 means free
   * @param threshold the part of max HP under which a hit target dies, above 0 and below 1
   * @throws IllegalArgumentException if a text is blank, the price is negative or the threshold is
   *     outside (0, 1)
   */
  public ExecutionerRing(String id, String nameKey, String spriteId, int price, float threshold) {
    super(id, nameKey, spriteId, price);
    if (!(threshold > 0 && threshold < 1)) {
      throw new IllegalArgumentException("Execute threshold must be in (0, 1): " + threshold);
    }
    this.threshold = threshold;
  }

  /**
   * Wraps the attack in an execute.
   *
   * @param inner the attack so far
   * @return an execute around {@code inner}
   */
  @Override
  public Attack decorateAttack(Attack inner) {
    return new Execute(Objects.requireNonNull(inner, "inner is null"), threshold);
  }

  /**
   * Returns the HP threshold, in percent.
   *
   * @return {@code effect.execute}
   */
  @Override
  public List<ItemEffect> getEffects() {
    return List.of(new ItemEffect("effect.execute", threshold * 100));
  }
}
