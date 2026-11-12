package com.epitech.depthdiablo.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.utils.SerializationException;

import java.beans.Transient;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Reading one json data file by id, and reading it aganf for F5.
 */
class DataRegistryTest {
  private static final String TWO_RATS =
  """
  {"enemies" : [ { "id":"rat","maxHp":3},{"id":"giant_rat","maxHp":9}
]}
  """;

  private String file = TWO_RATS;

  @Test
  void should_find_an_entry_by_its_id_when_loaded() {
    DataRegistry registry = new DataRegistry("enemies", () -> file);

    int maxHp = registry.definition("giant_rat").getInt("maxHp");

    assertEquals(9,maxHp);

  }

  @Test
  void should_list_the_ids_in_file_order_when_loaded() {
    DataRegistry registry = new DataRegistry("enemies", () -> file);

    assertEquals(List.of("rat", "giant_rat"), registry.ids());
  }

  @Test
  void should_reject_an_unknown_id() {
    DataRegistry registry = new DataRegistry("enemies", ()-> file);

    assertThrows(IllegalArgumentException.class, ()-> registry.definition("dragon"));
  }

  @Test
  void should_reject_a_file_when_it_has_no_array_for_the_category() {
    file = "{ \"items\": [] }";

    assertThrows(IllegalArgumentException.class, () -> new DataRegistry("enemies", ()->file));
  }

  @Test
  void should_reject_a_file_when_an_entry_has_no_id() {
    file = "{ \"enemies\": [ { \"maxHp\": 3 } ] }";

    assertThrows(IllegalArgumentException.class, () -> new DataRegistry("enemies", ()->file));
  }

  @Test
  void should_reject_a_file_when_two_entries_share_an_id() {
    file = "{ \"enemies\": [ { \"id\": \"rat\" }, { \"id\": \"rat\" } ] }";

    assertThrows(IllegalArgumentException.class, () -> new DataRegistry("enemies", ()->file));
  }

  @Test
  void should_read_the_new_values_when_reloaded() {
    DataRegistry registry = new DataRegistry("enemies", () -> file);
    file = "{ \"enemies\": [ { \"id\": \"rat\", \"maxHp\": 5 } ] }";

    registry.reload();

    assertEquals(5, registry.definition("rat").getInt("maxHp"));
    assertEquals(List.of("rat"), registry.ids());
  }

  @Test
  void should_keep_the_old_values_when_the_reloaded_file_is_invalid() {
    DataRegistry registry = new DataRegistry("enemies", () -> file);
    file = "{ \"enemies\": [ { \"id\": ";

    assertThrows(SerializationException.class, registry::reload);

    assertEquals(3, registry.definition("rat").getInt("maxHp"));
  }

  @Test
  void should_reject_a_missing_source() {
    assertThrows(NullPointerException.class, () -> new DataRegistry("enemies",null));
  }
}
