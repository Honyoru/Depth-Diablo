package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/**
 * What an enemy is doing (GDD 1.3 roster, 1.7). The states do nothing yet: their behaviour comes
 * with the enemy AI.
 *
 * <p>Pattern: State, on the gdx-ai state machine (Q49).
 */
public enum EnemyState implements State<Enemy> {
  /** Waits until it sees a target or is alerted. */
  IDLE {
    @Override
    public void update(Enemy enemy) {
      if (enemy.seesTarget()) {
        enemy.changeState(CHASE);
      }
    }
  },
  /** Walks towards its target (melee enemies). */
  CHASE {
    @Override
    public void update(Enemy enemy) {
      if (!enemy.seesTarget()) {
        enemy.changeState(IDLE);
      } else if (enemy.reachesTarget()) {
        enemy.changeState(ATTACK);
      } else {
        enemy.stepTowardsTarget();
      }
    }
  },
  /** Stays at its distance from its target (ranged enemies). */
  KEEP_DISTANCE,
  /** Runs away from its target (Necromancer). */
  FLEE,
  /** Hits, shoots or casts at its target. */
  ATTACK {
    @Override
    public void update(Enemy enemy) {
      if (!enemy.seesTarget()) {
        enemy.changeState(IDLE);
      } else if (!enemy.reachesTarget()) {
        enemy.changeState(CHASE);
      } else {
        enemy.attackWhenReady();
      }
    }
  },
  /** Spawns other enemies (summoners). */
  SUMMON,
  /** Has just been hit. */
  HIT,
  /** HP at 0. */
  DEAD {
    @Override
    public void enter(Enemy enemy) {
      enemy.remove();
    }
  };

  @Override
  public void enter(Enemy enemy) {
    // Enemy AI fill his State
  }

  @Override
  public void update(Enemy enemy) {
    // Enemy AI fill his State
  }

  @Override
  public void exit(Enemy enemy) {
    // Enemy AI fill his State
  }

  @Override
  public boolean onMessage(Enemy enemy, Telegram telegram) {
    return false;
  }
}
