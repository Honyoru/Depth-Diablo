package com.epitech.depthdiablo.testdata;

import com.epitech.depthdiablo.combat.AroundContactAttack;
import com.epitech.depthdiablo.combat.ContactAttack;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.BaseStats;
import com.epitech.depthdiablo.entity.MeleeEnemy;
import com.epitech.depthdiablo.entity.Rat;
import com.epitech.depthdiablo.entity.Stat;
import com.epitech.depthdiablo.entity.StatSource;
import com.epitech.depthdiablo.entity.Zombie;
import java.util.EnumMap;
import java.util.Map;

/** Test data builder (Q116) for real enemies: a rat or a zombie, changed one value at a time. */
public final class EnemyBuilder {
  private final Kind kind;
  private final Map<Stat, Float> stats = new EnumMap<>(Stat.class);
  private Aabb bodyBox = new Aabb(0, 0, 16, 16);
  private float reach = 4;
  private boolean summoned;
  private World world = new World();

  private EnemyBuilder(Kind kind, float maxHp, float attack) {
    this.kind = kind;
    stats.put(Stat.MAX_HP, maxHp);
    stats.put(Stat.ATTACK, attack);
    stats.put(Stat.DEFENSE, 1f);
    stats.put(Stat.SPEED, 60f);
    stats.put(Stat.ATTACK_SPEED, 1f);
    stats.put(Stat.LUCK, 0f);
  }

  /**
   * Starts a rat: 3 HP, 2 attack, 1 defense, a 16 x 16 body box and hurtbox at the origin, a reach
   * of 4, not summoned.
   *
   * @return a new builder
   */
  public static EnemyBuilder aRat() {
    return new EnemyBuilder(Rat::new, 3f, 2f);
  }

  /**
   * Starts a zombie: 8 HP, 3 attack, otherwise like {@link #aRat()}.
   *
   * @return a new builder
   */
  public static EnemyBuilder aZombie() {
    return new EnemyBuilder(Zombie::new, 8f, 3f);
  }

  /**
   * Moves the enemy's body box and hurtbox to the given corner.
   *
   * @param x the x of the bottom-left corner
   * @param y the y of the bottom-left corner
   * @return this builder
   */
  public EnemyBuilder at(float x, float y) {
    this.bodyBox = new Aabb(x, y, bodyBox.width(), bodyBox.height());
    return this;
  }

  /**
   * Sets how far its attack goes past its body box.
   *
   * @param value the reach in pixels
   * @return this builder
   */
  public EnemyBuilder withReach(float value) {
    this.reach = value;
    return this;
  }

  /**
   * Marks the enemy as spawned by a summoner.
   *
   * @return this builder
   */
  public EnemyBuilder summoned() {
    this.summoned = true;
    return this;
  }

  /**
   * Sets the world the enemy lives in, to read the events it publishes.
   *
   * @param value the world; a new empty world by default
   * @return this builder
   */
  public EnemyBuilder withWorld(World value) {
    this.world = value;
    return this;
  }

  /**
   * Builds the enemy.
   *
   * @return a new rat or zombie
   */
  public MeleeEnemy build() {
    return kind.create(
        bodyBox, new BaseStats(stats), new AroundContactAttack(reach), bodyBox, 0, summoned, world);
  }

  /** The constructor of a real melee enemy: {@code Rat::new} or {@code Zombie::new}. */
  @FunctionalInterface
  private interface Kind {
    MeleeEnemy create(
        Aabb bodyBox,
        StatSource stats,
        ContactAttack attack,
        Aabb hurtbox,
        int invincibilityTicksAfterHit,
        boolean summoned,
        World world);
  }
}
