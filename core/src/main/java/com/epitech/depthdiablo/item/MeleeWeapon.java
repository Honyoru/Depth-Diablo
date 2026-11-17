package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.Obb;
import com.epitech.depthdiablo.combat.Swing;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Character;
import java.util.Objects;

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
   * @param attackSpeed its base attack speed; the holder's ATTACK_SPEED stat multiplies it; above 0
   *     and finite
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
      float attackSpeed,
      float range,
      float width,
      float arcDegrees,
      int swingTicks) {
    super(id, nameKey, spriteId, rarity, price, attackBonus, attackSpeed, range);
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
  public final float getWidth() {
    return width;
  }

  /**
   * Returns the angle the blade sweeps during one swing, centred on the aim.
   *
   * @return the arc in degrees, from 0 to 360
   */
  public final float getArcDegrees() {
    return arcDegrees;
  }

  /**
   * Returns how many ticks one swing lasts.
   *
   * @return the swing duration in ticks, 1 or more
   */
  public final int getSwingTicks() {
    return swingTicks;
  }

  /**
   * Builds the blade at one moment of a swing. The blade starts at the pivot and points away from
   * it; during the swing it turns from {@code aim - arc/2} to {@code aim + arc/2}.
   *
   * @param pivotX the x of the point the blade turns around, usually the holder's centre
   * @param pivotY the y of that point
   * @param aimDegrees the direction of the attack, in degrees (0 = right, 90 = up)
   * @param progress how far the swing is, from 0 (start) to 1 (end)
   * @return the blade as a rotated rectangle, {@link #getRange()} long and {@link #getWidth()} wide
   * @throws IllegalArgumentException if the pivot or the aim is not finite, or the progress is
   *     outside 0 to 1
   */
  public final Obb swingShape(float pivotX, float pivotY, float aimDegrees, float progress) {
    if (!(progress >= 0 && progress <= 1)) {
      throw new IllegalArgumentException("Progress must be from 0 to 1: " + progress);
    }
    float angle = aimDegrees - arcDegrees / 2 + arcDegrees * progress;
    double radians = Math.toRadians(angle);
    float halfRange = getRange() / 2;
    float centreX = pivotX + (float) Math.cos(radians) * halfRange;
    float centreY = pivotY + (float) Math.sin(radians) * halfRange;
    return new Obb(centreX, centreY, angle, getRange(), getWidth());
  }

  /**
   * Starts a swing of this weapon: the owner holds it and runs it during its next updates, and
   * cannot attack again until the swing and then its cooldown are over (DECISIONS §5). Either all
   * of it happens or nothing does: a wrong argument leaves the owner's cooldown untouched.
   *
   * @param owner the character who swings; it must be able to attack now
   * @param aimDegrees the direction of the attack, in degrees (0 = right, 90 = up)
   * @param world its entities are the targets of the blade, its event bus gets the damage and
   *     deaths
   * @throws NullPointerException if an argument is {@code null}
   * @throws IllegalArgumentException if the aim is not a finite number
   * @throws IllegalStateException if the owner cannot attack now (see {@link Character#canAttack})
   */
  @Override
  public final void attack(Character owner, float aimDegrees, World world) {
    Objects.requireNonNull(world, "world is null");
    Swing swing = new Swing(this, owner, aimDegrees, world::getEntities, world.getEventBus());
    owner.startAttackCooldown(getSwingTicks(), getAttackSpeed());
    owner.holdSwing(swing);
  }
}
