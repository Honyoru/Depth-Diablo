package com.epitech.depthdiablo.desktop.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epitech.depthdiablo.item.ItemEffect;
import java.util.List;
import org.junit.jupiter.api.Test;

/** GDD 1.4: the inventory page writes what an item gives. */
class ItemEffectTextTest {

  @Test
  void should_write_an_attack_bonus() {
    assertEquals(
        List.of("Attack +2"), ItemEffectText.lines(List.of(new ItemEffect("bonus.attack", 2f))));
  }

  @Test
  void should_write_an_attack_speed() {
    assertEquals(
        List.of("Attack speed x1.5"),
        ItemEffectText.lines(List.of(new ItemEffect("bonus.attack_speed", 1.5f))));
  }

  @Test
  void should_write_a_defense_bonus() {
    assertEquals(
        List.of("Defense +1"), ItemEffectText.lines(List.of(new ItemEffect("bonus.defense", 1f))));
  }

  @Test
  void should_write_a_heal() {
    assertEquals(
        List.of("Heals 3 HP"), ItemEffectText.lines(List.of(new ItemEffect("effect.heal", 3f))));
  }

  @Test
  void should_write_a_burn_in_seconds_on_one_line() {
    List<ItemEffect> burn =
        List.of(
            new ItemEffect("effect.burn_damage", 2f),
            new ItemEffect("effect.burn_interval_ticks", 30f),
            new ItemEffect("effect.burn_duration_ticks", 120f));

    assertEquals(List.of("Burns 2 damage every 0.5 s for 2 s"), ItemEffectText.lines(burn));
  }

  @Test
  void should_name_an_item_from_its_id() {
    assertEquals("Fire ring", ItemEffectText.name("fire_ring"));
  }
}
