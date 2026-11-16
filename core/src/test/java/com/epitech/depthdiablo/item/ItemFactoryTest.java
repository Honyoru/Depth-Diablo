package com.epitech.depthdiablo.item;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.data.DataRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Building items from data/items.json (Q23, Q129). Every test reads the real file, and each item is
 * compared with its own entry, so balancing the file never breaks these tests.
 */
class ItemFactoryTest {

  @Test
  void should_build_the_potion_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    Potion potion = assertInstanceOf(Potion.class, new ItemFactory(items).create("potion_small"));

    JsonValue entry = items.definition("potion_small");
    assertEquals("potion_small", potion.id());
    assertEquals(entry.getString("nameKey"), potion.nameKey());
    assertEquals(entry.getString("spriteId"), potion.spriteId());
    assertEquals(entry.getInt("price"), potion.price());
    assertEquals(entry.getInt("healAmount"), potion.healAmount());
  }

  @Test
  void should_build_the_sword_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    Sword sword = assertInstanceOf(Sword.class, new ItemFactory(items).create("sword"));

    JsonValue entry = items.definition("sword");
    assertEquals("sword", sword.id());
    assertEquals(entry.getString("nameKey"), sword.nameKey());
    assertEquals(entry.getString("spriteId"), sword.spriteId());
    assertEquals(Rarity.valueOf(entry.getString("rarity")), sword.rarity());
    assertEquals(entry.getInt("price"), sword.price());
    assertEquals(entry.getInt("attackBonus"), sword.attackBonus());
    assertEquals(entry.getFloat("attackSpeed"), sword.attackSpeed());
    assertEquals(entry.getFloat("range"), sword.range());
    assertEquals(entry.getFloat("width"), sword.width());
    assertEquals(entry.getFloat("arcDegrees"), sword.arcDegrees());
    assertEquals(entry.getInt("swingTicks"), sword.swingTicks());
  }

  @Test
  void should_build_a_new_item_on_every_call() throws IOException {
    ItemFactory factory = new ItemFactory(realItems());

    Item first = factory.create("potion_small");
    Item second = factory.create("potion_small");

    assertNotSame(first, second);
  }

  @Test
  void should_build_every_item_of_the_data_file() throws IOException {
    DataRegistry items = realItems();
    ItemFactory factory = new ItemFactory(items);

    for (String id : items.ids()) {
      assertEquals(id, factory.create(id).id());
    }
  }

  @Test
  void should_reject_an_entry_when_its_class_is_unknown() throws IOException {
    String file = readItemsFile().replace("\"Potion\"", "\"Banana\"");
    ItemFactory factory = new ItemFactory(new DataRegistry("items", () -> file));

    assertThrows(IllegalArgumentException.class, () -> factory.create("potion_small"));
  }

  @Test
  void should_reject_an_entry_when_a_value_is_missing() throws IOException {
    String file = readItemsFile().replace("\"healAmount\"", "\"unknownKey\"");
    ItemFactory factory = new ItemFactory(new DataRegistry("items", () -> file));

    assertThrows(IllegalArgumentException.class, () -> factory.create("potion_small"));
  }

  @Test
  void should_reject_the_sword_when_its_rarity_is_unknown() throws IOException {
    String file = readItemsFile().replace("\"COMMON\"", "\"MYTHIC\"");
    ItemFactory factory = new ItemFactory(new DataRegistry("items", () -> file));

    assertThrows(IllegalArgumentException.class, () -> factory.create("sword"));
  }

  private static DataRegistry realItems() throws IOException {
    String file = readItemsFile();
    return new DataRegistry("items", () -> file);
  }

  private static String readItemsFile() throws IOException {
    return Files.readString(Path.of(System.getProperty("assets.dir"), "data", "items.json"), UTF_8);
  }
}
