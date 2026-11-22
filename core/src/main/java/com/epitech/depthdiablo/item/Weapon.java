package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.entity.stats.CharacterBuild;
import com.epitech.depthdiablo.entity.stats.StatSource;
import java.util.List;
import java.util.Objects;

/**
 * The equipped weapon: the player's attack (GDD 1.3.1, 1.6). Its attack bonus and attack speed go
 * into its holder's build (see {@link #modify}); it only hits targets within its range (Q37).
 *
 * <p>Pattern: Strategy. Each weapon is one {@link Attack}; the character calls it without knowing
 * which weapon it is, and equipping another weapon swaps the strategy.
 */
public abstract class Weapon extends Equipment implements Attack {

  private final int attackBonus;

  private final float attackSpeed;

  private final float range;

  /**
   * Creates a weapon.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE}
   * @param price its shop price in coins; 0 means free
   * @param attackBonus the flat bonus added to the holder's ATTACK when it builds; 0 or more
   * @param attackSpeed its base attack speed; it multiplies the holder's ATTACK_SPEED when it
   *     builds (GDD 1.6); above 0 and finite
   * @param range how far its hit reaches, in pixels; above 0 and finite
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a text is blank, the price or the attack bonus is negative,
   *     the rarity is CONSUMABLE, or the attack speed or the range is not a finite number above 0
   */
  protected Weapon(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float attackSpeed,
      float range) {
    super(id, nameKey, spriteId, rarity, price);
    if (attackBonus < 0) {
      throw new IllegalArgumentException("Attack bonus must not be negative: " + attackBonus);
    }
    if (attackSpeed <= 0 || Float.isNaN(attackSpeed) || Float.isInfinite(attackSpeed)) {
      throw new IllegalArgumentException(
          "Attack speed must be a finite number above 0: " + attackSpeed);
    }
    if ((range <= 0) || Float.isNaN(range) || Float.isInfinite(range)) {
      throw new IllegalArgumentException("Range must be a finite number above 0: " + range);
    }
    this.attackBonus = attackBonus;
    this.attackSpeed = attackSpeed;
    this.range = range;
  }

  /**
   * Returns the flat bonus this weapon adds to the holder's ATTACK when it builds.
   *
   * @return the attack bonus, 0 or more
   */
  public final int getAttackBonus() {
    return attackBonus;
  }

  /**
   * Returns the base attack speed of this weapon. It multiplies the holder's ATTACK_SPEED when it
   * builds.
   *
   * @return the attack speed, above 0
   */
  public final float getAttackSpeed() {
    return attackSpeed;
  }

  /**
   * Returns how far this weapon's hit reaches.
   *
   * @return the range in pixels, above 0
   */
  public final float getRange() {
    return range;
  }

  /**
   * Makes this weapon the attack of the build, adds its attack bonus to the build's ATTACK and
   * multiplies the build's ATTACK_SPEED by its attack speed (GDD 1.3.1, 1.6); the other stats stay
   * the same.
   *
   * @param build the build so far; it is not changed
   * @return a new build whose attack is this weapon
   * @throws NullPointerException if {@code build} is {@code null}
   */
  @Override
  public final CharacterBuild modify(CharacterBuild build) {
    Objects.requireNonNull(build, "build is null");
    StatSource base = build.stats();
    StatSource withWeapon =
        stat ->
            switch (stat) {
              case ATTACK -> base.get(stat) + attackBonus;
              case ATTACK_SPEED -> base.get(stat) * attackSpeed;
              default -> base.get(stat);
            };
    return build.withAttack(this).withStats(withWeapon);
  }

  /**
   * Returns the attack bonus and the attack speed of this weapon, as {@link #modify} applies them.
   *
   * @return {@code bonus.attack} then {@code bonus.attack_speed}
   */
  @Override
  public final List<ItemEffect> getEffects() {
    return List.of(
        new ItemEffect("bonus.attack", attackBonus),
        new ItemEffect("bonus.attack_speed", attackSpeed));
  }
}
