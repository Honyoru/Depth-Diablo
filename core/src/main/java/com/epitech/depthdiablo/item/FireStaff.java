package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.combat.DotSpec;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Character;
import com.epitech.depthdiablo.entity.projectile.Fireball;
import com.epitech.depthdiablo.entity.projectile.Projectile;
import com.epitech.depthdiablo.geometry.Aabb;
import java.util.Objects;

/**
 * The fire staff: a Mage's staff that casts a {@link Fireball} from the centre of its owner (GDD
 * 1.3.1). The fireball burns the first character it hurts with the burn of the staff's entry in
 * {@code data/items.json} (Q93).
 */
public final class FireStaff extends Staff {

  /** The side of a fireball's square body, in pixels. */
  static final float FIREBALL_SIZE = 12f;

  private final DotSpec burn;

  /**
   * Creates a fire staff with the values of its data entry.
   *
   * @param id the item's key in the data files
   * @param nameKey the translation key of its name
   * @param spriteId the picture the renderer draws for it
   * @param rarity its loot tier; never {@link Rarity#CONSUMABLE}
   * @param price its shop price in coins; 0 means free
   * @param attackBonus the flat bonus added to the holder's ATTACK when it builds; 0 or more
   * @param attackSpeed its base attack speed, multiplied into the holder's ATTACK_SPEED; above 0
   *     and finite
   * @param range how far its fireball flies, in pixels; above 0 and finite
   * @param projectileSpeed how fast its fireball flies, in pixels per second; above 0 and finite
   * @param projectileSpriteId the picture of its fireball, pointing right; not blank
   * @param burn the burn its fireball leaves on the character it hurts
   * @throws NullPointerException if a text, the rarity or {@code burn} is {@code null}
   * @throws IllegalArgumentException if a value is out of range
   */
  FireStaff(
      String id,
      String nameKey,
      String spriteId,
      Rarity rarity,
      int price,
      int attackBonus,
      float attackSpeed,
      float range,
      float projectileSpeed,
      String projectileSpriteId,
      DotSpec burn) {
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
    this.burn = Objects.requireNonNull(burn, "burn is null");
  }

  /**
   * Returns the burn its fireball leaves.
   *
   * @return the burn of its data entry
   */
  public DotSpec getBurn() {
    return burn;
  }

  /**
   * Makes a fireball centred on the owner, flying towards the aim.
   *
   * @param owner the character who casts it
   * @param aimDegrees the direction of the cast, in degrees
   * @param world the world the fireball flies in
   * @return the new fireball, not yet in the world
   */
  @Override
  protected Projectile createProjectile(Character owner, float aimDegrees, World world) {
    Aabb body = owner.getBodyBox();
    float x = body.x() + body.width() / 2 - FIREBALL_SIZE / 2;
    float y = body.y() + body.height() / 2 - FIREBALL_SIZE / 2;
    return new Fireball(
        new Aabb(x, y, FIREBALL_SIZE, FIREBALL_SIZE),
        owner,
        aimDegrees,
        getProjectileSpeed(),
        getProjectileLifetimeTicks(),
        getProjectileSpriteId(),
        burn,
        world);
  }
}
