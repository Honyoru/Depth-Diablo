package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.entity.BaseStats;
import com.epitech.depthdiablo.entity.CharacterBuild;
import com.epitech.depthdiablo.entity.Stat;
import com.epitech.depthdiablo.testdata.Items;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1, 1.4: an armor adds its defense bonus to the build of its holder. */
class ArmorTest {

  @Test
  void should_keep_its_values_when_created() {
    Armor armor = anArmor(3);

    assertEquals("leather_armor", armor.getId());
    assertEquals(Rarity.COMMON, armor.getRarity());
    assertEquals(3, armor.getDefenseBonus());
  }

  @Test
  void should_add_its_defense_bonus_to_the_build() {
    CharacterBuild build = anArmor(3).modify(aBuild(2));

    assertEquals(5f, build.stats().get(Stat.DEFENSE));
  }

  @Test
  void should_keep_the_other_stats_of_the_build() {
    CharacterBuild build = anArmor(3).modify(aBuild(2));

    assertEquals(1f, build.stats().get(Stat.ATTACK));
    assertEquals(1f, build.stats().get(Stat.MAX_HP));
  }

  @Test
  void should_keep_the_attack_of_the_build() {
    CharacterBuild before = aBuild(2);

    CharacterBuild after = anArmor(3).modify(before);

    assertSame(before.attack(), after.attack());
  }

  @Test
  void should_not_change_the_given_build() {
    CharacterBuild before = aBuild(2);

    anArmor(3).modify(before);

    assertEquals(2f, before.stats().get(Stat.DEFENSE));
  }

  @Test
  void should_accept_a_defense_bonus_of_0() {
    assertEquals(0, anArmor(0).getDefenseBonus());
  }

  @Test
  void should_throw_when_the_defense_bonus_is_negative() {
    assertThrows(IllegalArgumentException.class, () -> anArmor(-1));
  }

  @Test
  void should_throw_when_the_build_is_null() {
    Armor armor = anArmor(3);

    assertThrows(NullPointerException.class, () -> armor.modify(null));
  }

  @Test
  void should_give_its_defense_bonus_as_effect() {
    assertEquals(List.of(new ItemEffect("bonus.defense", 3f)), anArmor(3).getEffects());
  }

  /** Every stat at 1, except DEFENSE. */
  private static CharacterBuild aBuild(float defense) {
    Map<Stat, Float> values = new EnumMap<>(Stat.class);
    for (Stat stat : Stat.values()) {
      values.put(stat, 1f);
    }
    values.put(Stat.DEFENSE, defense);
    return new CharacterBuild(Items.sword(), new BaseStats(values));
  }

  private static Armor anArmor(int defenseBonus) {
    return new LeatherArmor(
        "leather_armor",
        "item.leather_armor.name",
        "items/leather_armor",
        Rarity.COMMON,
        10,
        defenseBonus);
  }
}
