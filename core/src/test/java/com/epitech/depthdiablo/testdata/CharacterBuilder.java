package com.epitech.depthdiablo.testdata;

import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;

/** Test Data Builder for characters (Q116): {@code aCharacter().withHp(10).build()}. */
public final class CharacterBuilder {
  private Aabb bodyBox = new Aabb(0, 0, 16, 16);
  private int maxHp = 10;
  private Aabb hurtbox = new Aabb(0, 0, 16, 16);
  private int invincibilityTicksAfterHit;

  private CharacterBuilder() {}

  /**
   * Starts a character with 10 HP, a 16 x 16 body box and hurtbox at the origin and no i-frames
   * after a hit.
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
    this.maxHp = hp;
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
   * Builds the character.
   *
   * @return a character that does nothing on its own
   */
  public Character build() {
    return new IdleCharacter(bodyBox, maxHp, hurtbox, invincibilityTicksAfterHit);
  }

  /** Hand-written fake (Q117): a character with no behaviour of its own. */
  private static final class IdleCharacter extends Character {
    IdleCharacter(Aabb bodyBox, int maxHp, Aabb hurtbox, int invincibilityTicksAfterHit) {
      super(bodyBox, maxHp, hurtbox, invincibilityTicksAfterHit);
    }

    @Override
    protected void think(long tick) {
      // An idle character never acts.
    }
  }
}
