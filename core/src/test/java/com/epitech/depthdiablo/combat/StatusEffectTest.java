package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.entity.Character;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: a status effect counts its ticks down and runs its step until it is over. */
class StatusEffectTest {
  private final Character source = aCharacter().build();
  private final Character target = aCharacter().build();

  @Test
  void should_start_with_its_whole_duration_left() {
    CountingEffect effect = new CountingEffect(3, source);

    assertEquals(3, effect.ticksLeft());
    assertFalse(effect.isOver());
  }

  @Test
  void should_lose_one_tick_each_time_it_ticks() {
    CountingEffect effect = new CountingEffect(3, source);

    effect.tick(target);

    assertEquals(2, effect.ticksLeft());
  }

  @Test
  void should_run_its_step_once_per_tick() {
    CountingEffect effect = new CountingEffect(3, source);

    effect.tick(target);
    effect.tick(target);

    assertEquals(2, effect.steps);
    assertSame(target, effect.lastTarget);
  }

  @Test
  void should_be_over_after_its_whole_duration() {
    CountingEffect effect = new CountingEffect(3, source);

    for (int i = 0; i < 3; i++) {
      effect.tick(target);
    }

    assertTrue(effect.isOver());
    assertEquals(0, effect.ticksLeft());
  }

  @Test
  void should_do_nothing_when_it_ticks_after_it_is_over() {
    CountingEffect effect = new CountingEffect(1, source);
    effect.tick(target);

    effect.tick(target);

    assertEquals(1, effect.steps);
    assertEquals(0, effect.ticksLeft());
  }

  @Test
  void should_keep_the_character_who_applied_it() {
    CountingEffect effect = new CountingEffect(3, source);

    assertSame(source, effect.getSource());
  }

  @Test
  void should_throw_when_the_target_is_null() {
    CountingEffect effect = new CountingEffect(3, source);

    assertThrows(NullPointerException.class, () -> effect.tick(null));
  }

  @Test
  void should_throw_when_the_duration_is_0() {
    assertThrows(IllegalArgumentException.class, () -> new CountingEffect(0, source));
  }

  @Test
  void should_throw_when_the_source_is_null() {
    assertThrows(NullPointerException.class, () -> new CountingEffect(3, null));
  }

  /** Hand-written fake (Q117): counts the calls to its step. */
  private static final class CountingEffect extends StatusEffect {
    private int steps;
    private Character lastTarget;

    CountingEffect(int durationTicks, Character source) {
      super(durationTicks, source);
    }

    @Override
    protected void onTick(Character target) {
      steps++;
      lastTarget = target;
    }
  }
}
