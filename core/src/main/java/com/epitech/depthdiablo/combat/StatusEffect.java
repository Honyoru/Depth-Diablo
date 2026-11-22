package com.epitech.depthdiablo.combat;

import com.epitech.depthdiablo.entity.character.Character;

/**
 * An effect that lasts a number of ticks on a character, such as a burn (GDD 1.3.1, 1.6). The
 * character ticks its effects at the start of each update and drops them once they are over.
 *
 * <p>Pattern: Template Method. {@link #tick} counts the duration down and calls {@link #onTick},
 * the step each effect fills in.
 */
public abstract class StatusEffect {
  private int ticksleft;
  private Character source;

  /**
   * Creates an effect with its whole duration left.
   *
   * @param durationTicks how many ticks it lasts; at least 1
   * @param source the character who applied it, credited for its damage
   * @throws IllegalArgumentException if {@code durationTicks} is less than 1
   * @throws NullPointerException if {@code source} is {@code null}
   */
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

  /**
   * Runs one tick of the effect: counts one tick down, then runs its step. Does nothing once the
   * effect is over.
   *
   * @param target the character who carries the effect
   * @throws NullPointerException if {@code target} is {@code null}
   */
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

  /**
   * The step each effect fills in, run once per tick while the effect lasts.
   *
   * @param target the character who carries the effect
   */
  protected abstract void onTick(Character target);

  /**
   * Tells whether the effect has run its whole duration.
   *
   * @return true once no tick is left
   */
  public boolean isOver() {
    return ticksleft == 0;
  }

  /**
   * Returns how many ticks the effect still lasts.
   *
   * @return the ticks left; 0 once over
   */
  public int ticksLeft() {
    return ticksleft;
  }

  /**
   * Returns the character who applied the effect.
   *
   * @return the source, credited for the effect's damage and kills
   */
  public Character getSource() {
    return source;
  }
}
