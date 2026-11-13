package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.GameConstants;
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
  private final int invincibilityTicksAfterHit;
  private int invincibilityTicksLeft;
  private final Aabb hurtboxFromBody;
  private int attackCooldownTicksLeft;

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
      throw new IllegalArgumentException(
          "The invincibility after a hit must not be negative, was "
              + invincibilityTicksAfterHit
              + " ticks");
    }
    Objects.requireNonNull(hurtbox, "The hurtbox of a character must not be null");
    this.hurtboxFromBody = hurtbox.translated(-bodyBox.x(), -bodyBox.y());
    this.stats = Objects.requireNonNull(stats, "The stats of a character must not be null");
    this.hp = (int) stats.get(Stat.MAX_HP);
    this.invincibilityTicksAfterHit = invincibilityTicksAfterHit;
  }

  /**
   * Counts the invincibility and the attack cooldown down, then performs character-specific
   * behavior for the tick.
   */
  @Override
  protected final void onUpdate(long tick) {
    if (invincibilityTicksLeft > 0) {
      invincibilityTicksLeft--;
    }
    if (attackCooldownTicksLeft > 0) {
      attackCooldownTicksLeft--;
    }
    think(tick);
  }

  /**
   * Performs character-specific behavior for the given tick.
   *
   * @param tick the number of the current logic tick
   */
  protected abstract void think(long tick);

  @Override
  public Aabb hurtbox() {
    return hurtboxFromBody.translated(bodyBox().x(), bodyBox().y());
  }

  @Override
  public void takeDamage(int amount) {
    if (amount < 1) {
      throw new IllegalArgumentException("Damage amount must be at least 1, was " + amount);
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
      throw new IllegalArgumentException("Heal amount must be at least 1, was " + amount);
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

  /**
   * Grants invincibility for at least the given number of ticks, such as a dodge roll. It never
   * shortens the invincibility already running.
   *
   * @param ticks how many ticks it lasts at least; 0 or more
   * @throws IllegalArgumentException if {@code ticks} is negative
   */
  public void grantInvincibility(int ticks) {
    if (ticks < 0) {
      throw new IllegalArgumentException("Invincibility ticks must not be negative, was " + ticks);
    }
    invincibilityTicksLeft = Math.max(invincibilityTicksLeft, ticks);
  }

  /**
   * Tells whether the character cannot take damage right now (i-frames after a hit, dodge roll).
   *
   * @return true while invincibility ticks are left
   */
  public boolean isInvincible() {
    return invincibilityTicksLeft > 0;
  }

  /**
   * Returns the character's current health.
   *
   * @return the current HP; 0 once dead
   */
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
   * Tells whether this character can start an attack now: it is alive, its attack speed is above 0
   * and its last attack's cooldown is over.
   *
   * @return true if it can attack
   */
  public boolean canAttack() {
    return !isDead() && attackSpeed() > 0 && attackCooldownTicksLeft == 0;
  }

  /**
   * Starts the cooldown of an attack: the character must wait {@code 60 / attack speed} ticks,
   * rounded and at least 1, before the next one. Players and enemies share it (DECISIONS §5).
   *
   * @throws IllegalStateException if the character cannot attack now (see {@link #canAttack})
   */
  public void startAttackCooldown() {
    if (!canAttack()) {
      throw new IllegalStateException("Cannot attack now");
    }
    attackCooldownTicksLeft =
        Math.max(1, Math.round(GameConstants.TICKS_PER_SECOND / attackSpeed()));
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
