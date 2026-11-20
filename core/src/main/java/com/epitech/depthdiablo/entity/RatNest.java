package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.engine.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The rat nest (GDD 1.3): a static summoner that spawns rats while it sees its target. Its attack
 * speed is its summon rate (0.25 = one rat every 4 seconds) and it keeps at most {@code maxSummons}
 * of its rats alive; a dead rat frees a slot. Its rats are summoned, so they never drop loot (Q16).
 * It never moves: its speed is 0 and it never chases.
 *
 * <p>Pattern: Template Method (fills the {@link Enemy} hooks for a summoner).
 */
public final class RatNest extends Enemy {
  /** The nest never strikes: summoning is its attack, done in {@link #attackTarget}. */
  private static final Attack NO_STRIKE = (owner, aimDegrees, world) -> {};

  private final String summonId;
  private final int maxSummons;
  private final Summoner summoner;
  private final List<Enemy> summons = new ArrayList<>();

  /**
   * Creates a rat nest.
   *
   * @param setup the values every enemy shares; must not be {@code null}
   * @param summonId the id of the enemy it spawns, e.g. {@code "rat"}; must not be blank
   * @param maxSummons how many of its summons may be alive at once; 1 or more
   * @param summoner builds the enemies it spawns; must not be {@code null}
   * @throws NullPointerException if {@code setup}, {@code summonId} or {@code summoner} is {@code
   *     null}
   * @throws IllegalArgumentException if {@code summonId} is blank or {@code maxSummons} is below 1
   */
  public RatNest(EnemySetup setup, String summonId, int maxSummons, Summoner summoner) {
    super(setup, NO_STRIKE);
    Objects.requireNonNull(summonId, "summonId is null");
    if (summonId.isBlank()) {
      throw new IllegalArgumentException("The id a rat nest summons must not be blank");
    }
    if (maxSummons < 1) {
      throw new IllegalArgumentException(
          "A rat nest must allow at least 1 summon, was " + maxSummons);
    }
    this.summonId = summonId;
    this.maxSummons = maxSummons;
    this.summoner = Objects.requireNonNull(summoner, "summoner is null");
  }

  /**
   * Returns how many of the enemies it spawned are still alive.
   *
   * @return 0 to {@code maxSummons}
   */
  public int getLivingSummons() {
    return (int) summons.stream().filter(summon -> !summon.isDead()).count();
  }

  /**
   * A nest summons when it sees a target.
   *
   * @return {@link EnemyState#SUMMON}
   */
  @Override
  protected EnemyState engagedState() {
    return EnemyState.SUMMON;
  }

  /**
   * Spawns one enemy on its centre, alerted to its target, unless it has no target it sees or
   * already has {@code maxSummons} alive. It starts the cooldown either way, so it tries again
   * after the next interval.
   *
   * @param world the world the nest lives in; the summon is added to it
   */
  @Override
  protected void attackTarget(World world) {
    startAttackCooldown(0);
    summons.removeIf(Enemy::isDead);
    Optional<Character> target = getTarget().filter(this::canSee);
    if (target.isEmpty() || summons.size() >= maxSummons) {
      return;
    }
    Aabb body = getBodyBox();
    Vector2 centre = new Vector2(body.x() + body.width() / 2f, body.y() + body.height() / 2f);
    Enemy summon = summoner.summon(summonId, centre);
    summon.alert(target.get());
    world.add(summon);
    summons.add(summon);
  }

  /**
   * A nest summons whatever the distance: it reaches every target it sees.
   *
   * @param target the character to reach
   * @return true if it sees the target
   */
  @Override
  protected boolean canReach(Character target) {
    return canSee(target);
  }
}
