package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.EventBus;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Entity;
import java.util.Objects;
import java.util.Optional;

/**
 * The attack of an enemy that hits at contact, such as a rat or a zombie (GDD 1.3.1): every hostile
 * character whose hurtbox touches its area gets hit. It replaces the code of {@code
 * MeleeEnemy.attackTarget}.
 *
 * <p>Pattern: Template Method. {@link #attack} always checks, starts the cooldown, hits and
 * publishes the same way; each subclass only gives the {@link #area} it covers.
 */
public abstract class ContactAttack implements Attack {
  private final float reach;

  /**
   * Creates a contact attack.
   *
   * @param reach how far the attack goes past the owner's body box, in pixels; 0 means touching
   * @throws IllegalArgumentException if {@code reach} is negative or not a finite number
   */
  protected ContactAttack(float reach) {
    if (reach < 0 || Float.isNaN(reach) || Float.isInfinite(reach)) {
      throw new IllegalArgumentException("Reach must be a finite number of 0 or more: " + reach);
    }
    this.reach = reach;
  }

  /**
   * Returns how far the attack goes past the owner's body box.
   *
   * @return the reach in pixels, 0 or more
   */
  public final float getReach() {
    return reach;
  }

  /**
   * Starts the owner's cooldown, then hits every hostile character whose hurtbox touches the area
   * of this attack (see {@link #area}), never the owner itself. Each hit that lands publishes a
   * DamageDealt event, and an EntityDied event if it kills.
   *
   * @param owner the character who attacks; it must be able to attack now
   * @param aimDegrees the direction of the attack, in degrees (0 = right, 90 = up); a finite number
   * @param world its entities are the targets, its event bus gets the damage and deaths
   * @throws NullPointerException if {@code owner} or {@code world} is {@code null}
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
    owner.startAttackCooldown(0);
    for (Entity entity : world.getEntities()) {
      if (entity instanceof Character target
          && !owner.equals(target)
          && owner.getFaction().canHurt(target.getFaction())
          && touches(owner.getBodyBox(), aimDegrees, target.getHurtbox())) {
        strike(owner, target, world.getEventBus());
      }
    }
  }

  /**
   * Tells whether the area of this attack, aimed from the body, touches the hurtbox. An enemy asks
   * it before attacking, to know whether its target is in reach; {@link #attack} asks it for each
   * character around.
   *
   * @param body the body box of the character who attacks
   * @param aimDegrees the direction of the attack, in degrees (0 = right, 90 = up)
   * @param hurtbox the hurtbox to reach
   * @return true if an attack now would touch it
   */
  public final boolean touches(Aabb body, float aimDegrees, Aabb hurtbox) {
    return area(body, aimDegrees).overlaps(hurtbox);
  }

  /**
   * Builds the zone this attack covers for a body and an aim.
   *
   * @param body the owner's body box
   * @param aimDegrees the direction of the attack, in degrees (0 = right, 90 = up); finite
   * @return the area as a rotated rectangle
   */
  protected abstract Obb area(Aabb body, float aimDegrees);

  private void strike(Character owner, Character target, EventBus events) {
    HitResult result = owner.hit(target);
    if (result.landed()) {
      events.publish(new DamageDealt(owner, target, result.damage()));
      if (result.killed()) {
        events.publish(new EntityDied(target, Optional.of(owner)));
      }
    }
  }
}
