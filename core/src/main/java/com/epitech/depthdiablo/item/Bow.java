package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Arrow;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Projectile;

/**
 * The bow family: every bow shoots an {@link Arrow} from the centre of its owner (GDD 1.3.1). The
 * real bows extend it and only bring their values from {@code data/items.json}; the desktop plays
 * the bow sound for all of them (GDD 4.2.2).
 */
public abstract class Bow extends RangedWeapon {

  /** The length of an arrow's body along its flight, in pixels. */
  static final float ARROW_LENGTH = 16f;

  /** The thickness of an arrow's body, in pixels. */
  static final float ARROW_THICKNESS = 4f;

  /**
   * Creates a bow.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE}
   * @param price its shop price in coins; 0 means free
   * @param attackBonus the flat bonus added to the holder's ATTACK when it builds; 0 or more
   * @param attackSpeed its base attack speed, multiplied into the holder's ATTACK_SPEED; above 0
   *     and finite
   * @param range how far its arrow flies, in pixels; above 0 and finite
   * @param projectileSpeed how fast its arrow flies, in pixels per second; above 0 and finite
   * @param projectileSpriteId the picture of its arrow, pointing right; not blank
   * @throws NullPointerException if a text or the rarity is {@code null}
   * @throws IllegalArgumentException if a value is out of range
   */
  protected Bow(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float attackSpeed,
      float range,
      float projectileSpeed,
      String projectileSpriteId) {
    super(
        id,
        nameKey,
        spriteId,
        rarity,
        price,
        attackBonus,
        attackSpeed,
        range,
        projectileSpeed,
        projectileSpriteId);
  }

  /**
   * Makes an arrow centred on the owner, flying towards the aim.
   *
   * @param owner the character who shoots
   * @param aimDegrees the direction of the shot, in degrees
   * @param world the world the arrow flies in
   * @return the new arrow, not yet in the world
   */
  @Override
  protected final Projectile createProjectile(Character owner, float aimDegrees, World world) {
    Aabb body = owner.getBodyBox();
    float x = body.x() + body.width() / 2 - ARROW_LENGTH / 2;
    float y = body.y() + body.height() / 2 - ARROW_THICKNESS / 2;
    return new Arrow(
        new Aabb(x, y, ARROW_LENGTH, ARROW_THICKNESS),
        owner,
        aimDegrees,
        getProjectileSpeed(),
        getProjectileLifetimeTicks(),
        getProjectileSpriteId(),
        world);
  }
}
