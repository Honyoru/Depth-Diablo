package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.entity.Stat;
import com.epitech.depthdiablo.entity.StatSource;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A passive item that adds a flat amount to one stat of its holder (GDD 1.3.1, 1.4). Every stat
 * ring is this class with different data (Q93).
 */
public final class StatRing extends PassiveItem {
  private final Stat stat;
  private final float amount;

  /**
   * Creates a stat ring.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param price its shop price in coins; 0 means free
   * @param stat the stat it raises
   * @param amount what it adds to that stat; above 0 and finite
   * @throws NullPointerException if a text or the stat is {@code null}
   * @throws IllegalArgumentException if a text is blank, the price is negative or the amount is not
   *     a finite number above 0
   */
  public StatRing(String id, String nameKey, String spriteId, int price, Stat stat, float amount) {
    super(id, nameKey, spriteId, price);
    this.stat = Objects.requireNonNull(stat, "stat is null");
    if (!(amount > 0) || Float.isInfinite(amount)) {
      throw new IllegalArgumentException("Amount must be a finite number above 0: " + amount);
    }
    this.amount = amount;
  }

  /**
   * Adds the amount to its stat; the other stats are kept.
   *
   * @param inner the stats so far
   * @return the stats with this ring
   * @throws NullPointerException if {@code inner} is {@code null}
   */
  @Override
  public StatSource decorateStats(StatSource inner) {
    Objects.requireNonNull(inner, "inner is null");
    return s -> s == stat ? inner.get(s) + amount : inner.get(s);
  }

  /**
   * Returns the stat this ring raises and by how much.
   *
   * @return {@code ring.<stat>}, e.g. {@code ring.max_hp}
   */
  @Override
  public List<ItemEffect> getEffects() {
    return List.of(new ItemEffect("ring." + stat.name().toLowerCase(Locale.ROOT), amount));
  }
}
