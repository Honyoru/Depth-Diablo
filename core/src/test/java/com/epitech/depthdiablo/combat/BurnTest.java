package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.entity.character.Character;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: a burn deals its damage every interval until its duration is over. */
class BurnTest {
  private final Character source = aCharacter().build();

  @Test
  void should_deal_no_damage_before_the_first_interval() {
    Character target = aCharacter().withHp(10).build();
    Burn burn = new Burn(new DotSpec(2, 3, 9), source);

    burn.tick(target);
    burn.tick(target);

    assertEquals(10, target.getHp());
  }

  @Test
  void should_deal_its_damage_at_the_end_of_each_interval() {
    Character target = aCharacter().withHp(10).build();
    Burn burn = new Burn(new DotSpec(2, 3, 9), source);

    for (int i = 0; i < 3; i++) {
      burn.tick(target);
    }

    assertEquals(8, target.getHp());
  }

  @Test
  void should_deal_its_damage_once_per_interval_over_its_whole_duration() {
    Character target = aCharacter().withHp(10).build();
    Burn burn = new Burn(new DotSpec(2, 3, 9), source);

    for (int i = 0; i < 9; i++) {
      burn.tick(target);
    }

    assertEquals(4, target.getHp());
    assertTrue(burn.isOver());
  }

  @Test
  void should_deal_no_more_damage_once_it_is_over() {
    Character target = aCharacter().withHp(10).build();
    Burn burn = new Burn(new DotSpec(2, 3, 9), source);
    for (int i = 0; i < 9; i++) {
      burn.tick(target);
    }

    for (int i = 0; i < 6; i++) {
      burn.tick(target);
    }

    assertEquals(4, target.getHp());
  }

  @Test
  void should_deal_damage_when_the_target_is_invincible() {
    Character target = aCharacter().withHp(10).build();
    target.grantInvincibility(100);
    Burn burn = new Burn(new DotSpec(2, 1, 1), source);

    burn.tick(target);

    assertEquals(8, target.getHp());
  }

  @Test
  void should_not_make_the_target_invincible() {
    Character target = aCharacter().withHp(10).withInvincibilityAfterHit(30).build();
    Burn burn = new Burn(new DotSpec(2, 1, 1), source);

    burn.tick(target);

    assertFalse(target.isInvincible());
  }

  @Test
  void should_last_the_duration_of_its_spec() {
    Burn burn = new Burn(new DotSpec(2, 3, 9), source);

    assertEquals(9, burn.ticksLeft());
  }

  @Test
  void should_keep_the_character_who_applied_it() {
    Burn burn = new Burn(new DotSpec(2, 3, 9), source);

    assertSame(source, burn.getSource());
  }

  @Test
  void should_throw_when_the_spec_is_null() {
    assertThrows(NullPointerException.class, () -> new Burn(null, source));
  }

  @Test
  void should_throw_when_the_source_is_null() {
    assertThrows(NullPointerException.class, () -> new Burn(new DotSpec(2, 3, 9), null));
  }
}
