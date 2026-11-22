package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.entity.stats.CharacterBuild;
import com.epitech.depthdiablo.entity.stats.StatSource;
import java.util.List;
import java.util.Objects;

/**
 * An armor the player wears in its portrait slot (GDD 1.3.1, 1.4): it adds its defense bonus to the
 * build of its holder. {@link LeatherArmor} is its first concrete class.
 */
public abstract class Armor extends Equipment {

  private final int defenseBonus;

  /**
   * Creates an armor.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE} or {@link Rarity#PASSIVE}
   * @param price its shop price in coins; 0 means free
   * @param defenseBonus the defense it adds to its holder's; 0 or more
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a value of {@link Equipment} is wrong or the defense bonus
   *     is negative
   */
  protected Armor(
      String id, String nameKey, String spriteId, Rarity rarity, int price, int defenseBonus) {
    super(id, nameKey, spriteId, rarity, price);
    if (defenseBonus < 0) {
      throw new IllegalArgumentException("Defense bonus must not be negative: " + defenseBonus);
    }
    this.defenseBonus = defenseBonus;
  }

  /**
   * Returns the defense this armor adds to its holder's.
   *
   * @return the defense bonus; 0 or more
   */
  public final int getDefenseBonus() {
    return defenseBonus;
  }

  /**
   * Adds the defense bonus to the build's DEFENSE; the attack and the other stats are kept.
   *
   * @param build the build before this armor
   * @return a new build with this armor's defense
   * @throws NullPointerException if {@code build} is null
   */
  @Override
  public final CharacterBuild modify(CharacterBuild build) {
    Objects.requireNonNull(build, "build is null");
    StatSource base = build.stats();
    StatSource withArmor =
        stat ->
            switch (stat) {
              case DEFENSE -> base.get(stat) + defenseBonus;
              default -> base.get(stat);
            };
    return build.withStats(withArmor);
  }

  /**
   * Returns the defense bonus of this armor, as {@link #modify} applies it.
   *
   * @return {@code bonus.defense}
   */
  @Override
  public final List<ItemEffect> getEffects() {
    return List.of(new ItemEffect("bonus.defense", defenseBonus));
  }
}
