package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.DamageFormula;
import com.epitech.depthdiablo.combat.Obb;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.World;

public abstract class MeleeEnemy extends Enemy {
  private final float reach;

  protected MeleeEnemy(
    Aabb bodyBox,
    StatSource stats,
    Aabb hurtbox,
    int invincibilityTicksAfterHit,
    boolean summoned,
    float reach){
      super(bodyBox, stats, hurtbox, invincibilityTicksAfterHit, summoned);
      if (!(reach >= 0)|| Float.isInfinite(reach)){
        throw new IllegalArgumentException("reach must be a finite number of 0 or more: "+ reach);
      }
      this.reach = reach;
    }

    @Override
    protected void attackTarget(World world) {
      Character target = target().orElse(null);
      if( target == null || target.isDead() || target.isInvincible()
        || !faction().canHurt(target.faction())
        || !attackArea().overlaps(target.hurtbox())) {
          return;
      }
        int damage == DamageFormula.compute(attack(),0,1.0, target.defense(),0,1.0);
        target.takeDamage(damage);
        world.eventBus().publish(new DamageDealt(this, target, damage));
    }

    public float reach() {
      return reach;
    }

    private Obb attackArea() {
      Aabb body = bodyBox();
      return Obb.around(
        new Aabb(
          body.x()-reach,
          body.y()-reach,
          body.width()+ 2 * reach,
          body.height()+2 * reach));

    }
}




