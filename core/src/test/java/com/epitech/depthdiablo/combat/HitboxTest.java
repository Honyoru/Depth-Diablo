package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** GDD 1.5: a hitbox is the area of an attack; it never hits its owner. */
class HitboxTest {
  private static final Obb SWING = new Obb(30, 8, 0, 24, 8);
  private static final Aabb IN_REACH = new Aabb(20, 0, 16, 16);
  private static final HitResult LANDED = new HitResult(3, false);

  @Test
  void should_keep_its_shape_owner_faction_and_lifetime() {
    Character owner = aCharacter().build();

    Hitbox hitbox = new Hitbox(SWING, owner, Faction.PLAYER, 12);

    assertSame(SWING, hitbox.getShape());
    assertSame(owner, hitbox.getOwner());
    assertEquals(Faction.PLAYER, hitbox.getFaction());
    assertEquals(12, hitbox.getTicksLeft());
  }

  @Test
  void should_throw_when_the_shape_is_null() {
    Character owner = aCharacter().build();

    NullPointerException error =
        assertThrows(NullPointerException.class, () -> new Hitbox(null, owner, Faction.PLAYER, 12));

    assertEquals("The shape of a hitbox must not be null", error.getMessage());
  }

  @Test
  void should_throw_when_the_owner_is_null() {
    NullPointerException error =
        assertThrows(NullPointerException.class, () -> new Hitbox(SWING, null, Faction.PLAYER, 12));

    assertEquals("The owner of a hitbox must not be null", error.getMessage());
  }

  @Test
  void should_throw_when_the_faction_is_null() {
    Character owner = aCharacter().build();

    NullPointerException error =
        assertThrows(NullPointerException.class, () -> new Hitbox(SWING, owner, null, 12));

    assertEquals("The faction of a hitbox must not be null", error.getMessage());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1})
  void should_throw_when_the_lifetime_is_not_above_zero(int lifetimeTicks) {
    Character owner = aCharacter().build();

    assertThrows(
        IllegalArgumentException.class,
        () -> new Hitbox(SWING, owner, Faction.PLAYER, lifetimeTicks));
  }

  @Test
  void should_not_be_expired_when_created() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    assertFalse(hitbox.isExpired());
  }

  @Test
  void should_lose_one_tick_on_each_update() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    hitbox.update(1);

    assertEquals(11, hitbox.getTicksLeft());
    assertFalse(hitbox.isExpired());
  }

  @Test
  void should_expire_after_its_lifetime() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    for (long tick = 1; tick <= 12; tick++) {
      hitbox.update(tick);
    }

    assertEquals(0, hitbox.getTicksLeft());
    assertTrue(hitbox.isExpired());
  }

  @Test
  void should_never_go_below_zero_ticks() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 1);

    hitbox.update(1);
    hitbox.update(2);
    hitbox.update(3);

    assertEquals(0, hitbox.getTicksLeft());
    assertTrue(hitbox.isExpired());
  }

  @Test
  void should_hit_an_enemy_its_shape_overlaps() {
    Character target = aCharacter().withHurtbox(IN_REACH).build();
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    assertTrue(hitbox.canHit(target));
  }

  @Test
  void should_not_hit_a_target_out_of_its_shape() {
    Character target = aCharacter().withHurtbox(new Aabb(100, 0, 16, 16)).build();
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    assertFalse(hitbox.canHit(target));
  }

  @Test
  void should_never_hit_its_owner() {
    Character owner = aCharacter().withHurtbox(IN_REACH).build();
    Hitbox hitbox = new Hitbox(SWING, owner, Faction.NEUTRAL, 12);

    assertFalse(hitbox.canHit(owner));
  }

  @Test
  void should_not_hit_its_own_side() {
    Character ally = aCharacter().withFaction(Faction.PLAYER).withHurtbox(IN_REACH).build();
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    assertFalse(hitbox.canHit(ally));
  }

  @Test
  void should_not_hit_once_expired() {
    Character target = aCharacter().withHurtbox(IN_REACH).build();
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 1);

    hitbox.update(1);

    assertFalse(hitbox.canHit(target));
  }

  @Test
  void should_not_hit_a_dead_target() {
    Character target = aCharacter().withHp(1).withHurtbox(IN_REACH).build();
    target.takeDamage(1);
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    assertFalse(hitbox.canHit(target));
  }

  @Test
  void should_throw_when_the_target_to_check_is_null() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    assertThrows(NullPointerException.class, () -> hitbox.canHit(null));
  }

  @Test
  void should_not_hit_the_same_target_twice() {
    Character target = aCharacter().withHurtbox(IN_REACH).build();
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    hitbox.recordHit(target, LANDED);

    assertFalse(hitbox.canHit(target));
  }

  @Test
  void should_still_hit_another_target_after_a_hit() {
    Character first = aCharacter().withHurtbox(IN_REACH).build();
    Character second = aCharacter().withHurtbox(IN_REACH).build();
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    hitbox.recordHit(first, LANDED);

    assertTrue(hitbox.canHit(second));
  }

  @Test
  void should_still_hit_a_target_its_hit_did_not_land_on() {
    Character target = aCharacter().withHurtbox(IN_REACH).build();
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    hitbox.recordHit(target, HitResult.NONE);

    assertTrue(hitbox.canHit(target));
  }

  @Test
  void should_throw_when_the_target_to_record_is_null() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    assertThrows(NullPointerException.class, () -> hitbox.recordHit(null, LANDED));
  }

  @Test
  void should_throw_when_the_result_to_record_is_null() {
    Character target = aCharacter().build();
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    assertThrows(NullPointerException.class, () -> hitbox.recordHit(target, null));
  }

  @Test
  void should_cover_its_new_shape_after_a_reshape() {
    Obb turned = new Obb(30, 8, 90, 24, 8);
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    hitbox.reshape(turned);

    assertSame(turned, hitbox.getShape());
  }

  @Test
  void should_hit_a_target_its_new_shape_reaches() {
    Character target = aCharacter().withHurtbox(new Aabb(100, 0, 16, 16)).build();
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    hitbox.reshape(new Obb(108, 8, 0, 24, 8));

    assertTrue(hitbox.canHit(target));
  }

  @Test
  void should_keep_its_hits_and_lifetime_after_a_reshape() {
    Character target = aCharacter().withHurtbox(IN_REACH).build();
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);
    hitbox.recordHit(target, LANDED);
    hitbox.update(1);

    hitbox.reshape(new Obb(30, 8, 45, 24, 8));

    assertFalse(hitbox.canHit(target));
    assertEquals(11, hitbox.getTicksLeft());
  }

  @Test
  void should_throw_when_the_new_shape_is_null() {
    Hitbox hitbox = new Hitbox(SWING, aCharacter().build(), Faction.PLAYER, 12);

    assertThrows(NullPointerException.class, () -> hitbox.reshape(null));
  }
}
