package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Character;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * GDD 1.3.1 and 1.4.1: a fire-effect item applies burn on every hit that lands, and nothing else.
 */
class FireTouchTest {
  private static final DotSpec BURN = new DotSpec(2, 1, 3);

  private final World world = new World();
  private final Character attacker = aCharacter().withWorld(world).build();
  private final Character target = aCharacter().withHp(10).withWorld(world).build();

  @Test
  void should_return_the_result_of_the_wrapped_attack() {
    HitResult landed = new HitResult(4, false);
    FireTouch fireTouch = new FireTouch(new FixedAttack(landed), BURN);

    HitResult result = fireTouch.hit(attacker, target);

    assertSame(landed, result);
  }

  @Test
  void should_burn_the_target_when_the_hit_lands() {
    FireTouch fireTouch = new FireTouch(new FixedAttack(new HitResult(4, false)), BURN);

    fireTouch.hit(attacker, target);
    target.update(1);

    assertEquals(8, target.getHp());
  }

  @Test
  void should_burn_the_target_for_the_duration_of_its_spec() {
    FireTouch fireTouch = new FireTouch(new FixedAttack(new HitResult(4, false)), BURN);

    fireTouch.hit(attacker, target);
    for (long tick = 1; tick <= 5; tick++) {
      target.update(tick);
    }

    assertEquals(4, target.getHp());
  }

  @Test
  void should_not_burn_the_target_when_the_hit_does_not_land() {
    FireTouch fireTouch = new FireTouch(new FixedAttack(HitResult.NONE), BURN);

    fireTouch.hit(attacker, target);
    target.update(1);

    assertEquals(10, target.getHp());
  }

  @Test
  void should_credit_the_attacker_for_the_burn_damage() {
    FireTouch fireTouch = new FireTouch(new FixedAttack(new HitResult(4, false)), BURN);
    List<GameEvent> events = new ArrayList<>();
    world.getEventBus().subscribe(DamageDealt.class, events::add);

    fireTouch.hit(attacker, target);
    target.update(1);
    world.getEventBus().flush();

    assertEquals(List.of(new DamageDealt(attacker, target, 2)), events);
  }

  @Test
  void should_throw_when_the_wrapped_attack_is_null() {
    assertThrows(NullPointerException.class, () -> new FireTouch(null, BURN));
  }

  @Test
  void should_throw_when_the_burn_is_null() {
    assertThrows(
        NullPointerException.class, () -> new FireTouch(new FixedAttack(HitResult.NONE), null));
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
