package com.epitech.depthdiablo.testdata;

import com.epitech.depthdiablo.combat.AroundContactAttack;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Character;
import com.epitech.depthdiablo.entity.stats.BaseStats;
import com.epitech.depthdiablo.entity.stats.Stat;
import com.epitech.depthdiablo.entity.stats.StatSource;
import com.epitech.depthdiablo.geometry.Aabb;
import java.util.EnumMap;
import java.util.Map;

/** Test Data Builder for characters (Q116): {@code aCharacter().withHp(10).build()}. */
public final class CharacterBuilder {
  private Aabb bodyBox = new Aabb(0, 0, 16, 16);
  private final Map<Stat, Float> stats = new EnumMap<>(Stat.class);
  private Aabb hurtbox = new Aabb(0, 0, 16, 16);
  private int invincibilityTicksAfterHit;
  private Faction faction = Faction.ENEMY;
  private World world = new World();

  private CharacterBuilder() {
    stats.put(Stat.MAX_HP, 10f);
    stats.put(Stat.ATTACK, 1f);
    stats.put(Stat.DEFENSE, 1f);
    stats.put(Stat.SPEED, 1f);
    stats.put(Stat.ATTACK_SPEED, 1f);
    stats.put(Stat.LUCK, 0f);
  }

  /**
   * Starts a character with 10 HP, 1 attack, 1 defense, a speed and attack speed of 1, 0 luck, a 16
   * x 16 body box and hurtbox at the origin, no i-frames after a hit, on the ENEMY side.
   *
   * @return a new builder
   */
  public static CharacterBuilder aCharacter() {
    return new CharacterBuilder();
  }

  /**
   * Sets the box the character occupies in the world.
   *
   * @param box the body box
   * @return this builder
   */
  public CharacterBuilder withBodyBox(Aabb box) {
    this.bodyBox = box;
    return this;
  }

  /**
   * Sets the maximum and starting HP.
   *
   * @param hp the HP
   * @return this builder
   */
  public CharacterBuilder withHp(int hp) {
    stats.put(Stat.MAX_HP, (float) hp);
    return this;
  }

  /**
   * Sets the attack.
   *
   * @param value the attack
   * @return this builder
   */
  public CharacterBuilder withAttack(int value) {
    stats.put(Stat.ATTACK, (float) value);
    return this;
  }

  /**
   * Sets the defense.
   *
   * @param value the defense
   * @return this builder
   */
  public CharacterBuilder withDefense(int value) {
    stats.put(Stat.DEFENSE, (float) value);
    return this;
  }

  /**
   * Sets the movement speed.
   *
   * @param value the speed, in pixels per second
   * @return this builder
   */
  public CharacterBuilder withSpeed(float value) {
    stats.put(Stat.SPEED, value);
    return this;
  }

  /**
   * Sets the attack speed.
   *
   * @param value the attacks per second
   * @return this builder
   */
  public CharacterBuilder withAttackSpeed(float value) {
    stats.put(Stat.ATTACK_SPEED, value);
    return this;
  }

  /**
   * Sets the hurtbox.
   *
   * @param box the hurtbox
   * @return this builder
   */
  public CharacterBuilder withHurtbox(Aabb box) {
    this.hurtbox = box;
    return this;
  }

  /**
   * Sets the invincibility granted after each hit.
   *
   * @param ticks the i-frames, in ticks
   * @return this builder
   */
  public CharacterBuilder withInvincibilityAfterHit(int ticks) {
    this.invincibilityTicksAfterHit = ticks;
    return this;
  }

  /**
   * Sets the side the character fights for.
   *
   * @param side its faction
   * @return this builder
   */
  public CharacterBuilder withFaction(Faction side) {
    this.faction = side;
    return this;
  }

  /**
   * Sets the world the character lives in, to read the events it publishes.
   *
   * @param value the world; a new empty world by default
   * @return this builder
   */
  public CharacterBuilder withWorld(World value) {
    this.world = value;
    return this;
  }

  /**
   * Builds the character.
   *
   * @return a character that does nothing on its own
   * @throws IllegalArgumentException if a stat is invalid (see {@link BaseStats})
   */
  public Character build() {
    return new IdleCharacter(
        bodyBox, new BaseStats(stats), hurtbox, invincibilityTicksAfterHit, faction, world);
  }

  /** Hand-written fake (Q117): a character with no behaviour of its own. */
  private static final class IdleCharacter extends Character {
    private final Faction faction;

    IdleCharacter(
        Aabb bodyBox,
        StatSource stats,
        Aabb hurtbox,
        int invincibilityTicksAfterHit,
        Faction faction,
        World world) {
      super(
          bodyBox, stats, new AroundContactAttack(0f), hurtbox, invincibilityTicksAfterHit, world);
      this.faction = faction;
    }

    @Override
    protected void think(long tick) {
      // An idle character never acts.
    }

    @Override
    public Faction getFaction() {
      return faction;
    }
  }
}
