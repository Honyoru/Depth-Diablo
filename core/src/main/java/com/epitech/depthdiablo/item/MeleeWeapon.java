package com.epitech.depthdiablo.item;

/**
 * A weapon that hits up close with a blade (GDD 1.6). Its swing is a rotated rectangle (an Obb) as
 * long as the weapon's range, which sweeps an arc around the aim during a few ticks.
 *
 * <p>The weapon only keeps the fixed size of the blade and how it sweeps. The position and the
 * angle of the blade only exist during a swing, so the Obb is built at each tick of the swing.
 */
public abstract class MeleeWeapon extends Weapon {
  private final float width;
  private final float arcDegrees;
  private final int swingTicks;

  /**
   * Creates a melee weapon.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE}
   * @param price its shop price in coins; 0 means free
   * @param attackBonus the flat bonus added to the attack in the damage formula; 0 or more
   * @param range the length of the blade, in pixels; above 0 and finite
   * @param width the width of the blade, in pixels; above 0 and finite
   * @param arcDegrees the angle the blade sweeps, centred on the aim; from 0 (a straight thrust) to
   *     360 (a full spin)
   * @param swingTicks how many ticks one swing lasts; 1 or more
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a value of {@link Weapon} is wrong, the width is not a
   *     finite number above 0, the arc is outside 0 to 360, or the swing lasts less than 1 tick
   */
  protected MeleeWeapon(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float range,
      float width,
      float arcDegrees,
      int swingTicks) {
    super(id, nameKey, spriteId, rarity, price, attackBonus, range);
    if (width <= 0 || Float.isNaN(width) || Float.isInfinite(width)) {
      throw new IllegalArgumentException("Width must be a finite number above 0: " + width);
    }
    if (!(arcDegrees >= 0 && arcDegrees <= 360)) {
      throw new IllegalArgumentException("Arc must be from 0 to 360 degrees: " + arcDegrees);
    }
    if (swingTicks < 1) {
      throw new IllegalArgumentException("A swing must last at least 1 tick: " + swingTicks);
    }
    this.width = width;
    this.arcDegrees = arcDegrees;
    this.swingTicks = swingTicks;
  }

  /**
   * Returns the width of the blade.
   *
   * @return the width in pixels, above 0
   */
  public final float width() {
    return width;
  }

  /**
   * Returns the angle the blade sweeps during one swing, centred on the aim.
   *
   * @return the arc in degrees, from 0 to 360
   */
  public final float arcDegrees() {
    return arcDegrees;
  }

  /**
   * Returns how many ticks one swing lasts.
   *
   * @return the swing duration in ticks, 1 or more
   */
  public final int swingTicks() {
    return swingTicks;
  }
}
