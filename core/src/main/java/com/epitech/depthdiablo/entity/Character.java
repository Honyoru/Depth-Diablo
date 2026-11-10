package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.Damageable;
import java.util.Objects;

/**
 * Base class for player and enemy entities, with shared health and damage handling. The final
 * {@link #onUpdate(long)} method performs common per-tick bookkeeping before delegating
 * character-specific behavior to {@link #think(long)}.
 */
public abstract class Character extends Entity implements Damageable {
  private final int maxHp;
  private int hp;
  private final int attack;
  private final int resistance;
  private final float speed;
  private final float attackSpeed;
  private final Aabb hurtbox;
  private final int invincibilityTicksAfterHit;
  private int invincibilityTicksLeft;

  /**
   * Creates a character with the specified body box, health, combat stats, hurtbox, and post-hit
   * invincibility duration.
   *
   * @param bodyBox the box the character occupies in the world; must not be {@code null}
   * @param maxHp the character's maximum health; must be at least 1
   * @param attack the base damage of its hits in the damage formula (GDD 1.6); must not be negative
   * @param resistance the divisor of the damage it takes in the damage formula (GDD 1.6); must be
   *     at least 1
   * @param speed the movement speed in pixels per second (GDD 1.6); 0 or more, finite
   * @param attackSpeed the attacks per second (GDD 1.6); 0 or more, finite
   * @param hurtbox the character's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit the number of invincibility ticks granted after taking
   *     damage; must not be negative
   * @throws IllegalArgumentException if {@code maxHp} is less than 1, {@code attack} is negative,
   *     {@code resistance} is less than 1, {@code speed} or {@code attackSpeed} is negative or not
   *     a finite number, or {@code invincibilityTicksAfterHit} is negative
   * @throws NullPointerException if {@code bodyBox} or {@code hurtbox} is {@code null}
   */
  protected Character(
      Aabb bodyBox,
      int maxHp,
      int attack,
      int resistance,
      float speed,
      float attackSpeed,
      Aabb hurtbox,
      int invincibilityTicksAfterHit) {
    super(bodyBox);
    if (maxHp < 1) {
      throw new IllegalArgumentException("maxHp must be at least 1");
    }
    if (attack < 0) {
      throw new IllegalArgumentException("attack must not be negative");
    }
    if (resistance < 1) {
      throw new IllegalArgumentException("resistance must be at least 1");
    }
    requireFiniteAndNotNegative(speed, "speed");
    requireFiniteAndNotNegative(attackSpeed, "attackSpeed");
    if (invincibilityTicksAfterHit < 0) {
      throw new IllegalArgumentException("invincibilityTicksAfterHit must not be negative");
    }
    this.maxHp = maxHp;
    this.hp = maxHp;
    this.attack = attack;
    this.resistance = resistance;
    this.speed = speed;
    this.attackSpeed = attackSpeed;
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
    hp = Math.min(maxHp, hp + amount);
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
   * @return the attack, 0 or more
   */
  public int attack() {
    return attack;
  }

  /**
   * Returns the resistance this character opposes to hits, used as the divisor in the damage
   * formula.
   *
   * @return the resistance, at least 1
   */
  public int resistance() {
    return resistance;
  }

  /**
   * Returns how fast this character moves.
   *
   * @return the speed in pixels per second, 0 or more
   */
  public float speed() {
    return speed;
  }

  /**
   * Returns how often this character can attack. The weapon and passive items will change it later.
   *
   * @return the attacks per second, 0 or more
   */
  public float attackSpeed() {
    return attackSpeed;
  }

  /** Returns the character's maximum health. */
  public int maxHp() {
    return maxHp;
  }

  private static void requireFiniteAndNotNegative(float value, String name) {
    if (value < 0 || Float.isNaN(value) || Float.isInfinite(value)) {
      throw new IllegalArgumentException(name + " must be a finite number of 0 or more: " + value);
    }
  }
}
