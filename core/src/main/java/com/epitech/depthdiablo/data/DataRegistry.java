package com.epitech.depthdiablo.data;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The entries of one json data file by id (Q129). The file holds one array
 * named after this
 * category, ex {@code { "items": [ { "id": "potion_small", ... } ] }}.
 *
 * The factory read their values here and gameplay nubers are not writen in the
 * code (Q93).
 *
 * <p>
 * The text comes from a supplier and core never be touches
 * {@code Gdx.files}(Q100) ->
 * module read the file, tests give a string. F5 {@link #reload()} (GDD 1.1.2).
 *
 * <p>
 * Pattern: Resistry read by EnemyFactory and ItemFactory.
 */
public final class DataRegistry {
  private final String category;
  private final Supplier<String> source;
  private Map<String, JsonValue> definitions = Map.of();

  /**
   * Create the regitry and read the file once.
   *
   * @param category the name oh=f the array that holds the entries,ex
   *                 {@code "items"}
   * @param source   give the json text each time the file is read
   * @throws NullPointerException                          if an argument is null
   * @throws IllegalArgumentException                      if the text has no such
   *                                                       array or an entry has
   *                                                       no id or
   *                                                       the same id as another
   * @throws com.badlogic.gdx.utils.SerializationException if the text is not
   *                                                       valid json
   */

  public DataRegistry(String category, Supplier<String> source) {
    this.category = Objects.requireNonNull(category, "category");
    this.source = Objects.requireNonNull(source, "source");
    reload();
  }

  /**
   * Reads the file again. if the new text is invalid the call throws and the
   * registry
   * keeps the entries it had and a typo during a hot reload never empties the
   * game.
   *
   * @throws IllegalArgumentException if the text has no such or an entry has no
   *                                  id or the same id has another
   * @throws com.badlogic.gdx.utils.SerializationException if the text is not valid json
   */
  public void reload(){
    JsonValue entries = new JsonReader().parse(source.get()).get(category);
    if(entries==null || !entries.isArray()) {
      throw new IllegalArgumentException("No \"" + category + "\" array in the data file");
    }
    Map<String, JsonValue> read = new LinkedHashMap<>();
    for(JsonValue entry : entries) {
      String id = entry.getString("id");
      if (read.put(id, entry) != null) {
        throw new IllegalArgumentException("Duplicate id in " + category + ": " + id);
      }
    }
    definitions = Collections.unmodifiableMap(read);
  }

  /**
   * Returns the entrey with the given id.
   *
   * @param id the entry's id ex{@code "rat"}
   * @return its json values
   * @throws IllegalArgumentException if no entry has this id
   */
  public JsonValue definition(String id) {
    JsonValue entry = definitions.get(id);
    if (entry == null) {
      throw new IllegalArgumentException("Unknown id in " + category + ": " + id);
    }
    return entry;
  }

  /**
   * Returns the ids of every entry, in file order.
   *
   * @return the ids, read-only
   */
  public List<String> ids() {
    return List.copyOf(definitions.keySet());
  }
}
