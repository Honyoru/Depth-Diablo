package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.entity.Character;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** GDD 1.5: a hitbox is the area of an attack; it never hits its owner. */
class HitboxTest {
  private static final Obb SWING = new Obb(30, 8, 0, 24, 8);

  @Test
  void should_keep_its_shape_owner_and_lifetime() {
    Character owner = aCharacter().build();

    Hitbox hitbox = new Hitbox(SWING, owner, 12);

    assertSame(SWING, hitbox.shape());
    assertSame(owner, hitbox.owner());
    assertEquals(12, hitbox.ticksLeft());
  }

  @Test
  void should_throw_when_the_shape_is_null() {
    Character owner = aCharacter().build();

    NullPointerException error =
        assertThrows(NullPointerException.class, () -> new Hitbox(null, owner, 12));

    assertEquals("shape is null", error.getMessage());
  }

  @Test
  void should_throw_when_the_owner_is_null() {
    NullPointerException error =
        assertThrows(NullPointerException.class, () -> new Hitbox(SWING, null, 12));

    assertEquals("owner is null", error.getMessage());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1})
  void should_throw_when_the_lifetime_is_not_above_zero(int lifetimeTicks) {
    Character owner = aCharacter().build();

    assertThrows(IllegalArgumentException.class, () -> new Hitbox(SWING, owner, lifetimeTicks));
  }

  @Test
  void should_not_be_expired_when_created() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), 12);

    assertFalse(hitbox.isExpired());
  }

  @Test
  void should_lose_one_tick_on_each_update() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), 12);

    hitbox.update(1);

    assertEquals(11, hitbox.ticksLeft());
    assertFalse(hitbox.isExpired());
  }

  @Test
  void should_expire_after_its_lifetime() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), 12);

    for (long tick = 1; tick <= 12; tick++) {
      hitbox.update(tick);
    }

    assertEquals(0, hitbox.ticksLeft());
    assertTrue(hitbox.isExpired());
  }

  @Test
  void should_never_go_below_zero_ticks() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), 1);

    hitbox.update(1);
    hitbox.update(2);
    hitbox.update(3);

    assertEquals(0, hitbox.ticksLeft());
    assertTrue(hitbox.isExpired());
  }
}
