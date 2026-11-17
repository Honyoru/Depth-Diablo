package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.data.DataRegistry;
import java.util.Objects;

/**
 * Builds items from {@code data/items.json} (Q23, Q129). Each entry names its class ex {@code
 * "class":"Potion"} plus its values; {@link ItemSpecReader} reads them into an {@link ItemSpec}.
 * After F5 the next {@link #create(String)} use the new values.
 *
 * <p>Pattern: Simple Factory
 */
public final class ItemFactory {
  private final DataRegistry items;

  /**
   * Creates a factory thet read its value from the given regitry.
   *
   * @param items the entries of {@code data/items.json}
   * @throws NullPointerException if {@code items} is null
   */
  public ItemFactory(DataRegistry items) {
    this.items = Objects.requireNonNull(items, "The item data registry must not be null");
  }

  /**
   * Builds a new item from its entry.
   *
   * @param id the item's id ex {@code "potion_small"}
   * @return a new item which the entry's values
   * @throws IllegalArgumentException if the id is unknown, the class is unknown or a value is
   *     missing or invalid
   */
  public Item create(String id) {
    ItemSpec spec = ItemSpecReader.read(id, items.definition(id));
    return switch (spec) {
      case ItemSpec.PotionSpec p ->
          new Potion(p.id(), p.nameKey(), p.spriteId(), p.price(), p.healAmount());
      case ItemSpec.SwordSpec s ->
          new Sword(
              s.id(),
              s.nameKey(),
              s.spriteId(),
              s.rarity(),
              s.price(),
              s.attackBonus(),
              s.attackSpeed(),
              s.range(),
              s.width(),
              s.arcDegrees(),
              s.swingTicks());
      case ItemSpec.FireRingSpec f ->
          new FireRing(f.id(), f.nameKey(), f.spriteId(), f.price(), f.burn());
    };
  }
}
