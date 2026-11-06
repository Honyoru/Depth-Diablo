package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1 and 1.5: HP, death, invincibility frames and the update template of a Character. */
class CharacterTest {

  @Test
  void should_start_with_full_hp_when_created() {
    Character character = aCharacter().withHp(12).build();

    int hp = character.hp();

    assertEquals(12, hp);
    assertEquals(12, character.maxHp());
  }

  @Test
  void should_lose_hp_when_it_takes_damage() {
    Character character = aCharacter().withHp(10).build();

    character.takeDamage(3);

    assertEquals(7, character.hp());
  }

  @Test
  void should_die_when_hp_reaches_0() {
    Character character = aCharacter().withHp(5).build();

    character.takeDamage(5);

    assertTrue(character.isDead());
  }

  @Test
  void should_stay_alive_when_hp_is_above_0() {
    Character character = aCharacter().withHp(5).build();

    character.takeDamage(4);

    assertFalse(character.isDead());
  }

  @Test
  void should_stop_at_0_hp_when_the_damage_is_higher_than_the_hp() {
    Character character = aCharacter().withHp(5).build();

    character.takeDamage(50);

    assertEquals(0, character.hp());
  }

  @Test
  void should_take_no_more_damage_when_dead() {
    Character character = aCharacter().withHp(5).build();
    character.takeDamage(5);

    character.takeDamage(1);

    assertEquals(0, character.hp());
  }

  @Test
  void should_not_be_invincible_when_created() {
    Character character = aCharacter().build();

    boolean invincible = character.isInvincible();

    assertFalse(invincible);
  }

  @Test
  void should_take_no_damage_when_invincible() {
    Character character = aCharacter().withHp(10).build();
    character.grantInvincibility(30);

    character.takeDamage(3);

    assertEquals(10, character.hp());
  }

  @Test
  void should_ignore_a_second_hit_when_it_lands_during_the_i_frames() {
    Character character = aCharacter().withHp(10).withInvincibilityAfterHit(30).build();
    character.takeDamage(3);

    character.takeDamage(3);

    assertEquals(7, character.hp());
  }

  @Test
  void should_take_damage_again_when_the_i_frames_are_over() {
    Character character = aCharacter().withHp(10).withInvincibilityAfterHit(2).build();
    character.takeDamage(3);
    character.update(1);
    character.update(2);

    character.takeDamage(3);

    assertEquals(4, character.hp());
  }

  @Test
  void should_keep_the_longer_invincibility_when_a_shorter_one_is_granted() {
    Character character = aCharacter().build();
    character.grantInvincibility(10);
    character.grantInvincibility(2);
    character.update(1);
    character.update(2);

    boolean invincible = character.isInvincible();

    assertTrue(invincible);
  }

  @Property
  void should_be_invincible_for_exactly_the_granted_ticks(
      @ForAll @IntRange(min = 1, max = 120) int ticks) {
    Character character = aCharacter().build();
    character.grantInvincibility(ticks);

    for (int t = 1; t < ticks; t++) {
      character.update(t);
    }
    boolean stillInvincible = character.isInvincible();
    character.update(ticks);
    boolean invincibleAfter = character.isInvincible();

    assertTrue(stillInvincible);
    assertFalse(invincibleAfter);
  }

  @Test
  void should_call_think_once_per_update() {
    CountingCharacter character = new CountingCharacter();

    character.update(1);
    character.update(2);
    character.update(3);

    assertEquals(3, character.thinks);
    assertEquals(3, character.lastTick);
  }

  @Test
  void should_return_the_hurtbox_it_was_built_with() {
    Aabb box = new Aabb(4, 8, 16, 16);
    Character character = aCharacter().withHurtbox(box).build();

    Aabb hurtbox = character.hurtbox();

    assertSame(box, hurtbox);
  }

  @Test
  void should_reject_damage_below_1() {
    Character character = aCharacter().build();

    assertThrows(IllegalArgumentException.class, () -> character.takeDamage(0));
  }

  @Test
  void should_reject_a_negative_invincibility() {
    Character character = aCharacter().build();

    assertThrows(IllegalArgumentException.class, () -> character.grantInvincibility(-1));
  }

  @Test
  void should_reject_a_max_hp_below_1() {
    int hp = 0;

    assertThrows(IllegalArgumentException.class, () -> aCharacter().withHp(hp).build());
  }

  @Test
  void should_reject_negative_i_frames() {
    int ticks = -1;

    assertThrows(
        IllegalArgumentException.class,
        () -> aCharacter().withInvincibilityAfterHit(ticks).build());
  }

  @Test
  void should_reject_a_missing_hurtbox() {
    assertThrows(NullPointerException.class, () -> aCharacter().withHurtbox(null).build());
  }

  /** Hand-written fake (Q117): counts the calls to think(). */
  private static final class CountingCharacter extends Character {
    private int thinks;
    private long lastTick;

    CountingCharacter() {
      super(10, new Aabb(0, 0, 16, 16), 0);
    }

    @Override
    protected void think(long tick) {
      thinks++;
      lastTick = tick;
    }
  }
}
