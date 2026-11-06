package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.Damageable;
import java.util.Objects;

/**
 * Base class for player and enemy entities, with shared health and damage handling. The final
 * {@link #update(long)} method performs common per-tick bookkeeping before delegating
 * character-specific behavior to {@link #think(long)}.
 */
public abstract class Character implements Damageable {
  private final int maxHp;
  private int hp;
  private final Aabb hurtbox;
  private final int invincibilityTicksAfterHit;
  private int invincibilityTicksLeft;

  /**
   * Creates a character with the specified health, hurtbox, and post-hit invincibility duration.
   *
   * @param maxHp the character's maximum health; must be at least 1
   * @param hurtbox the character's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit the number of invincibility ticks granted after taking
   *     damage; must not be negative
   * @throws IllegalArgumentException if {@code maxHp} is less than 1 or {@code
   *     invincibilityTicksAfterHit} is negative
   * @throws NullPointerException if {@code hurtbox} is {@code null}
   */
  protected Character(int maxHp, Aabb hurtbox, int invincibilityTicksAfterHit) {
    if (maxHp < 1) {
      throw new IllegalArgumentException("maxHp must be at least 1");
    }
    if (invincibilityTicksAfterHit < 0) {
      throw new IllegalArgumentException("invincibilityTicksAfterHit must not be negative");
    }
    this.maxHp = maxHp;
    this.hp = maxHp;
    this.hurtbox = Objects.requireNonNull(hurtbox, "hurtbox");
    this.invincibilityTicksAfterHit = invincibilityTicksAfterHit;
  }

  /** Updates invincibility bookkeeping and performs character-specific behavior for the tick. */
  public final void update(long tick) {
    if (invincibilityTicksLeft > 0) {
      invincibilityTicksLeft--;
    }
    think(tick);
  }

  /** Performs character-specific behavior for the given tick. */
  protected abstract void think(long tick);

  @Override
  public Aabb hurtbox() {
    return hurtbox;
  }

  @Override
  public void takeDamage(int amount) {
    if (amount < 1) {
      throw new IllegalArgumentException("Damage amount must be at least 1");
    }
    if (isDead() || isInvincible()) {
      return;
    }
    hp = amount >= hp ? 0 : hp - amount;
    invincibilityTicksLeft = invincibilityTicksAfterHit;
  }

  @Override
  public boolean isDead() {
    return hp == 0;
  }

  /** Grants invincibility for at least the specified number of ticks. */
  public void grantInvincibility(int ticks) {
    if (ticks < 0) {
      throw new IllegalArgumentException("ticks must not be negative");
    }
    invincibilityTicksLeft = Math.max(invincibilityTicksLeft, ticks);
  }

  /** Returns whether the character currently has active invincibility. */
  public boolean isInvincible() {
    return invincibilityTicksLeft > 0;
  }

  /** Returns the character's current health. */
  public int hp() {
    return hp;
  }

  /** Returns the character's maximum health. */
  public int maxHp() {
    return maxHp;
  }
}
