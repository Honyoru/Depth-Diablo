package com.epitech.depthdiablo.loot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.item.ItemFactory;
import com.epitech.depthdiablo.item.Rarity;
import com.epitech.depthdiablo.random.SeededRandomSource;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.4: each rarity has its share of the drops; the rarer an item, the less often it drops. */
class LootTableTest {
  private static final String COMMON_AND_LEGENDARY =
      """
      { "items": [
        { "id": "hatchet", "class": "Axe", "nameKey": "item.hatchet.name",
          "spriteId": "items/axes/hatchet", "rarity": "COMMON", "price": 40,
          "attackBonus": 2, "attackSpeed": 0.8, "range": 200, "width": 220,
          "arcDegrees": 110, "swingTicks": 16 },
        { "id": "obsidian_axe", "class": "Axe", "nameKey": "item.obsidian_axe.name",
          "spriteId": "items/axes/obsidian_axe", "rarity": "LEGENDARY", "price": 600,
          "attackBonus": 10, "attackSpeed": 0.8, "range": 200, "width": 220,
          "arcDegrees": 110, "swingTicks": 16 },
        { "id": "potion_small", "class": "Potion", "nameKey": "item.potion_small.name",
          "spriteId": "items/potion", "price": 10, "healAmount": 3 }
      ] }
      """;

  private final DataRegistry items = new DataRegistry("items", () -> COMMON_AND_LEGENDARY);
  private final ItemFactory factory = new ItemFactory(items);

  @Test
  void should_drop_common_items_more_often_than_legendary_ones() {
    LootTable table = new LootTable(items, factory, LootTable.DEFAULT_SHARES);
    SeededRandomSource random = new SeededRandomSource(42);
    Map<String, Integer> counts = new HashMap<>();

    for (int i = 0; i < 2000; i++) {
      table.roll(random).ifPresent(id -> counts.merge(id, 1, Integer::sum));
    }

    assertTrue(
        counts.getOrDefault("hatchet", 0) > 5 * counts.getOrDefault("obsidian_axe", 0),
        "counts: " + counts);
    assertTrue(counts.getOrDefault("obsidian_axe", 0) > 0, "a legendary still drops: " + counts);
  }

  @Test
  void should_never_drop_a_rarity_with_no_share() {
    Map<Rarity, Integer> onlyLegendary = new EnumMap<>(Rarity.class);
    onlyLegendary.put(Rarity.LEGENDARY, 1);
    LootTable table = new LootTable(items, factory, onlyLegendary);
    SeededRandomSource random = new SeededRandomSource(7);

    for (int i = 0; i < 100; i++) {
      assertEquals(Optional.of("obsidian_axe"), table.roll(random));
    }
  }

  @Test
  void should_read_the_rarity_of_items_without_one_in_the_data() {
    LootTable table = new LootTable(items, factory, Map.of(Rarity.CONSUMABLE, 1));

    assertEquals(Optional.of("potion_small"), table.roll(new SeededRandomSource(3)));
  }

  @Test
  void should_drop_nothing_when_no_rarity_with_a_share_has_an_item() {
    LootTable table = new LootTable(items, factory, Map.of(Rarity.EPIC, 5));

    assertEquals(Optional.empty(), table.roll(new SeededRandomSource(1)));
  }

  @Test
  void should_give_every_rarity_a_share_by_default() {
    for (Rarity rarity : Rarity.values()) {
      assertTrue(LootTable.DEFAULT_SHARES.get(rarity) > 0, rarity.name());
    }
    assertTrue(
        LootTable.DEFAULT_SHARES.get(Rarity.COMMON)
            > LootTable.DEFAULT_SHARES.get(Rarity.LEGENDARY));
  }

  @Test
  void should_favour_potions_and_rings_by_default() {
    Map<Rarity, Integer> shares = LootTable.DEFAULT_SHARES;

    assertTrue(shares.get(Rarity.CONSUMABLE) > shares.get(Rarity.COMMON));
    assertTrue(shares.get(Rarity.PASSIVE) > shares.get(Rarity.COMMON));
  }

  @Test
  void should_keep_the_share_of_a_rarity_whatever_its_number_of_items() {
    String threeLegendaries =
        COMMON_AND_LEGENDARY.substring(0, COMMON_AND_LEGENDARY.lastIndexOf(']'))
            + ", "
            + legendary("obsidian_axe_2")
            + ", "
            + legendary("obsidian_axe_3")
            + "] }";
    DataRegistry data = new DataRegistry("items", () -> threeLegendaries);
    assertEquals(4, data.getIds().size() - 1); // 1 common and 3 legendaries, besides the potion
    LootTable table =
        new LootTable(data, new ItemFactory(data), Map.of(Rarity.COMMON, 1, Rarity.LEGENDARY, 1));
    SeededRandomSource random = new SeededRandomSource(11);
    int commons = 0;

    for (int i = 0; i < 4000; i++) {
      if (table.roll(random).equals(Optional.of("hatchet"))) {
        commons++;
      }
    }

    assertTrue(commons > 1800 && commons < 2200, "about half are the common one: " + commons);
  }

  @Test
  void should_throw_when_a_share_is_negative() {
    Map<Rarity, Integer> negative = Map.of(Rarity.COMMON, -1);

    assertThrows(IllegalArgumentException.class, () -> new LootTable(items, factory, negative));
  }

  @Test
  void should_throw_when_an_argument_is_null() {
    Map<Rarity, Integer> shares = LootTable.DEFAULT_SHARES;

    assertThrows(NullPointerException.class, () -> new LootTable(null, factory, shares));
    assertThrows(NullPointerException.class, () -> new LootTable(items, null, shares));
    assertThrows(NullPointerException.class, () -> new LootTable(items, factory, null));
    assertThrows(NullPointerException.class, () -> table().roll(null));
  }

  private static String legendary(String id) {
    return "        { \"id\": \""
        + id
        + "\", \"class\": \"Axe\", \"nameKey\": \"item.x.name\","
        + " \"spriteId\": \"items/axes/obsidian_axe\", \"rarity\": \"LEGENDARY\", \"price\": 600,"
        + " \"attackBonus\": 10, \"attackSpeed\": 0.8, \"range\": 200, \"width\": 220,"
        + " \"arcDegrees\": 110, \"swingTicks\": 16 }";
  }

  private LootTable table() {
    return new LootTable(items, factory, LootTable.DEFAULT_SHARES);
  }
}
