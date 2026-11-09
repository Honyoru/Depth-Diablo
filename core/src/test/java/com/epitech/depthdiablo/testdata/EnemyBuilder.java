package com.epitech.depthdiablo.testdata;

import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Enemy;

/** Test Data Builder for enemies (Q116): {@code anEnemy().build()}. */
public final class EnemyBuilder {
  private EnemyBuilder() {}

  /**
   * Starts an enemy with 10 HP, a 16 x 16 hurtbox at the origin and no i-frames after a hit.
   *
   * @return a new builder
   */
  public static EnemyBuilder anEnemy() {
    return new EnemyBuilder();
  }

  /**
   * Builds the enemy.
   *
   * @return an enemy that does nothing on its own
   */
  public Enemy build() {
    return new IdleEnemy();
  }

  /** Hand-written fake (Q117): an enemy with no behaviour of its own. */
  private static final class IdleEnemy extends Enemy {
    IdleEnemy() {
      super(10, new Aabb(0, 0, 16, 16), 0);
    }

    @Override
    protected void think(long tick) {
      // An idle enemy never acts.
    }
  }
}
