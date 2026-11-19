package com.epitech.depthdiablo.item;

import java.util.Objects;

/**
 * One line of what an item gives (GDD 1.4), shown when the item is selected in the inventory: a
 * translation key and its value, e.g. {@code ("bonus.attack", 2)}. The UI turns it into text.
 *
 * @param key the translation key of the line, e.g. {@code "bonus.attack"}
 * @param value the number shown on the line
 */
public record ItemEffect(String key, float value) {

  /**
   * Checks the key.
   *
   * @throws NullPointerException if {@code key} is null
   * @throws IllegalArgumentException if {@code key} is blank
   */
  public ItemEffect {
    Objects.requireNonNull(key, "key is null");
    if (key.isBlank()) {
      throw new IllegalArgumentException("The key of an item effect must not be blank");
    }
  }
}
