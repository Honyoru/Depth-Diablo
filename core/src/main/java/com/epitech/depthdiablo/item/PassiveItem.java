package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.entity.BuildModifier;
import com.epitech.depthdiablo.entity.CharacterBuild;
import com.epitech.depthdiablo.entity.StatSource;
import java.util.Objects;

/**
 * An item used once from the hotbar, such as a potion (GDD 1.3.1, 1.4).
 *
 * <p>Pattern: Command. Each consumable is one action the player triggers.
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

  /** Returns the stats with this item's effect; by default the item doesn't touch stats. */
  public StatSource decorateStats(StatSource inner) {
    return Objects.requireNonNull(inner, "...");
  }

  /** Returns the attack with this item's effect; by default the item doesn't touch the attack. */
  public Attack decorateAttack(Attack inner) {
    return Objects.requireNonNull(inner, "...");
  }

  @Override
  public final CharacterBuild modify(CharacterBuild build) {
    Objects.requireNonNull(build, "...");
    return build.withStats(decorateStats(build.stats())).withAttack(decorateAttack(build.attack()));
  }
}
