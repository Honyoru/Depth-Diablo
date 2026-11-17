package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.ContactAttack;
import com.epitech.depthdiablo.combat.DamageFormula;
import com.epitech.depthdiablo.combat.Obb;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;
import java.util.Optional;

/**
 * The enemies that hit at contact: Rat, Zombie, Orc (GDD 1.3.1). Its attack covers its body box
 * grown by its reach on every side, as an {@link Obb} at angle 0 (DECISIONS §5), and uses the
 * damage formula with no bonus and no buff (GDD 1.6).
 */
public abstract class MeleeEnemy extends Enemy {

  private final ContactAttack attack;

  /**
   * Creates a melee enemy.
   *
   * @param bodyBox the box the enemy occupies in the world; must not be {@code null}
   * @param stats where its stats come from (GDD 1.6); must not be {@code null}
   * @param attack its contact attack, which holds its reach; it is also its base attack; must not
   *     be {@code null}
   * @param hurtbox the enemy's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param summoned true if a summoner spawned it
   * @param world the world it lives in; must not be {@code null}
   * @throws NullPointerException if {@code bodyBox}, {@code stats}, {@code attack}, {@code hurtbox}
   *     or {@code world} is {@code null}
   * @throws IllegalArgumentException if {@code invincibilityTicksAfterHit} is negative
   */
  protected MeleeEnemy(
      Aabb bodyBox,
      StatSource stats,
      ContactAttack attack,
      Aabb hurtbox,
      int invincibilityTicksAfterHit,
      boolean summoned,
      World world) {
    super(bodyBox, stats, attack, hurtbox, invincibilityTicksAfterHit, summoned, world);
    this.attack = Objects.requireNonNull(attack, "attack is null");
  }

  /**
   * Returns how far this enemy's attack goes past its body box.
   *
   * @return the reach in pixels, 0 or more
   */
  public float getReach() {
    return attack.getReach();
  }

  /**
   * Hits the target if its hurtbox is in reach and it can be hurt, then publishes the damage dealt,
   * and the death if the hit kills it.
   *
   * @param world the world the enemy lives in
   */
  @Override
  protected void attackTarget(World world) {
    Character target = getTarget().orElse(null);
    if (target == null
        || target.isDead()
        || target.isInvincible()
        || !getFaction().canHurt(target.getFaction())
        || !canReach(target)) {
      return;
    }
    int damage = DamageFormula.compute(getAttack(), 0, 1.0, target.getDefense(), 0, 1.0);
    target.takeDamage(damage);
    world.getEventBus().publish(new DamageDealt(this, target, damage));
    if (target.isDead()) {
      world.getEventBus().publish(new EntityDied(target, Optional.of(this)));
    }
  }

  /**
   * Tells whether the target's hurtbox is inside this enemy's attack area: its body box grown by
   * its reach on every side.
   *
   * @param target the character to reach
   * @return true if an attack now would touch it
   */
  @Override
  protected boolean canReach(Character target) {
    return attackArea().overlaps(target.getHurtbox());
  }

  private Obb attackArea() {
    Aabb body = getBodyBox();
    float reach = getReach();
    return Obb.around(
        new Aabb(
            body.x() - reach,
            body.y() - reach,
            body.width() + 2 * reach,
            body.height() + 2 * reach));
  }
}
