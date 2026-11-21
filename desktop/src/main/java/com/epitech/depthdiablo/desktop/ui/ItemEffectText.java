package com.epitech.depthdiablo.desktop.ui;

import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.item.ItemEffect;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Writes what an item gives for the inventory page (GDD 1.4): one line per effect, the three burn
 * values on one line, times in seconds. There is no translation file yet, so the text is English.
 */
final class ItemEffectText {
  private ItemEffectText() {}

  /**
   * Writes the lines of an item's effects.
   *
   * @param effects what the item gives, from {@code Item.getEffects()}
   * @return one line per effect, the burn as one line
   * @throws NullPointerException if {@code effects} is null
   */
  static List<String> lines(List<ItemEffect> effects) {
    Objects.requireNonNull(effects, "effects is null");
    List<String> lines = new ArrayList<>();
    float burnDamage = 0;
    float burnInterval = 0;
    float burnDuration = 0;
    boolean burns = false;
    for (ItemEffect effect : effects) {
      float value = effect.value();
      switch (effect.key()) {
        case "bonus.attack" -> lines.add("Attack +" + number(value));
        case "bonus.attack_speed" -> lines.add("Attack speed x" + number(value));
        case "bonus.defense" -> lines.add("Defense +" + number(value));
        case "effect.heal" -> lines.add("Heals " + number(value) + " HP");
        case "effect.burn_damage" -> {
          burnDamage = value;
          burns = true;
        }
        case "effect.burn_interval_ticks" -> burnInterval = value;
        case "effect.burn_duration_ticks" -> burnDuration = value;
        case "effect.life_steal" -> lines.add("Heals " + number(value) + "% of damage dealt");
        case "effect.execute" -> lines.add("Kills targets under " + number(value) + "% HP");
        default ->
            lines.add(
                effect.key().startsWith("ring.")
                    ? name(effect.key().substring("ring.".length())) + " +" + number(value)
                    : effect.key() + " " + number(value));
      }
    }
    if (burns) {
      lines.add(
          "Burns "
              + number(burnDamage)
              + " damage every "
              + seconds(burnInterval)
              + " s for "
              + seconds(burnDuration)
              + " s");
    }
    return List.copyOf(lines);
  }

  /**
   * Writes an item's name from its id, until a translation file exists.
   *
   * @param id the item's id, e.g. {@code "fire_ring"}
   * @return the name, e.g. {@code "Fire ring"}
   */
  static String name(String id) {
    String words = Objects.requireNonNull(id, "id is null").replace('_', ' ');
    return words.isEmpty()
        ? words
        : words.substring(0, 1).toUpperCase(Locale.ROOT) + words.substring(1);
  }

  private static String seconds(float ticks) {
    return number(ticks / GameConstants.TICKS_PER_SECOND);
  }

  /** A number with as few decimals as it needs: 2, 1.5, 0.5. */
  private static String number(float value) {
    return new BigDecimal(Float.toString(value)).stripTrailingZeros().toPlainString();
  }
}
