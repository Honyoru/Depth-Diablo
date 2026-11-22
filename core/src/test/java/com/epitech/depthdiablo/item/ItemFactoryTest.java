package com.epitech.depthdiablo.item;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.combat.DotSpec;
import com.epitech.depthdiablo.data.DataRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
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
    assertEquals("potion_small", potion.getId());
    assertEquals(entry.getString("nameKey"), potion.getNameKey());
    assertEquals(entry.getString("spriteId"), potion.getSpriteId());
    assertEquals(entry.getInt("price"), potion.getPrice());
    assertEquals(entry.getInt("healAmount"), potion.getHealAmount());
  }

  @Test
  void should_build_the_sword_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    Sword sword = assertInstanceOf(Sword.class, new ItemFactory(items).create("sword"));

    JsonValue entry = items.definition("sword");
    assertEquals("sword", sword.getId());
    assertEquals(entry.getString("nameKey"), sword.getNameKey());
    assertEquals(entry.getString("spriteId"), sword.getSpriteId());
    assertEquals(Rarity.valueOf(entry.getString("rarity")), sword.getRarity());
    assertEquals(entry.getInt("price"), sword.getPrice());
    assertEquals(entry.getInt("attackBonus"), sword.getAttackBonus());
    assertEquals(entry.getFloat("attackSpeed"), sword.getAttackSpeed());
    assertEquals(entry.getFloat("range"), sword.getRange());
    assertEquals(entry.getFloat("width"), sword.getWidth());
    assertEquals(entry.getFloat("arcDegrees"), sword.getArcDegrees());
    assertEquals(entry.getInt("swingTicks"), sword.getSwingTicks());
  }

  @Test
  void should_build_the_leather_armor_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    LeatherArmor armor =
        assertInstanceOf(LeatherArmor.class, new ItemFactory(items).create("leather_armor"));

    JsonValue entry = items.definition("leather_armor");
    assertEquals("leather_armor", armor.getId());
    assertEquals(entry.getString("nameKey"), armor.getNameKey());
    assertEquals(entry.getString("spriteId"), armor.getSpriteId());
    assertEquals(Rarity.valueOf(entry.getString("rarity")), armor.getRarity());
    assertEquals(entry.getInt("price"), armor.getPrice());
    assertEquals(entry.getInt("defenseBonus"), armor.getDefenseBonus());
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

    for (String id : items.getIds()) {
      assertEquals(id, factory.create(id).getId());
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

  @Test
  void should_build_the_fire_ring_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    FireRing ring = assertInstanceOf(FireRing.class, new ItemFactory(items).create("fire_ring"));

    JsonValue entry = items.definition("fire_ring");
    JsonValue burn = entry.get("burn");
    assertEquals("fire_ring", ring.getId());
    assertEquals(entry.getString("nameKey"), ring.getNameKey());
    assertEquals(entry.getString("spriteId"), ring.getSpriteId());
    assertEquals(Rarity.PASSIVE, ring.getRarity());
    assertEquals(entry.getInt("price"), ring.getPrice());
    assertEquals(
        new DotSpec(
            burn.getInt("damage"), burn.getInt("intervalTicks"), burn.getInt("durationTicks")),
        ring.getBurn());
  }

  @Test
  void should_reject_the_fire_ring_when_its_burn_is_missing() throws IOException {
    String file = readItemsFile().replace("\"burn\"", "\"heat\"");
    ItemFactory factory = new ItemFactory(new DataRegistry("items", () -> file));

    assertThrows(IllegalArgumentException.class, () -> factory.create("fire_ring"));
  }

  @Test
  void should_build_an_axe_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    Axe axe = assertInstanceOf(Axe.class, new ItemFactory(items).create("hatchet"));

    JsonValue entry = items.definition("hatchet");
    assertEquals(Rarity.valueOf(entry.getString("rarity")), axe.getRarity());
    assertEquals(entry.getInt("price"), axe.getPrice());
    assertEquals(entry.getInt("attackBonus"), axe.getAttackBonus());
    assertEquals(entry.getFloat("attackSpeed"), axe.getAttackSpeed());
    assertEquals(entry.getFloat("range"), axe.getRange());
    assertEquals(entry.getFloat("width"), axe.getWidth());
    assertEquals(entry.getFloat("arcDegrees"), axe.getArcDegrees());
    assertEquals(entry.getInt("swingTicks"), axe.getSwingTicks());
  }

  @Test
  void should_build_a_short_bow_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    ShortBow bow = assertInstanceOf(ShortBow.class, new ItemFactory(items).create("short_bow"));

    JsonValue entry = items.definition("short_bow");
    assertEquals(Rarity.valueOf(entry.getString("rarity")), bow.getRarity());
    assertEquals(entry.getInt("price"), bow.getPrice());
    assertEquals(entry.getInt("attackBonus"), bow.getAttackBonus());
    assertEquals(entry.getFloat("attackSpeed"), bow.getAttackSpeed());
    assertEquals(entry.getFloat("range"), bow.getRange());
    assertEquals(entry.getFloat("projectileSpeed"), bow.getProjectileSpeed());
    assertEquals(entry.getString("projectileSpriteId"), bow.getProjectileSpriteId());
  }

  @Test
  void should_build_a_fire_staff_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    FireStaff staff =
        assertInstanceOf(FireStaff.class, new ItemFactory(items).create("fire_staff"));

    JsonValue entry = items.definition("fire_staff");
    JsonValue burn = entry.get("burn");
    assertEquals(Rarity.valueOf(entry.getString("rarity")), staff.getRarity());
    assertEquals(entry.getInt("price"), staff.getPrice());
    assertEquals(entry.getInt("attackBonus"), staff.getAttackBonus());
    assertEquals(entry.getFloat("attackSpeed"), staff.getAttackSpeed());
    assertEquals(entry.getFloat("range"), staff.getRange());
    assertEquals(entry.getFloat("projectileSpeed"), staff.getProjectileSpeed());
    assertEquals(entry.getString("projectileSpriteId"), staff.getProjectileSpriteId());
    assertEquals(
        new DotSpec(
            burn.getInt("damage"), burn.getInt("intervalTicks"), burn.getInt("durationTicks")),
        staff.getBurn());
  }

  @Test
  void should_build_a_shield_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    Shield shield = assertInstanceOf(Shield.class, new ItemFactory(items).create("kite_shield"));

    JsonValue entry = items.definition("kite_shield");
    assertEquals(Rarity.valueOf(entry.getString("rarity")), shield.getRarity());
    assertEquals(entry.getInt("price"), shield.getPrice());
    assertEquals(entry.getInt("defenseBonus"), shield.getDefenseBonus());
  }

  @Test
  void should_build_a_stat_ring_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    StatRing ring = assertInstanceOf(StatRing.class, new ItemFactory(items).create("iron_band"));

    JsonValue entry = items.definition("iron_band");
    assertEquals(Rarity.PASSIVE, ring.getRarity());
    assertEquals(
        List.of(
            new ItemEffect(
                "ring." + entry.getString("stat").toLowerCase(Locale.ROOT),
                entry.getFloat("amount"))),
        ring.getEffects());
  }

  @Test
  void should_build_the_life_steal_ring_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    LifeStealRing ring =
        assertInstanceOf(LifeStealRing.class, new ItemFactory(items).create("ruby_ring"));

    JsonValue entry = items.definition("ruby_ring");
    assertEquals(
        List.of(new ItemEffect("effect.life_steal", entry.getFloat("fraction") * 100)),
        ring.getEffects());
  }

  @Test
  void should_build_the_executioner_ring_with_the_values_of_its_entry() throws IOException {
    DataRegistry items = realItems();

    ExecutionerRing ring =
        assertInstanceOf(ExecutionerRing.class, new ItemFactory(items).create("bone_ring"));

    JsonValue entry = items.definition("bone_ring");
    assertEquals(
        List.of(new ItemEffect("effect.execute", entry.getFloat("threshold") * 100)),
        ring.getEffects());
  }

  @Test
  void should_reject_a_stat_ring_when_its_stat_is_unknown() throws IOException {
    String file = readItemsFile().replace("\"stat\": \"ATTACK\"", "\"stat\": \"MANA\"");
    ItemFactory factory = new ItemFactory(new DataRegistry("items", () -> file));

    assertThrows(IllegalArgumentException.class, () -> factory.create("iron_band"));
  }

  private static DataRegistry realItems() throws IOException {
    String file = readItemsFile();
    return new DataRegistry("items", () -> file);
  }

  private static String readItemsFile() throws IOException {
    return Files.readString(Path.of(System.getProperty("assets.dir"), "data", "items.json"), UTF_8);
  }
}
