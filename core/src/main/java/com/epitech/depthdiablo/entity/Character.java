package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.Damageable;
import java.util.Objects;

/**
 * Base of the player and of every enemy: HP, hurtbox and invincibility frames (GDD 1.3.1).
 *
 * <p>Pattern: Template Method. {@link #update(long)} counts the i-frames down, then calls {@link
 * #think(long)}, which each subclass fills in.
 */
public abstract class Character implements Damageable {
  private final int maxHp;
  private final Aabb hurtbox;
  private final int invincibilityTicksAfterHit;
  private int hp;
  private int invincibleTicks;

  /**
   * Creates a character at full HP.
   *
   * @param maxHp the maximum and starting HP, at least 1
   * @param hurtbox where the character can be hit
   * @param invincibilityTicksAfterHit i-frames granted after each hit (Q28), 0 for none
   * @throws IllegalArgumentException if maxHp is below 1 or the i-frames are negative
   */
  protected Character(int maxHp, Aabb hurtbox, int invincibilityTicksAfterHit) {
    if (maxHp < 1) {
      throw new IllegalArgumentException("maxHp must be at least 1: " + maxHp);
    }
    if (invincibilityTicksAfterHit < 0) {
      throw new IllegalArgumentException("I-frames must not be negative");
    }
    this.maxHp = maxHp;
    this.hp = maxHp;
    this.hurtbox = Objects.requireNonNull(hurtbox, "hurtbox");
    this.invincibilityTicksAfterHit = invincibilityTicksAfterHit;
  }

  /**
   * Advances the character by one tick.
   *
   * @param tick the current tick number
   */
  public final void update(long tick) {
    if (invincibleTicks > 0) {
      invincibleTicks--;
    }
    think(tick);
  }

  /**
   * The subclass's own behaviour for one tick (AI, input).
   *
   * @param tick the current tick number
   */
  protected abstract void think(long tick);

  @Override
  public Aabb hurtbox() {
    return hurtbox;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Ignored while invincible or dead; otherwise grants the i-frames after the hit.
   *
   * @throws IllegalArgumentException if the amount is below 1
   */
  @Override
  public void takeDamage(int amount) {
    if (amount < 1) {
      throw new IllegalArgumentException("Damage must be at least 1: " + amount);
    }
    if (isDead() || isInvincible()) {
      return;
    }
    hp -= amount;
    invincibleTicks = invincibilityTicksAfterHit;
  }

  @Override
  public boolean isDead() {
    return hp <= 0;
  }

  /**
   * Makes the character invincible, for example during a dodge roll (Q40).
   *
   * @param ticks how long, in ticks; a shorter value never cuts a longer invincibility
   */
  public void grantInvincibility(int ticks) {
    invincibleTicks = Math.max(invincibleTicks, ticks);
  }

  /**
   * Tells whether hits are ignored right now.
   *
   * @return true during i-frames
   */
  public boolean isInvincible() {
    return invincibleTicks > 0;
  }

  /**
   * Returns the current HP.
   *
   * @return the HP, 0 or less once dead
   */
  public int hp() {
    return hp;
  }

  /**
   * Returns the maximum HP.
   *
   * @return the maximum HP
   */
  public int maxHp() {
    return maxHp;
  }
}
