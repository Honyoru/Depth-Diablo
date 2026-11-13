package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.Damageable;
import java.util.Objects;

/**
 * Base class for player and enemy entities, with shared health and damage handling. The final
 * {@link #onUpdate(long)} method performs common per-tick bookkeeping before delegating
 * character-specific behavior to {@link #think(long)}.
 */
public abstract class Character extends Entity implements Damageable {
  private final StatSource stats;
  private int hp;
  private final Aabb hurtbox;
  private final int invincibilityTicksAfterHit;
  private int invincibilityTicksLeft;
  private final Aabb hurtboxFromBody;

  /**
   * Creates a character with the specified body box, stats, hurtbox, and post-hit invincibility
   * duration. It starts with full HP.
   *
   * @param bodyBox the box the character occupies in the world; must not be {@code null}
   * @param stats where its stats come from (GDD 1.6), usually {@link BaseStats}, which checks the
   *     values; must not be {@code null}
   * @param hurtbox the character's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit the number of invincibility ticks granted after taking
   *     damage; must not be negative
   * @throws IllegalArgumentException if {@code invincibilityTicksAfterHit} is negative
   * @throws NullPointerException if {@code bodyBox}, {@code stats} or {@code hurtbox} is {@code
   *     null}
   */
  protected Character(
      Aabb bodyBox, StatSource stats, Aabb hurtbox, int invincibilityTicksAfterHit) {
    super(bodyBox);
    if (invincibilityTicksAfterHit < 0) {
      throw new IllegalArgumentException("invincibilityTicksAfterHit must not be negative");
    }
    Objects.requireNonNull(hurtbox, "hurtbox");
    this.hurtboxFromBody = hurtbox.translated(-bodyBox.x(), -bodyBox.y());
    this.stats = Objects.requireNonNull(stats, "stats");
    this.hp = (int) stats.get(Stat.MAX_HP);
    this.hurtbox = Objects.requireNonNull(hurtbox, "hurtbox");
    this.invincibilityTicksAfterHit = invincibilityTicksAfterHit;
  }

  /** Updates invincibility bookkeeping and performs character-specific behavior for the tick. */
  @Override
  protected final void onUpdate(long tick) {
    if (invincibilityTicksLeft > 0) {
      invincibilityTicksLeft--;
    }
    think(tick);
  }

  /** Performs character-specific behavior for the given tick. */
  protected abstract void think(long tick);

  @Override
  public Aabb hurtbox() {
    return hurtboxFromBody.translated(bodyBox().x(), bodyBox().y());
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

  /**
   * Restores HP, up to the maximum. Does nothing once the character is dead.
   *
   * @param amount the HP to restore; must be at least 1
   * @throws IllegalArgumentException if {@code amount} is less than 1
   */
  public void heal(int amount) {
    if (amount < 1) {
      throw new IllegalArgumentException("Heal amount must be at least 1");
    }
    if (isDead()) {
      return;
    }
    hp = Math.min(maxHp(), hp + amount);
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

  /**
   * Returns the base damage of this character's hits, used as the attack in the damage formula.
   *
   * @return the attack, rounded down
   */
  public int attack() {
    return (int) stats.get(Stat.ATTACK);
  }

  /**
   * Returns the defense this character opposes to hits, used as the resistance (the divisor) in the
   * damage formula.
   *
   * @return the defense, rounded down
   */
  public int defense() {
    return (int) stats.get(Stat.DEFENSE);
  }

  /**
   * Returns how fast this character moves.
   *
   * @return the speed in pixels per second
   */
  public float speed() {
    return stats.get(Stat.SPEED);
  }

  /**
   * Returns how often this character can attack. The weapon and passive items will change it later.
   *
   * @return the attacks per second
   */
  public float attackSpeed() {
    return stats.get(Stat.ATTACK_SPEED);
  }

  /**
   * Returns the character's maximum health.
   *
   * @return the maximum HP, rounded down
   */
  public int maxHp() {
    return (int) stats.get(Stat.MAX_HP);
  }
}
