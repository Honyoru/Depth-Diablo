package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.engine.event.EntityDied;
import com.epitech.depthdiablo.engine.event.GameEvent;
import com.epitech.depthdiablo.entity.character.Character;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: execute finishes a target that a landed hit leaves under a part of its max HP. */
class ExecuteTest {
  private final World world = new World();
  private final Character attacker = aCharacter().withWorld(world).build();
  private final Character target =
      aCharacter().withHp(100).withInvincibilityAfterHit(18).withWorld(world).build();

  @Test
  void should_kill_a_target_left_under_the_threshold() {
    target.takeDamage(85); // 15 HP left, and invincible for 18 ticks
    Execute execute = new Execute(new FixedAttack(new HitResult(5, false)), 0.2f);

    execute.hit(attacker, target);

    assertTrue(target.isDead());
  }

  @Test
  void should_publish_one_death_credited_to_the_attacker() {
    target.takeDamage(85);
    List<GameEvent> deaths = new ArrayList<>();
    world.getEventBus().subscribe(EntityDied.class, deaths::add);
    Execute execute = new Execute(new FixedAttack(new HitResult(5, false)), 0.2f);

    execute.hit(attacker, target);
    world.getEventBus().flush();

    assertEquals(List.of(new EntityDied(target, Optional.of(attacker))), deaths);
  }

  @Test
  void should_spare_a_target_above_the_threshold() {
    target.takeDamage(50);
    Execute execute = new Execute(new FixedAttack(new HitResult(5, false)), 0.2f);

    execute.hit(attacker, target);

    assertEquals(50, target.getHp());
  }

  @Test
  void should_spare_a_low_target_when_the_hit_does_not_land() {
    target.takeDamage(85);
    Execute execute = new Execute(new FixedAttack(HitResult.NONE), 0.2f);

    execute.hit(attacker, target);

    assertFalse(target.isDead());
  }

  @Test
  void should_return_the_result_of_the_wrapped_attack() {
    HitResult landed = new HitResult(5, false);
    Execute execute = new Execute(new FixedAttack(landed), 0.2f);

    assertSame(landed, execute.hit(attacker, target));
  }

  @Test
  void should_throw_when_the_threshold_is_outside_0_to_1() {
    Attack inner = new FixedAttack(HitResult.NONE);

    assertThrows(IllegalArgumentException.class, () -> new Execute(inner, 0f));
    assertThrows(IllegalArgumentException.class, () -> new Execute(inner, 1f));
    assertThrows(IllegalArgumentException.class, () -> new Execute(inner, Float.NaN));
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
