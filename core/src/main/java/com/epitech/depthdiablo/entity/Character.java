package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.combat.Damageable;
import com.epitech.depthdiablo.combat.StatusEffect;
import com.epitech.depthdiablo.combat.Swing;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Base class for player and enemy entities, with shared health and damage handling. The final
 * {@link #onUpdate(long)} method performs common per-tick bookkeeping before delegating
 * character-specific behavior to {@link #think(long)}.
 */
public abstract class Character extends Entity implements Damageable {

  private final CharacterBuild baseBuild;

  private CharacterBuild build;

  private int hp;

  private final int invincibilityTicksAfterHit;

  private int invincibilityTicksLeft;

  private final Aabb hurtboxFromBody;

  private int swingTicksLeft;

  private int attackCooldownTicksLeft;

  private Swing currentSwing;
  private final List<StatusEffect> statuses = new ArrayList<>();
  private final World world;

  /**
   * Creates a character with the specified body box, stats, base attack, hurtbox, and post-hit
   * invincibility duration. Its build starts as its stats and base attack (GDD 1.3.1). It starts
   * with full HP.
   *
   * @param bodyBox the box the character occupies in the world; must not be {@code null}
   * @param stats where its stats come from (GDD 1.6), usually {@link BaseStats}, which checks the
   *     values; must not be {@code null}
   * @param baseAttack its attack before any item: the starting weapon of a player, the natural
   *     attack of an enemy; must not be {@code null}
   * @param hurtbox the character's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit the number of invincibility ticks granted after taking
   *     damage; must not be negative
   * @param world the world it lives in; its event bus gets the damage and death of status effects
   * @throws IllegalArgumentException if {@code invincibilityTicksAfterHit} is negative
   * @throws NullPointerException if {@code bodyBox}, {@code stats}, {@code baseAttack}, {@code
   *     hurtbox} or {@code world} is {@code null}
   */
  protected Character(
      Aabb bodyBox,
      StatSource stats,
      Attack baseAttack,
      Aabb hurtbox,
      int invincibilityTicksAfterHit,
      World world) {
    super(bodyBox);
    if (invincibilityTicksAfterHit < 0) {
      throw new IllegalArgumentException(
          "The invincibility after a hit must not be negative, was "
              + invincibilityTicksAfterHit
              + " ticks");
    }
    Objects.requireNonNull(hurtbox, "The hurtbox of a character must not be null");
    this.hurtboxFromBody = hurtbox.translated(-bodyBox.x(), -bodyBox.y());
    this.baseBuild = new CharacterBuild(baseAttack, stats);
    this.build = baseBuild;
    this.hp = (int) stats.get(Stat.MAX_HP);
    this.invincibilityTicksAfterHit = invincibilityTicksAfterHit;
    this.world = Objects.requireNonNull(world, "...");
  }

  @Override
  public Aabb getHurtbox() {
    return hurtboxFromBody.translated(getBodyBox().x(), getBodyBox().y());
  }

  /**
   * Returns the character's current health.
   *
   * @return the current HP; 0 once dead
   */
  public int getHp() {
    return hp;
  }

  /**
   * Returns the base damage of this character's hits, used as the attack in the damage formula.
   *
   * @return the attack, rounded down
   */
  public int getAttack() {
    return (int) build.stats().get(Stat.ATTACK);
  }

  /**
   * Returns the defense this character opposes to hits, used as the resistance (the divisor) in the
   * damage formula.
   *
   * @return the defense, rounded down
   */
  public int getDefense() {
    return (int) build.stats().get(Stat.DEFENSE);
  }

  /**
   * Returns how fast this character moves.
   *
   * @return the speed in pixels per second
   */
  public float getSpeed() {
    return build.stats().get(Stat.SPEED);
  }

  /**
   * Returns this character's attack speed multiplier. The weapon gives the base attack speed and
   * this stat multiplies it; passive items will change it later.
   *
   * @return the multiplier; 0 means it never attacks
   */
  public float getAttackSpeed() {
    return build.stats().get(Stat.ATTACK_SPEED);
  }

  /**
   * Returns the swing running now, for the renderer.
   *
   * @return the current swing, or empty if this character is not swinging or its swing is over
   */
  public Optional<Swing> getCurrentSwing() {
    return Optional.ofNullable(currentSwing).filter(swing -> !swing.isOver());
  }

  /**
   * Returns the character's maximum health.
   *
   * @return the maximum HP, rounded down
   */
  public int getMaxHp() {
    return (int) build.stats().get(Stat.MAX_HP);
  }

  /**
   * Returns what this character fights with now: its attack and its stats (GDD 1.3.1).
   *
   * @return the current build
   */
  public CharacterBuild getBuild() {
    return build;
  }

  /**
   * Ticks the status effects and drops the ones that are over (all of them once dead), counts the
   * invincibility down, runs the current swing, counts the swing of the current attack down, then
   * its cooldown once the swing is over, and performs character-specific behavior for the tick.
   */
  @Override
  protected final void onUpdate(long tick) {
    for (StatusEffect effect : List.copyOf(statuses)) {
      effect.tick(this);
    }
    statuses.removeIf(StatusEffect::isOver);
    if (isDead()) {
      statuses.clear();
    }
    if (invincibilityTicksLeft > 0) {
      invincibilityTicksLeft--;
    }
    if (currentSwing != null) {
      currentSwing.update(tick); // does nothing once the swing is over
    }
    if (swingTicksLeft > 0) {
      swingTicksLeft--;
    } else if (attackCooldownTicksLeft > 0) {
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
    hp = Math.min(getMaxHp(), hp + amount);
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
   * Tells whether this character can start an attack now: it is alive, its attack speed is above 0,
   * and its last attack's swing and cooldown are over.
   *
   * @return true if it can attack
   */
  public boolean canAttack() {
    return !isDead() && getAttackSpeed() > 0 && swingTicksLeft == 0 && attackCooldownTicksLeft == 0;
  }

  /**
   * Starts an attack: the swing runs first, then the character waits {@code 60 / (weapon attack
   * speed × its own attack speed)} ticks, rounded and at least 1, before the next one. The cooldown
   * only counts down once the swing is over, so two swings never overlap (DECISIONS §5).
   *
   * @param swingTicks how many ticks the swing of the weapon lasts; 0 for an attack with no swing
   * @param weaponAttackSpeed the base attack speed of the weapon; above 0 and finite
   * @throws IllegalArgumentException if {@code swingTicks} is negative or {@code weaponAttackSpeed}
   *     is not a finite number above 0
   * @throws IllegalStateException if the character cannot attack now (see {@link #canAttack})
   */
  public void startAttackCooldown(int swingTicks, float weaponAttackSpeed) {
    if (swingTicks < 0) {
      throw new IllegalArgumentException("Swing ticks must not be negative: " + swingTicks);
    }
    if ((weaponAttackSpeed <= 0)
        || Float.isNaN(weaponAttackSpeed)
        || Float.isInfinite(weaponAttackSpeed)) {
      throw new IllegalArgumentException(
          "Weapon attack speed must be a finite number above 0: " + weaponAttackSpeed);
    }
    if (!canAttack()) {
      throw new IllegalStateException("Cannot attack now");
    }
    swingTicksLeft = swingTicks;
    attackCooldownTicksLeft =
        Math.max(
            1, Math.round(GameConstants.TICKS_PER_SECOND / (weaponAttackSpeed * getAttackSpeed())));
  }

  /**
   * Attacks towards the aim with a weapon, whatever its kind: a sword starts a swing, a bow will
   * shoot an arrow. A player or an enemy calls it from {@link #think(long)}, once {@link
   * #canAttack()} is true.
   *
   * @param weapon the attack to start, usually the equipped weapon
   * @param aimDegrees the direction of the attack, in degrees (0 = right, 90 = up)
   * @param world the world the attack happens in
   * @throws NullPointerException if an argument is {@code null}
   * @throws IllegalArgumentException if the aim is not a finite number
   * @throws IllegalStateException if this character cannot attack now (see {@link #canAttack})
   */
  protected final void startAttack(Attack weapon, float aimDegrees, World world) {
    Objects.requireNonNull(weapon, "weapon is null");
    weapon.attack(this, aimDegrees, world);
  }

  /**
   * Takes a swing that a melee weapon just started for this character. The swing then runs during
   * this character's next updates.
   *
   * @param swing the swing to run; its holder must be this character
   * @throws NullPointerException if {@code swing} is {@code null}
   * @throws IllegalArgumentException if the swing belongs to another character
   * @throws IllegalStateException if this character is still swinging
   */
  public final void holdSwing(Swing swing) {
    Objects.requireNonNull(swing, "swing is null");
    if (!equals(swing.getHolder())) {
      throw new IllegalArgumentException("The swing belongs to another character");
    }
    if (getCurrentSwing().isPresent()) {
      throw new IllegalStateException("Still swinging");
    }
    currentSwing = swing;
  }

  /**
   * Adds a status effect, such as a burn (GDD 1.3.1). It ticks from the next update until it is
   * over. Effects stack: two burns both deal their damage. Does nothing once the character is dead.
   *
   * @param effect the effect to add
   * @throws NullPointerException if {@code effect} is {@code null}
   */
  public void applyStatus(StatusEffect effect) {
    Objects.requireNonNull(effect, "status effect is null");
    if (isDead()) {
      return;
    }
    statuses.add(effect);
  }

  /**
   * Takes damage from a status effect, such as a burn tick (GDD 1.3.1). Unlike {@link
   * #takeDamage(int)}, it ignores invincibility and grants none: a burn keeps ticking during the
   * frames after a hit, and never blocks the next hit. The amount is applied as is, without the
   * damage formula. It publishes a {@link DamageDealt}, then an {@link EntityDied} if it kills,
   * both credited to the source. Does nothing once the character is dead.
   *
   * @param amount the HP to lose; must be at least 1
   * @param source the character who applied the effect
   * @throws IllegalArgumentException if {@code amount} is less than 1
   * @throws NullPointerException if {@code source} is {@code null}
   */
  public void takeStatusDamage(int amount, Character source) {
    if (amount < 1) {
      throw new IllegalArgumentException("Status damage amount must be at least 1, was " + amount);
    }
    Objects.requireNonNull(source, "The source of status damage must not be null");
    if (isDead()) {
      return;
    }
    hp = amount >= hp ? 0 : hp - amount;
    world.getEventBus().publish(new DamageDealt(source, this, amount));
    if (isDead()) {
      world.getEventBus().publish(new EntityDied(this, Optional.of(source)));
    }
  }

  /**
   * Returns the world this character lives in.
   *
   * @return the world given at creation
   */
  protected World world() {
    return world;
  }

  /**
   * Rebuilds what this character fights with (GDD 1.3.1): starts again from its base build and
   * applies each modifier in order (weapon, then armor, then passive items in pickup order). If the
   * new max HP is below the current HP, the HP is cut down to it.
   *
   * @param modifiers the modifiers in the order to apply them; an empty list gives the base build
   * @throws NullPointerException if {@code modifiers} or one of them is {@code null}
   */
  public final void rebuild(List<BuildModifier> modifiers) {
    Objects.requireNonNull(modifiers, "modifiers is null");
    CharacterBuild result = baseBuild;
    for (BuildModifier modifier : modifiers) {
      result = Objects.requireNonNull(modifier, "modifier is null").modify(result);
    }
    build = result;
    hp = Math.min(hp, getMaxHp());
  }
}
