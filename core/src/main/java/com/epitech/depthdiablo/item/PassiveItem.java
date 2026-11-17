package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.entity.BuildModifier;
import com.epitech.depthdiablo.entity.CharacterBuild;
import com.epitech.depthdiablo.entity.StatSource;
import java.util.Objects;

/**
 * A permanent modifier the player holds, such as the fire ring (GDD 1.3.1, 1.4). It changes the
 * holder's build: its stats, its attack, or both.
 *
 * <p>Pattern: Decorator. Each passive wraps the stats or the attack it is given; the build applies
 * them in pickup order, so removing one and rebuilding restores the holder exactly.
 */
public abstract class PassiveItem extends Item implements BuildModifier {

  /**
   * Creates a passive item. Every passive has the {@link Rarity#PASSIVE} rarity, so loot tables can
   * pick passives by that tier.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param price its shop price in coins; 0 means free
   * @throws NullPointerException if a text is {@code null}
   * @throws IllegalArgumentException if a text is blank or the price is negative
   */
  protected PassiveItem(String id, String nameKey, String spriteId, int price) {
    super(id, nameKey, spriteId, Rarity.PASSIVE, price);
  }

  /**
   * Returns the stats with this item's effect. By default a passive does not touch the stats.
   *
   * @param inner the stats so far
   * @return the stats with the effect; {@code inner} itself by default
   * @throws NullPointerException if {@code inner} is {@code null}
   */
  public StatSource decorateStats(StatSource inner) {
    return Objects.requireNonNull(inner, "...");
  }

  /**
   * Returns the attack with this item's effect. By default a passive does not touch the attack.
   *
   * @param inner the attack so far
   * @return the attack with the effect; {@code inner} itself by default
   * @throws NullPointerException if {@code inner} is {@code null}
   */
  public Attack decorateAttack(Attack inner) {
    return Objects.requireNonNull(inner, "...");
  }

  /**
   * Applies this item to a build through its two hooks. Subclasses change the hooks, never this.
   *
   * @param build the build so far; it is not changed
   * @return a new build with the decorated stats and attack
   * @throws NullPointerException if {@code build} is {@code null}
   */
  @Override
  public final CharacterBuild modify(CharacterBuild build) {
    Objects.requireNonNull(build, "...");
    return build.withStats(decorateStats(build.stats())).withAttack(decorateAttack(build.attack()));
  }
}
