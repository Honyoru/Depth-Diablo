package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public enum EnemyState implements States<Enemy> {

  IDLE,
  CHASE,
  KEEP_DISTANCE,
  FLEE,
  ATTACK,
  SUMMON,
  HIT,
  DEAD;

  @Override
  public void enter(Enemy enemy) {
    //Enemy AI fill his State
  }

  @Override
  public void update(Enemy enemy) {
    //Enemy AI fill his State
  }

  @Override
  public void exit(Enemy enemy) {
    //Enemy AI fill his State
  }

  @Override
  public boolean onMessage(Enemy enemy, Telegram telegram){
    return false;
  }

}
