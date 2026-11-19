package com.epitech.depthdiablo.item;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.epitech.depthdiablo.combat.BurnSpec;
import com.epitech.depthdiablo.data.DataRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Q93 and Q129: one entry of data/items.json becomes a typed spec, chosen by its "class". */
class ItemSpecReaderTest {

  private static final String POTION =
      """
      { "id": "potion_small", "class": "Potion", "nameKey": "item.potion_small.name",
        "spriteId": "items/potion", "price": 10, "healAmount": 3 }
      """;

  private static final String SWORD =
      """
      { "id": "sword", "class": "Sword", "nameKey": "item.sword.name",
        "spriteId": "items/sword", "rarity": "COMMON", "price": 30, "attackBonus": 2,
        "attackSpeed": 1, "range": 24, "width": 8, "arcDegrees": 90, "swingTicks": 12 }
      """;

  private static final String ARMOR =
      """
      { "id": "leather_armor", "class": "LeatherArmor", "nameKey": "item.leather_armor.name",
        "spriteId": "items/leather_armor", "rarity": "COMMON", "price": 10, "defenseBonus": 1 }
      """;

  private static final String FIRE_RING =
      """
      { "id": "fire_ring", "class": "FireRing", "nameKey": "item.fire_ring.name",
        "spriteId": "items/fire_ring", "price": 0,
        "burn": { "damage": 1, "intervalTicks": 30, "durationTicks": 120 } }
      """;

  @Test
  void should_read_a_potion_with_all_its_values() {
    ItemSpec spec = ItemSpecReader.read("potion_small", parse(POTION));

    assertEquals(
        new ItemSpec.PotionSpec("potion_small", "item.potion_small.name", "items/potion", 10, 3),
        spec);
  }

  @Test
  void should_read_a_sword_with_all_its_values() {
    ItemSpec spec = ItemSpecReader.read("sword", parse(SWORD));

    assertEquals(
        new ItemSpec.SwordSpec(
            "sword", "item.sword.name", "items/sword", Rarity.COMMON, 30, 2, 1f, 24f, 8f, 90f, 12),
        spec);
  }

  @Test
  void should_read_an_armor_with_all_its_values() {
    ItemSpec spec = ItemSpecReader.read("leather_armor", parse(ARMOR));

    assertEquals(
        new ItemSpec.LeatherArmorSpec(
            "leather_armor",
            "item.leather_armor.name",
            "items/leather_armor",
            Rarity.COMMON,
            10,
            1),
        spec);
  }

  @Test
  void should_read_a_fire_ring_with_its_burn() {
    ItemSpec spec = ItemSpecReader.read("fire_ring", parse(FIRE_RING));

    assertEquals(
        new ItemSpec.FireRingSpec(
            "fire_ring", "item.fire_ring.name", "items/fire_ring", 0, new BurnSpec(1, 30, 120)),
        spec);
  }

  @Test
  void should_use_the_given_id() {
    ItemSpec spec = ItemSpecReader.read("potion_big", parse(POTION));

    assertEquals("potion_big", spec.id());
  }

  @Test
  void should_throw_when_the_class_is_unknown() {
    JsonValue entry = parse(POTION.replace("\"Potion\"", "\"Banana\""));

    assertThrows(IllegalArgumentException.class, () -> ItemSpecReader.read("potion_small", entry));
  }

  @Test
  void should_throw_when_the_class_is_missing() {
    JsonValue entry = parse(POTION.replace("\"class\"", "\"kind\""));

    assertThrows(IllegalArgumentException.class, () -> ItemSpecReader.read("potion_small", entry));
  }

  @Test
  void should_throw_when_a_value_is_missing() {
    JsonValue entry = parse(POTION.replace("\"healAmount\"", "\"unknownKey\""));

    assertThrows(IllegalArgumentException.class, () -> ItemSpecReader.read("potion_small", entry));
  }

  @Test
  void should_throw_when_the_sword_rarity_is_unknown() {
    JsonValue entry = parse(SWORD.replace("\"COMMON\"", "\"MYTHIC\""));

    assertThrows(IllegalArgumentException.class, () -> ItemSpecReader.read("sword", entry));
  }

  @Test
  void should_throw_when_the_fire_ring_has_no_burn() {
    JsonValue entry = parse(FIRE_RING.replace("\"burn\"", "\"heat\""));

    assertThrows(IllegalArgumentException.class, () -> ItemSpecReader.read("fire_ring", entry));
  }

  @Test
  void should_throw_when_a_burn_value_is_missing() {
    JsonValue entry = parse(FIRE_RING.replace("\"intervalTicks\"", "\"unknownKey\""));

    assertThrows(IllegalArgumentException.class, () -> ItemSpecReader.read("fire_ring", entry));
  }

  @Test
  void should_throw_when_the_burn_damage_is_0() {
    JsonValue entry = parse(FIRE_RING.replace("\"damage\": 1", "\"damage\": 0"));

    assertThrows(IllegalArgumentException.class, () -> ItemSpecReader.read("fire_ring", entry));
  }

  @Test
  void should_throw_when_the_id_is_null() {
    JsonValue entry = parse(POTION);

    assertThrows(NullPointerException.class, () -> ItemSpecReader.read(null, entry));
  }

  @Test
  void should_throw_when_the_entry_is_null() {
    assertThrows(NullPointerException.class, () -> ItemSpecReader.read("potion_small", null));
  }

  @Test
  void should_read_every_entry_of_the_data_file() throws IOException {
    Path file = Path.of(System.getProperty("assets.dir"), "data", "items.json");
    String text = Files.readString(file, UTF_8);
    DataRegistry items = new DataRegistry("items", () -> text);

    for (String id : items.getIds()) {
      assertEquals(id, ItemSpecReader.read(id, items.definition(id)).id());
    }
  }

  private static JsonValue parse(String json) {
    return new JsonReader().parse(json);
  }
}
