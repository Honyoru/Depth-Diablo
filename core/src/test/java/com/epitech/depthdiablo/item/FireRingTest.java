package com.epitech.depthdiablo.item;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.combat.BurnSpec;
import com.epitech.depthdiablo.combat.FireTouch;
import com.epitech.depthdiablo.combat.HitResult;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.CharacterBuild;
import com.epitech.depthdiablo.entity.StatSource;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1 and 1.4.1: the fire ring makes every landed hit of its holder burn the target. */
class FireRingTest {
  private static final BurnSpec BURN = new BurnSpec(2, 1, 3);

  private final World world = new World();
  private final Character attacker = aCharacter().withWorld(world).build();
  private final Character target = aCharacter().withHp(10).withWorld(world).build();
  private final StatSource stats = stat -> 1f;

  @Test
  void should_have_the_passive_rarity() {
    FireRing ring = aFireRing();

    assertEquals(Rarity.PASSIVE, ring.getRarity());
  }

  @Test
  void should_keep_its_values_when_created() {
    FireRing ring = aFireRing();

    assertEquals("fire_ring", ring.getId());
    assertEquals("item.fire_ring.name", ring.getNameKey());
    assertEquals("items/fire_ring", ring.getSpriteId());
    assertEquals(0, ring.getPrice());
    assertEquals(BURN, ring.getBurn());
  }

  @Test
  void should_wrap_the_attack_in_a_fire_touch() {
    Attack decorated = aFireRing().decorateAttack(new FixedAttack(HitResult.NONE));

    assertInstanceOf(FireTouch.class, decorated);
  }

  @Test
  void should_make_the_attack_of_the_build_a_fire_touch() {
    CharacterBuild build = new CharacterBuild(new FixedAttack(HitResult.NONE), stats);

    CharacterBuild modified = aFireRing().modify(build);

    assertInstanceOf(FireTouch.class, modified.attack());
  }

  @Test
  void should_leave_the_stats_of_the_build_unchanged() {
    CharacterBuild build = new CharacterBuild(new FixedAttack(HitResult.NONE), stats);

    CharacterBuild modified = aFireRing().modify(build);

    assertSame(stats, modified.stats());
  }

  @Test
  void should_return_the_result_of_the_attack_it_wraps() {
    HitResult landed = new HitResult(4, false);
    CharacterBuild build = aFireRing().modify(new CharacterBuild(new FixedAttack(landed), stats));

    HitResult result = build.attack().hit(attacker, target);

    assertSame(landed, result);
  }

  @Test
  void should_burn_the_target_when_a_hit_of_the_build_lands() {
    CharacterBuild build =
        aFireRing().modify(new CharacterBuild(new FixedAttack(new HitResult(4, false)), stats));

    build.attack().hit(attacker, target);
    target.update(1);

    assertEquals(8, target.getHp());
  }

  @Test
  void should_not_burn_the_target_when_a_hit_of_the_build_does_not_land() {
    CharacterBuild build =
        aFireRing().modify(new CharacterBuild(new FixedAttack(HitResult.NONE), stats));

    build.attack().hit(attacker, target);
    target.update(1);

    assertEquals(10, target.getHp());
  }

  @Test
  void should_credit_the_holder_for_the_burn_damage() {
    CharacterBuild build =
        aFireRing().modify(new CharacterBuild(new FixedAttack(new HitResult(4, false)), stats));
    List<GameEvent> events = new ArrayList<>();
    world.getEventBus().subscribe(DamageDealt.class, events::add);

    build.attack().hit(attacker, target);
    target.update(1);
    world.getEventBus().flush();

    assertEquals(List.of(new DamageDealt(attacker, target, 2)), events);
  }

  @Test
  void should_throw_when_the_burn_is_null() {
    assertThrows(
        NullPointerException.class,
        () -> new FireRing("fire_ring", "item.fire_ring.name", "items/fire_ring", 0, null));
  }

  @Test
  void should_throw_when_the_attack_to_decorate_is_null() {
    FireRing ring = aFireRing();

    assertThrows(NullPointerException.class, () -> ring.decorateAttack(null));
  }

  @Test
  void should_throw_when_the_build_is_null() {
    FireRing ring = aFireRing();

    assertThrows(NullPointerException.class, () -> ring.modify(null));
  }

  private static FireRing aFireRing() {
    return new FireRing("fire_ring", "item.fire_ring.name", "items/fire_ring", 0, BURN);
  }

  /** Hand-written fake (Q117): an attack that always returns the same result, dealing nothing. */
  private static final class FixedAttack implements Attack {
    private final HitResult result;

    FixedAttack(HitResult result) {
      this.result = result;
    }

    @Override
    public HitResult hit(Character attacker, Character target) {
      return result;
    }

    @Override
    public void attack(Character owner, float aimDegrees, World world) {
      // Never started in these tests.
    }
  }
}
