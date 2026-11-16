package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.testdata.Items;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: a build never changes; each with... method returns a new one. */
class CharacterBuildTest {

  @Test
  void should_keep_its_attack_and_stats_when_created() {
    Attack sword = Items.sword();
    StatSource stats = someStats();

    CharacterBuild build = new CharacterBuild(sword, stats);

    assertSame(sword, build.attack());
    assertSame(stats, build.stats());
  }

  @Test
  void should_give_a_build_with_the_new_attack_and_the_same_stats() {
    CharacterBuild build = new CharacterBuild(Items.sword(), someStats());
    Attack newSword = Items.sword();

    CharacterBuild changed = build.withAttack(newSword);

    assertSame(newSword, changed.attack());
    assertSame(build.stats(), changed.stats());
  }

  @Test
  void should_leave_the_build_unchanged_when_given_a_new_attack() {
    Attack sword = Items.sword();
    CharacterBuild build = new CharacterBuild(sword, someStats());

    build.withAttack(Items.sword());

    assertSame(sword, build.attack());
  }

  @Test
  void should_give_a_build_with_the_new_stats_and_the_same_attack() {
    CharacterBuild build = new CharacterBuild(Items.sword(), someStats());
    StatSource newStats = someStats();

    CharacterBuild changed = build.withStats(newStats);

    assertSame(newStats, changed.stats());
    assertSame(build.attack(), changed.attack());
  }

  @Test
  void should_leave_the_build_unchanged_when_given_new_stats() {
    StatSource stats = someStats();
    CharacterBuild build = new CharacterBuild(Items.sword(), stats);

    build.withStats(someStats());

    assertSame(stats, build.stats());
  }

  @Test
  void should_throw_when_the_attack_is_null() {
    StatSource stats = someStats();

    assertThrows(NullPointerException.class, () -> new CharacterBuild(null, stats));
  }

  @Test
  void should_throw_when_the_stats_are_null() {
    Attack sword = Items.sword();

    assertThrows(NullPointerException.class, () -> new CharacterBuild(sword, null));
  }

  @Test
  void should_throw_when_the_new_attack_is_null() {
    CharacterBuild build = new CharacterBuild(Items.sword(), someStats());

    assertThrows(NullPointerException.class, () -> build.withAttack(null));
  }

  @Test
  void should_throw_when_the_new_stats_are_null() {
    CharacterBuild build = new CharacterBuild(Items.sword(), someStats());

    assertThrows(NullPointerException.class, () -> build.withStats(null));
  }

  private static StatSource someStats() {
    Map<Stat, Float> values = new EnumMap<>(Stat.class);
    for (Stat stat : Stat.values()) {
      values.put(stat, 1f);
    }
    return new BaseStats(values);
  }
}
