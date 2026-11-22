package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Character;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: an attack decorator forwards both calls to the attack it wraps. */
class AttackDecoratorTest {
  private final Character attacker = aCharacter().build();
  private final Character target = aCharacter().build();

  @Test
  void should_return_the_result_of_the_wrapped_attack_when_it_hits() {
    RecordingAttack inner = new RecordingAttack(new HitResult(3, false));
    Attack decorator = new PlainDecorator(inner);

    HitResult result = decorator.hit(attacker, target);

    assertSame(inner.result, result);
  }

  @Test
  void should_pass_the_attacker_and_the_target_to_the_wrapped_attack() {
    RecordingAttack inner = new RecordingAttack(HitResult.NONE);
    Attack decorator = new PlainDecorator(inner);

    decorator.hit(attacker, target);

    assertSame(attacker, inner.attacker);
    assertSame(target, inner.target);
  }

  @Test
  void should_start_the_wrapped_attack_with_the_same_arguments() {
    RecordingAttack inner = new RecordingAttack(HitResult.NONE);
    Attack decorator = new PlainDecorator(inner);
    World world = new World();

    decorator.attack(attacker, 45f, world);

    assertSame(attacker, inner.owner);
    assertEquals(45f, inner.aimDegrees);
    assertSame(world, inner.world);
  }

  @Test
  void should_throw_when_the_wrapped_attack_is_null() {
    assertThrows(NullPointerException.class, () -> new PlainDecorator(null));
  }

  /** Hand-written fake (Q117): a decorator that adds nothing. */
  private static final class PlainDecorator extends AttackDecorator {
    PlainDecorator(Attack inner) {
      super(inner);
    }
  }

  /** Hand-written fake (Q117): records its calls and returns a fixed hit result. */
  private static final class RecordingAttack implements Attack {
    private final HitResult result;
    private Character attacker;
    private Character target;
    private Character owner;
    private float aimDegrees;
    private World world;

    RecordingAttack(HitResult result) {
      this.result = result;
    }

    @Override
    public HitResult hit(Character attacker, Character target) {
      this.attacker = attacker;
      this.target = target;
      return result;
    }

    @Override
    public void attack(Character owner, float aimDegrees, World world) {
      this.owner = owner;
      this.aimDegrees = aimDegrees;
      this.world = world;
    }
  }
}
