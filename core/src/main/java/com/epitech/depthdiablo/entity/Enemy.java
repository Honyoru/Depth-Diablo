package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;
import java.util.Optional;

/**
 * Every enemy and boss (GDD 1.3.1). Its gdx-ai state machine decides what it does each tick (GDD
 * 1.7); each family fixes how it attacks in {@link #attackTarget}. Its values come from {@code
 * data/enemies.json} (Q93) through the EnemyFactory.
 *
 * <p>Pattern: State ({@link EnemyState} on the gdx-ai state machine) and Template Method ({@link
 * #attackTarget} is the hook each family fills).
 */

public abstract class Enemy extends Character {
    private StateMachine<Enemy, EnemyState> ai;
    private Optional<Character> target = Optional.empty();
    private final boolean summoned;


    /**
   * Creates an enemy. It starts {@link EnemyState#IDLE}, with no target.
   *
   * @param bodyBox the box the enemy occupies in the world; must not be {@code null}
   * @param stats where its stats come from (GDD 1.6); must not be {@code null}
   * @param hurtbox the enemy's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param summoned true if a summoner spawned it; a summoned enemy never drops loot (Q16)
   * @throws NullPointerException if {@code bodyBox}, {@code stats} or {@code hurtbox} is {@code
   *     null}
   * @throws IllegalArgumentException if {@code invincibilityTicksAfterHit} is negative
   */
  protected Enemy(
    Aabb bodyBox,
    StatSource stats,
    Aabb hurtbox,
    int invincibilityTicksAfterHit,
    boolean summoned){
      super(bodyBox, stats, hurtbox, invincibilityTicksAfterHit);
      this.summoned = summoned;
    }

  /**
   * Runs one step of the enemy's state machine (GDD 1.7).
   *
   * @param tick the number of current logic tick
   */
  @Override
  protected void think(long tick){
    if(ai == null){
      ai = new DefaultStateMachine<>(this, EnemyState.IDLE);
    }
    ai.update();
  }

  /**
   * Tells whether this enemy sees a target: alive and on a side it can hurt. The detection range is
   * still open (GDD 1.7); line of sight comes with the labyrinth (Q51).
   *
   * @param target the character to look at
   * @return true if it can see and attack it
   * @throws NullPointerException if {@code target} is {@code null}
   */
  public boolean canSee(Character target) {
    Objects.requireNonNull(target, "Target is null!");
    return !target.equals(this) && !target.isDead() && faction().canHurt(target.faction());
  }

  /**
   * Gives this enemy a target to chase: the signal sent to an area's enemies when the player enters
   * it (Q51), or a freshly spawned wave. A target it cannot see (dead, or on its own side) is
   * ignored.
   *
   * @param target the character to chase
   * @throws NullPointerException if {@code target} is {@code null}
   */
  public void alert(Character target) {
    if (canSee(target)) {
      this.target = Optional.of(target);
    }
  }

  /**
   * Attacks the target if it can. Each family fixes how: contact, projectile or summon (GDD 1.3.1).
   *
   * @param world the world the enemy lives in
   */
  protected abstract void attackTarget(World world);

  /**
   * Returns the side every enemy fight fo (GDD 1.5).
   *
   * @return {@Link Faction#ENEMY}
   */
  @Override
  public Faction faction() {
    return Faction.ENEMY;
  }

  /**
   * Returns the character is enemy class if any.
   *
   * @return the target or empty while it has none
   */
  public Optional<Character> target() {
    return target;
  }

  /**
   * Returns what this enemy is doing now; the renderer picks its animation from it.
   *
   * @return the current state of its state machine; {@link EnemyState#IDLE} before its first tick
   */
  public EnemyState state() {
    return ai == null ? EnemyState.IDLE : ai.getCurrentState();
  }

  /**
   * Tells whether a summoner spawned this enemy; a summoned enemy never drops loot (Q16).
   *
   * @return true if it was summoned
   */
  public boolean isSummoned(){
    return summoned;
  }
}
