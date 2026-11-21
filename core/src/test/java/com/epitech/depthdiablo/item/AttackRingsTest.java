package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.combat.Execute;
import com.epitech.depthdiablo.combat.LifeSteal;
import com.epitech.depthdiablo.testdata.Items;
import java.util.List;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: the life steal and executioner rings wrap their holder's attack. */
class AttackRingsTest {

  @Test
  void should_wrap_the_attack_in_a_life_steal() {
    LifeStealRing ring = new LifeStealRing("ruby_ring", "n", "s", 0, 0.25f);

    assertInstanceOf(LifeSteal.class, ring.decorateAttack(Items.sword()));
  }

  @Test
  void should_show_the_life_steal_in_percent() {
    LifeStealRing ring = new LifeStealRing("ruby_ring", "n", "s", 0, 0.25f);

    assertEquals(List.of(new ItemEffect("effect.life_steal", 25)), ring.getEffects());
  }

  @Test
  void should_reject_a_life_steal_fraction_outside_0_to_1() {
    assertThrows(IllegalArgumentException.class, () -> new LifeStealRing("r", "n", "s", 0, 0f));
    assertThrows(IllegalArgumentException.class, () -> new LifeStealRing("r", "n", "s", 0, 2f));
  }

  @Test
  void should_wrap_the_attack_in_an_execute() {
    ExecutionerRing ring = new ExecutionerRing("bone_ring", "n", "s", 0, 0.5f);

    assertInstanceOf(Execute.class, ring.decorateAttack(Items.sword()));
  }

  @Test
  void should_show_the_execute_threshold_in_percent() {
    ExecutionerRing ring = new ExecutionerRing("bone_ring", "n", "s", 0, 0.5f);

    assertEquals(List.of(new ItemEffect("effect.execute", 50)), ring.getEffects());
  }

  @Test
  void should_reject_an_execute_threshold_outside_0_to_1() {
    assertThrows(IllegalArgumentException.class, () -> new ExecutionerRing("r", "n", "s", 0, 0f));
    assertThrows(IllegalArgumentException.class, () -> new ExecutionerRing("r", "n", "s", 0, 1f));
  }

  @Test
  void should_throw_when_the_wrapped_attack_is_null() {
    assertThrows(
        NullPointerException.class,
        () -> new LifeStealRing("r", "n", "s", 0, 0.25f).decorateAttack(null));
    assertThrows(
        NullPointerException.class,
        () -> new ExecutionerRing("r", "n", "s", 0, 0.5f).decorateAttack(null));
  }
}
