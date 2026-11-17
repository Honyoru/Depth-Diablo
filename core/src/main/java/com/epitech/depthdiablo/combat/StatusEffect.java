package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.Character;

public abstract class StatusEffect {
  private int ticksleft;
  private Character source;

  protected StatusEffect(int durationTicks, Character source) {
    if (durationTicks <= 0) {
      throw new IllegalArgumentException("durationTicks must be positive: " + durationTicks);
    }
    if (source == null) {
      throw new NullPointerException("source cannot be null");
    }
    this.ticksleft = durationTicks;
    this.source = source;
  }

  public final void tick(Character target) {
    if (target == null) {
      throw new NullPointerException("target cannot be null");
    }
    if (isOver()) {
      return;
    }
    ticksleft--;
    onTick(target);
  }

  protected abstract void onTick(Character target);

  public boolean isOver() {
    return ticksleft == 0;
  }

  public int ticksLeft() {
    return ticksleft;
  }

  public Character getSource() {
    return source;
  }
}
