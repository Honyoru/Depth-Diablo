package com.epitech.depthdiablo.item;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.item.Item;
import com.epitech.depthdiablo.item.ItemFactory;
import com.epitech.depthdiablo.item.Potion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Building items from data/items.json (Q23, Q129).
 * ItemFactoryTest
 */
class ItemFactoryTest {
  private static final String ONE_POTION = """
      { "items": [ {
          "id": "potion_small", "class": "Potion", "nameKey": "item.potion_small.name",
          "spriteId": "items/potion", "price": 10, "healAmount": 3 } ] }
      """;

  @Test
  void should_build_a_potion_with_its_values_when_the_entry_is_a_potion() {
    ItemFactory factory = new ItemFactory(new DataRegistry("items", () -> ONE_POTION));

    Item item = factory.create("potion_small");

    Potion potion = assertInstanceOf(Potion.class, item);
    assertEquals("potion_small", potion.id());
    assertEquals("item.potion_small.name", potion.nameKey());
    assertEquals("items/potion", potion.spriteId());
    assertEquals(10, potion.price());
    assertEquals(3, potion.healAmount());
  }

  @Test
  void should_build_a_new_item_on_every_call() {
    ItemFactory factory = new ItemFactory(new DataRegistry("items", () -> ONE_POTION));

    Item first = factory.create("potion_small");
    Item second = factory.create("potion_small");

    assertNotSame(first, second);
  }

  @Test
  void should_reject_an_entry_when_its_class_is_unknown() {
    String file = "{ \"items\": [ { \"id\": \"sword\", \"class\": \"Banana\" } ] }";
    ItemFactory factory = new ItemFactory(new DataRegistry("items", () -> file));

    assertThrows(IllegalArgumentException.class, () -> factory.create("sword"));
  }

  @Test
  void should_reject_an_entry_when_a_value_is_missing() {
    String file = "{ \"items\": [ { \"id\": \"potion_small\", \"class\": \"Potion\" } ] }";
    ItemFactory factory = new ItemFactory(new DataRegistry("items", () -> file));

    assertThrows(IllegalArgumentException.class, () -> factory.create("potion_small"));
  }

  @Test
  void should_build_every_item_of_the_data_file() throws IOException {
    String file = Files.readString(dataFile("items.json"), UTF_8);
    DataRegistry items = new DataRegistry("items", () -> file);
    ItemFactory factory = new ItemFactory(items);

    for (String id : items.ids()) {
      assertEquals(id, factory.create(id).id());
    }
  }

  private static Path dataFile(String name) {
    return Path.of(System.getProperty("assets.dir"), "data", name);
  }
}
