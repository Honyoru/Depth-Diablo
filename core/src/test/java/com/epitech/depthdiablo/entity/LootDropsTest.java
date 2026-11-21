package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aRat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.item.ItemFactory;
import com.epitech.depthdiablo.item.LootTable;
import com.epitech.depthdiablo.item.Rarity;
import com.epitech.depthdiablo.random.SeededRandomSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.4: a dying enemy may drop an item of its loot table where it stood. */
class LootDropsTest {
  private static final String ONE_POTION =
      """
      { "items": [
        { "id": "potion_small", "class": "Potion", "nameKey": "item.potion_small.name",
          "spriteId": "items/potion", "price": 10, "healAmount": 3 }
      ] }
      """;

  private final World world = new World();
  private final DataRegistry items = new DataRegistry("items", () -> ONE_POTION);
  private final ItemFactory factory = new ItemFactory(items);
  private final ItemDropper dropper = new ItemDropper(factory, world);
  private final LootTable table = new LootTable(items, factory, LootTable.DEFAULT_WEIGHTS);

  @Test
  void should_drop_an_item_of_the_table_where_an_enemy_dies() {
    MeleeEnemy rat = aRat().at(64, 32).withWorld(world).build();
    LootDrops loot = new LootDrops(table, dropper, new SeededRandomSource(1), 1);

    loot.onEvent(new EntityDied(rat, Optional.empty()));

    List<ItemDrop> drops = drops();
    assertEquals(1, drops.size());
    assertEquals("potion_small", drops.get(0).getItem().getId());
    Aabb ratBody = rat.getBodyBox();
    Aabb dropBody = drops.get(0).getBodyBox();
    assertEquals(ratBody.x() + ratBody.width() / 2, dropBody.x() + dropBody.width() / 2, 1e-4f);
    assertEquals(ratBody.y() + ratBody.height() / 2, dropBody.y() + dropBody.height() / 2, 1e-4f);
  }

  @Test
  void should_drop_nothing_when_the_chance_is_0() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    LootDrops loot = new LootDrops(table, dropper, new SeededRandomSource(1), 0);

    loot.onEvent(new EntityDied(rat, Optional.empty()));

    assertEquals(List.of(), drops());
  }

  @Test
  void should_drop_nothing_when_the_table_has_no_weighted_item() {
    MeleeEnemy rat = aRat().withWorld(world).build();
    LootTable noPotions = new LootTable(items, factory, Map.of(Rarity.LEGENDARY, 1));
    LootDrops loot = new LootDrops(noPotions, dropper, new SeededRandomSource(1), 1);

    loot.onEvent(new EntityDied(rat, Optional.empty()));

    assertEquals(List.of(), drops());
  }

  @Test
  void should_drop_nothing_when_the_player_dies() {
    Character player = aCharacter().withFaction(Faction.PLAYER).withWorld(world).build();
    LootDrops loot = new LootDrops(table, dropper, new SeededRandomSource(1), 1);

    loot.onEvent(new EntityDied(player, Optional.empty()));

    assertEquals(List.of(), drops());
  }

  @Test
  void should_throw_when_the_chance_is_outside_0_to_1() {
    SeededRandomSource random = new SeededRandomSource(1);

    assertThrows(IllegalArgumentException.class, () -> new LootDrops(table, dropper, random, -0.1));
    assertThrows(IllegalArgumentException.class, () -> new LootDrops(table, dropper, random, 1.5));
    assertThrows(
        IllegalArgumentException.class, () -> new LootDrops(table, dropper, random, Double.NaN));
  }

  @Test
  void should_throw_when_an_argument_is_null() {
    SeededRandomSource random = new SeededRandomSource(1);

    assertThrows(NullPointerException.class, () -> new LootDrops(null, dropper, random, 1));
    assertThrows(NullPointerException.class, () -> new LootDrops(table, null, random, 1));
    assertThrows(NullPointerException.class, () -> new LootDrops(table, dropper, null, 1));
  }

  private List<ItemDrop> drops() {
    return world.getEntities().stream()
        .filter(ItemDrop.class::isInstance)
        .map(ItemDrop.class::cast)
        .toList();
  }
}
