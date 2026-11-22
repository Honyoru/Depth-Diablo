package com.epitech.depthdiablo.desktop.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.arena.ArenaCycle;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.item.Armor;
import com.epitech.depthdiablo.item.Consumable;
import com.epitech.depthdiablo.item.Inventory;
import com.epitech.depthdiablo.item.ItemFactory;
import com.epitech.depthdiablo.item.Weapon;
import org.junit.jupiter.api.Test;

/** GDD 1.4: the HUD hotbar shows the picture of each consumable of the inventory. */
class HudStateTest {
  private static final String ITEMS_TEST =
      """
      { "items": [
        { "id": "potion_small", "class": "Potion", "nameKey": "item.potion_small.name",
          "spriteId": "items/potion", "price": 10, "healAmount": 3 },
        { "id": "sword", "class": "Sword", "nameKey": "item.sword.name",
          "spriteId": "items/swords/short_sword", "rarity": "COMMON", "price": 30,
          "attackBonus": 2, "attackSpeed": 1, "range": 24, "width": 8, "arcDegrees": 90,
          "swingTicks": 12 },
        { "id": "leather_armor", "class": "LeatherArmor", "nameKey": "item.leather_armor.name",
          "spriteId": "items/shields/round_shield", "rarity": "COMMON", "price": 10,
          "defenseBonus": 1 }
      ] }
      """;
  private final ItemFactory items = new ItemFactory(new DataRegistry("items", () -> ITEMS_TEST));

  @Test
  void should_show_free_hotbar_slots_when_created() {
    HudState state = new HudState();

    for (int i = 0; i < HudState.SLOT_COUNT; i++) {
      assertEquals("items/empty", state.getHotbarSpriteId(i));
    }
  }

  @Test
  void should_show_the_picture_of_each_hotbar_consumable() {
    Inventory inventory =
        new Inventory((Weapon) items.create("sword"), (Armor) items.create("leather_armor"));
    inventory.add((Consumable) items.create("potion_small"));
    HudState state = new HudState();

    state.syncHotbar(inventory);

    assertEquals("items/potion", state.getHotbarSpriteId(0));
    assertEquals("items/empty", state.getHotbarSpriteId(1));
  }

  @Test
  void should_throw_when_the_hotbar_slot_is_outside_the_hotbar() {
    HudState state = new HudState();

    assertThrows(IndexOutOfBoundsException.class, () -> state.getHotbarSpriteId(3));
  }

  @Test
  void should_show_the_wave_and_no_timer_while_a_wave_fights() {
    HudState state = new HudState();
    ArenaCycle cycle = new ArenaCycle(5, 15 * GameConstants.TICKS_PER_SECOND);

    state.syncArena(cycle);

    assertEquals("Wave 1", state.getProgressText());
    assertEquals("", state.getTimerText());
  }

  @Test
  void should_count_the_break_down_in_whole_seconds() {
    HudState state = new HudState();
    ArenaCycle cycle = new ArenaCycle(5, 15 * GameConstants.TICKS_PER_SECOND);
    cycle.waveCleared();
    state.syncArena(cycle);
    assertEquals("Next wave in 15 s", state.getTimerText());

    for (int tick = 0; tick < 14 * GameConstants.TICKS_PER_SECOND + 1; tick++) {
      cycle.update();
    }
    state.syncArena(cycle);

    assertEquals("Next wave in 1 s", state.getTimerText()); // 59 ticks left: still 1 s
  }

  @Test
  void should_mark_the_boss_wave() {
    HudState state = new HudState();
    ArenaCycle cycle = new ArenaCycle(1, 1); // every wave is a boss wave

    state.syncArena(cycle);

    assertEquals("Wave 1 - BOSS", state.getProgressText());
  }

  @Test
  void should_show_the_shop_once_the_boss_is_beaten() {
    HudState state = new HudState();
    ArenaCycle cycle = new ArenaCycle(1, 1);
    cycle.waveCleared();

    state.syncArena(cycle);

    assertEquals("Shop open", state.getTimerText());
    assertEquals("Wave 1", state.getProgressText()); // the boss is no longer fighting
  }

  @Test
  void should_show_the_floor_and_no_timer_in_endless() {
    HudState state = new HudState();

    state.syncFloor(1);

    assertEquals("Floor 1", state.getProgressText());
    assertEquals("", state.getTimerText());
  }

  @Test
  void should_throw_when_the_floor_is_below_1() {
    assertThrows(IllegalArgumentException.class, () -> new HudState().syncFloor(0));
  }

  @Test
  void should_hide_the_boss_bar_when_created() {
    assertFalse(new HudState().isBossShown());
  }

  @Test
  void should_show_the_boss_life_as_a_percentage() {
    HudState state = new HudState();

    state.syncBoss(30, 120);

    assertTrue(state.isBossShown());
    assertEquals(25f, state.getBossHealthPercentage(), 0.001f);
  }

  @Test
  void should_hide_the_boss_bar_once_hidden() {
    HudState state = new HudState();
    state.syncBoss(30, 120);

    state.hideBoss();

    assertFalse(state.isBossShown());
  }

  @Test
  void should_throw_when_the_boss_max_hp_is_0() {
    assertThrows(IllegalArgumentException.class, () -> new HudState().syncBoss(0, 0));
  }
}
