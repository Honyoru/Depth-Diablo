package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Aabb;
import java.util.Objects;

/** The damage area of one attack. It only hurts targets whose hurtbox it overlaps (GDD 1.5). */
public final class Hitbox {
  private final Aabb shape;
  private final Damageable owner;

  /**
   * Creates a hitbox.
   *
   * @param shape the damage area, in world coordinates
   * @param owner who attacks; a hitbox never hits its owner
   */
  public Hitbox(Aabb shape, Damageable owner) {
    this.shape = Objects.requireNonNull(shape, "shape");
    this.owner = Objects.requireNonNull(owner, "owner");
  }

  /**
   * Tells whether this hitbox can damage a target.
   *
   * @param target the target to test
   * @return true if the target is not the owner and its hurtbox overlaps this hitbox
   */
  public boolean canHit(Damageable target) {
    return !owner.equals(target) && shape.overlaps(target.hurtbox());
  }
}
