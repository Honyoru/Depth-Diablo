package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Character;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: life steal heals the attacker by a part of the damage of every hit that lands. */
class LifeStealTest {
  private final Character attacker = aCharacter().withHp(20).build();
  private final Character target = aCharacter().withHp(20).build();

  LifeStealTest() {
    attacker.takeDamage(10); // 10 HP left out of 20, so a heal shows
  }

  @Test
  void should_heal_the_attacker_by_its_fraction_of_the_damage() {
    LifeSteal lifeSteal = new LifeSteal(new FixedAttack(new HitResult(8, false)), 0.5f);

    lifeSteal.hit(attacker, target);

    assertEquals(14, attacker.getHp());
  }

  @Test
  void should_heal_at_least_1_hp_when_the_hit_lands() {
    LifeSteal lifeSteal = new LifeSteal(new FixedAttack(new HitResult(1, false)), 0.1f);

    lifeSteal.hit(attacker, target);

    assertEquals(11, attacker.getHp());
  }

  @Test
  void should_not_heal_when_the_hit_does_not_land() {
    LifeSteal lifeSteal = new LifeSteal(new FixedAttack(HitResult.NONE), 0.5f);

    lifeSteal.hit(attacker, target);

    assertEquals(10, attacker.getHp());
  }

  @Test
  void should_return_the_result_of_the_wrapped_attack() {
    HitResult landed = new HitResult(8, false);
    LifeSteal lifeSteal = new LifeSteal(new FixedAttack(landed), 0.5f);

    assertSame(landed, lifeSteal.hit(attacker, target));
  }

  @Test
  void should_throw_when_the_fraction_is_outside_0_to_1() {
    Attack inner = new FixedAttack(HitResult.NONE);

    assertThrows(IllegalArgumentException.class, () -> new LifeSteal(inner, 0f));
    assertThrows(IllegalArgumentException.class, () -> new LifeSteal(inner, 1.5f));
    assertThrows(IllegalArgumentException.class, () -> new LifeSteal(inner, Float.NaN));
  }

  @Test
  void should_throw_when_the_wrapped_attack_is_null() {
    assertThrows(NullPointerException.class, () -> new LifeSteal(null, 0.5f));
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
