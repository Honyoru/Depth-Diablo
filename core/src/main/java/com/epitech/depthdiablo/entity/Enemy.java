package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;
import java.util.Optional;

/**
 * Every enemy and boss (GDD 1.3.1). Its gdx-ai state machine decides what it does each tick (GDD
 * 1.7); each family fixes how it attacks in {@link #attackTarget} and how far in {@link #canReach}.
 * Its values come from {@code data/characters.json} (Q93) through the {@link CharacterFactory}.
 *
 * <p>Pattern: State ({@link EnemyState} on the gdx-ai state machine) and Template Method ({@link
 * #attackTarget} and {@link #canReach} are the hooks each family fills).
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
   * @param baseAttack its natural attack; must not be {@code null}
   * @param hurtbox the enemy's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param summoned true if a summoner spawned it; a summoned enemy never drops loot (Q16)
   * @param world the world it lives in: its walls stop it, its event bus hears its hits
   * @throws NullPointerException if {@code bodyBox}, {@code stats}, {@code baseAttack}, {@code
   *     hurtbox} or {@code world} is {@code null}
   * @throws IllegalArgumentException if {@code invincibilityTicksAfterHit} is negative
   */
  protected Enemy(
      Aabb bodyBox,
      StatSource stats,
      Attack baseAttack,
      Aabb hurtbox,
      int invincibilityTicksAfterHit,
      boolean summoned,
      World world) {
    super(bodyBox, stats, baseAttack, hurtbox, invincibilityTicksAfterHit, world);
    this.summoned = summoned;
  }

  /**
   * Returns the side every enemy fight fo (GDD 1.5).
   *
   * @return {@link Faction#ENEMY}
   */
  @Override
  public Faction getFaction() {
    return Faction.ENEMY;
  }

  /**
   * Returns the character is enemy class if any.
   *
   * @return the target or empty while it has none
   */
  public Optional<Character> getTarget() {
    return target;
  }

  /**
   * Returns what this enemy is doing now; the renderer picks its animation from it.
   *
   * @return the current state of its state machine; {@link EnemyState#IDLE} before its first tick
   */
  public EnemyState getState() {
    return ai == null ? EnemyState.IDLE : ai.getCurrentState();
  }

  /**
   * Runs one step of the enemy's state machine (GDD 1.7). An enemy whose HP reached 0 goes to
   * {@link EnemyState#DEAD}.
   *
   * @param tick the number of current logic tick
   */
  @Override
  protected void think(long tick) {
    if (ai == null) {
      ai = new DefaultStateMachine<>(this, EnemyState.IDLE);
    }
    if (isDead() && !ai.isInState(EnemyState.DEAD)) {
      ai.changeState(EnemyState.DEAD);
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
    return !target.equals(this) && !target.isDead() && getFaction().canHurt(target.getFaction());
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
   * It also starts the cooldown, through its attack.
   *
   * @param world the world the enemy lives in
   */
  protected abstract void attackTarget(World world);

  /**
   * Tells whether the target is close enough for {@link #attackTarget} to hit it. Each family fixes
   * how far: its reach for a melee enemy.
   *
   * @param target the character to reach
   * @return true if an attack now would hit it
   */
  protected abstract boolean canReach(Character target);

  /**
   * Tells whether this enemy has a target it still sees. Used by its states.
   *
   * @return true if it has a living target on a side it can hurt
   */
  boolean seesTarget() {
    return target.filter(this::canSee).isPresent();
  }

  /**
   * Tells whether this enemy can hit its target from where it stands. Used by its states.
   *
   * @return true if it sees its target and the target is in reach
   */
  boolean reachesTarget() {
    return seesTarget() && canReach(target.orElseThrow());
  }

  /**
   * Takes one step towards the target, at its speed. A wall or another enemy stops the step on its
   * axis only, so the enemy slides along it like the player. Used by its states.
   */
  void stepTowardsTarget() {
    if (!seesTarget()) {
      return;
    }
    Aabb body = getBodyBox();
    Vector2 direction = towards(target.orElseThrow()).nor();
    Vector2 step = direction.scl(getSpeed() / GameConstants.TICKS_PER_SECOND);
    Vector2 position = position();
    if (canStandIn(body.translated(step.x, 0f))) {
      position.x += step.x;
    }
    if (canStandIn(new Aabb(position.x, body.y() + step.y, body.width(), body.height()))) {
      position.y += step.y;
    }
    moveTo(position);
  }

  /**
   * Returns the direction from this enemy's centre to the target's centre, for its attack.
   *
   * @param target the character to aim at
   * @return the aim in degrees (0 = right, 90 = up), from 0 to 360
   * @throws NullPointerException if {@code target} is {@code null}
   */
  float aimAt(Character target) {
    return towards(Objects.requireNonNull(target, "target is null")).angleDeg();
  }

  private Vector2 towards(Character target) {
    Aabb body = getBodyBox();
    Aabb goal = target.getBodyBox();
    return new Vector2(
        goal.x() + goal.width() / 2 - (body.x() + body.width() / 2),
        goal.y() + goal.height() / 2 - (body.y() + body.height() / 2));
  }

  /**
   * Tells whether this enemy can stand in the given box: no wall, and no other living enemy that it
   * does not touch yet. Enemies it already touches are ignored, so two enemies on top of each other
   * can still move apart.
   */
  private boolean canStandIn(Aabb box) {
    if (!world().isFree(box, this)) {
      return false;
    }
    Aabb body = getBodyBox();
    for (Entity other : world().getEntities()) {
      if (other instanceof Enemy enemy
          && !enemy.equals(this)
          && !enemy.isDead()
          && !enemy.isRemoved()
          && !enemy.getBodyBox().overlaps(body)
          && enemy.getBodyBox().overlaps(box)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Attacks the target if the cooldown allows it, then starts the cooldown: one attack per second
   * times its attack speed (GDD 1.6). Used by its states.
   */
  void attackWhenReady() {
    if (canAttack()) {
      attackTarget(world());
    }
  }

  /**
   * Switches this enemy to another state; gdx-ai calls exit() on the old one and enter() on the new
   * one. Used by its states.
   *
   * @param next the state to go to
   */
  void changeState(EnemyState next) {
    ai.changeState(next);
  }

  /**
   * Tells whether a summoner spawned this enemy; a summoned enemy never drops loot (Q16).
   *
   * @return true if it was summoned
   */
  public boolean isSummoned() {
    return summoned;
  }
}
