package com.epitech.depthdiablo.desktop.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
