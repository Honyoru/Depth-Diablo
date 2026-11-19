package com.epitech.depthdiablo.data;

/**
 * Something F5 can read again from its data file while the game runs (GDD 1.1.2): a {@link
 * DataRegistry}, or a factory that hands its values to live entities.
 *
 * <p>Pattern: part of the hot reload, called by {@link com.epitech.depthdiablo.debug.HotReload}.
 */
public interface Reloadable {

  /**
   * Reads the data again and applies it. If the new data is invalid, the call throws and the old
   * values stay, so a mistake in a data file never breaks the running game.
   *
   * @throws IllegalArgumentException if a value of the new data is missing or invalid
   * @throws com.badlogic.gdx.utils.SerializationException if the new text is not valid JSON
   */
  void reload();
}
