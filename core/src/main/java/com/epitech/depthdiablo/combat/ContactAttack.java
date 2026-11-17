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
 * The attack of an enemy that hits at contact, such as a rat or a zombie (GDD 1.3.1). It hits every
 * hostile character whose hurtbox is within its reach of the owner's body box, on every side
 * (DECISIONS §5). It replaces the code of {@code MeleeEnemy.attackTarget}.
 */
public final class ContactAttack implements Attack {
  private final float reach;

  /**
   * Creates a contact attack.
   *
   * @param reach how far the attack goes past the owner's body box, in pixels; 0 means touching
   * @throws IllegalArgumentException if {@code reach} is negative or not a finite number
   */
  public ContactAttack(float reach) {
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
  public float reach() {
    return reach;
  }

  /**
   * Hits the target with the damage formula (GDD 1.6): the attacker's attack against the target's
   * defense, with no bonus and no buff.
   *
   * @param attacker the character who hits
   * @param target the character who is hit
   * @return the damage dealt and whether it killed the target; {@link HitResult#NONE} if the target
   *     is dead or invincible
   * @throws NullPointerException if {@code attacker} or {@code target} is {@code null}
   */
  @Override
  public HitResult hit(Character attacker, Character target) {
    Objects.requireNonNull(attacker, "attacker is null");
    Objects.requireNonNull(target, "target is null");
    if (target.isDead() || target.isInvincible()) {
      return HitResult.NONE;
    }
    int damage = DamageFormula.compute(attacker.attack(), 0, 1.0, target.defense(), 0, 1.0);
    target.takeDamage(damage);
    return new HitResult(damage, target.isDead());
  }

  /**
   * Hits every hostile character whose hurtbox is within the reach of the owner's body box, on
   * every side (DECISIONS §5), then starts the owner's cooldown. The aim is not used: a contact
   * attack hits all around. Each hit that lands publishes a DamageDealt event, and an EntityDied
   * event if it kills.
   *
   * @param owner the character who attacks; it must be able to attack now
   * @param aimDegrees not used, but it must be a finite number, as for every attack
   * @param world its entities are the targets, its event bus gets the damage and deaths
   * @throws NullPointerException if {@code owner} or {@code world} is {@code null}
   * @throws IllegalArgumentException if the aim is not a finite number
   * @throws IllegalStateException if the owner cannot attack now (see {@link Character#canAttack})
   */
  @Override
  public void attack(Character owner, float aimDegrees, World world) {
    Objects.requireNonNull(owner, "owner is null");
    Objects.requireNonNull(world, "world is null");
    if (!Float.isFinite(aimDegrees)) {
      throw new IllegalArgumentException("Aim must be a finite number: " + aimDegrees);
    }
    owner.startAttackCooldown(0, 1f);
    Obb area = areaAround(owner);
    for (Entity entity : world.entities()) {
      if (entity instanceof Character target
          && !owner.equals(target)
          && owner.faction().canHurt(target.faction())
          && area.overlaps(target.hurtbox())) {
        strike(owner, target, world.eventBus());
      }
    }
  }

  private Obb areaAround(Character owner) {
    Aabb body = owner.bodyBox();
    return Obb.around(
        new Aabb(
            body.x() - reach,
            body.y() - reach,
            body.width() + 2 * reach,
            body.height() + 2 * reach));
  }

  private void strike(Character owner, Character target, EventBus events) {
    HitResult result = hit(owner, target);
    if (result.landed()) {
      events.publish(new DamageDealt(owner, target, result.damage()));
      if (result.killed()) {
        events.publish(new EntityDied(target, Optional.of(owner)));
      }
    }
  }
}
