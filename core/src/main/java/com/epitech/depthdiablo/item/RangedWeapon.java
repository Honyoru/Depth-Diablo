package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Projectile;
import java.util.Objects;

/**
 * A weapon that attacks from afar: each attack fires one projectile that flies {@link #getRange()}
 * pixels (GDD 1.3.1). Its families are {@link Bow} and {@link Staff}; the real weapons extend one
 * of them, so the desktop picks the attack sound by family (GDD 4.2.2).
 *
 * <p>Pattern: Template Method. {@link #attack} checks, starts the cooldown and adds the shot to the
 * world; {@link #createProjectile} is the hook that says which shot it is.
 */
public abstract class RangedWeapon extends Weapon {

  private final float projectileSpeed;

  /**
   * Creates a ranged weapon.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE}
   * @param price its shop price in coins; 0 means free
   * @param attackBonus the flat bonus added to the holder's ATTACK when it builds; 0 or more
   * @param attackSpeed its base attack speed, multiplied into the holder's ATTACK_SPEED; above 0
   *     and finite
   * @param range how far its projectile flies, in pixels; above 0 and finite
   * @param projectileSpeed how fast its projectile flies, in pixels per second; above 0 and finite
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a value is out of range
   */
  protected RangedWeapon(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float attackSpeed,
      float range,
      float projectileSpeed) {
    super(id, nameKey, spriteId, rarity, price, attackBonus, attackSpeed, range);
    if (projectileSpeed <= 0 || Float.isNaN(projectileSpeed) || Float.isInfinite(projectileSpeed)) {
      throw new IllegalArgumentException(
          "Projectile speed must be a finite number above 0: " + projectileSpeed);
    }
    this.projectileSpeed = projectileSpeed;
  }

  /**
   * Returns how fast its projectile flies.
   *
   * @return the speed, in pixels per second
   */
  public final float getProjectileSpeed() {
    return projectileSpeed;
  }

  /**
   * Returns how many ticks its projectile flies to cover the range, at least 1.
   *
   * @return the projectile's lifetime, in ticks
   */
  public final int getProjectileLifetimeTicks() {
    return Math.max(1, Math.round(getRange() / projectileSpeed * GameConstants.TICKS_PER_SECOND));
  }

  /**
   * Fires one projectile towards the aim and starts the owner's cooldown. Either all of it happens
   * or nothing does: a wrong argument leaves the owner's cooldown untouched.
   *
   * @param owner the character who shoots; it must be able to attack now
   * @param aimDegrees the direction of the shot, in degrees (0 = right, 90 = up)
   * @param world the world the projectile flies in
   * @throws NullPointerException if an argument is {@code null}
   * @throws IllegalArgumentException if the aim is not a finite number
   * @throws IllegalStateException if the owner cannot attack now (see {@link Character#canAttack})
   */
  @Override
  public final void attack(Character owner, float aimDegrees, World world) {
    Objects.requireNonNull(owner, "owner is null");
    Objects.requireNonNull(world, "world is null");
    if (!Float.isFinite(aimDegrees)) {
      throw new IllegalArgumentException("Aim must be a finite number: " + aimDegrees);
    }
    Projectile shot = createProjectile(owner, aimDegrees, world);
    owner.startAttackCooldown(0);
    world.add(shot);
  }

  /**
   * Makes the projectile of one shot, starting on the owner and flying towards the aim at {@link
   * #getProjectileSpeed()} for {@link #getProjectileLifetimeTicks()} ticks.
   *
   * @param owner the character who shoots
   * @param aimDegrees the direction of the shot, in degrees, already checked as finite
   * @param world the world the projectile flies in
   * @return the new projectile, not yet in the world
   */
  protected abstract Projectile createProjectile(Character owner, float aimDegrees, World world);
}
