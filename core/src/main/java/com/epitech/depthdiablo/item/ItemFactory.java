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
  public Item create(String id) { // ** TO REFACTOR WITH A FACTORY FOR EACH TYPE */
    ItemSpec spec = ItemSpecReader.read(id, items.definition(id));
    return switch (spec) {
      case ItemSpec.PotionSpec potion ->
          new Potion(
              potion.id(),
              potion.nameKey(),
              potion.spriteId(),
              potion.price(),
              potion.healAmount());
      case ItemSpec.SwordSpec sword ->
          new Sword(
              sword.id(),
              sword.nameKey(),
              sword.spriteId(),
              sword.rarity(),
              sword.price(),
              sword.attackBonus(),
              sword.attackSpeed(),
              sword.range(),
              sword.width(),
              sword.arcDegrees(),
              sword.swingTicks());
      case ItemSpec.LeatherArmorSpec armor ->
          new LeatherArmor(
              armor.id(),
              armor.nameKey(),
              armor.spriteId(),
              armor.rarity(),
              armor.price(),
              armor.defenseBonus());
      case ItemSpec.FireRingSpec fireRing ->
          new FireRing(
              fireRing.id(),
              fireRing.nameKey(),
              fireRing.spriteId(),
              fireRing.price(),
              fireRing.burn());
      case ItemSpec.AxeSpec axe ->
          new Axe(
              axe.id(),
              axe.nameKey(),
              axe.spriteId(),
              axe.rarity(),
              axe.price(),
              axe.attackBonus(),
              axe.attackSpeed(),
              axe.range(),
              axe.width(),
              axe.arcDegrees(),
              axe.swingTicks());
      case ItemSpec.ShortBowSpec bow ->
          new ShortBow(
              bow.id(),
              bow.nameKey(),
              bow.spriteId(),
              bow.rarity(),
              bow.price(),
              bow.attackBonus(),
              bow.attackSpeed(),
              bow.range(),
              bow.projectileSpeed());
      case ItemSpec.ShieldSpec shield ->
          new Shield(
              shield.id(),
              shield.nameKey(),
              shield.spriteId(),
              shield.rarity(),
              shield.price(),
              shield.defenseBonus());
      case ItemSpec.StatRingSpec ring ->
          new StatRing(
              ring.id(), ring.nameKey(), ring.spriteId(), ring.price(), ring.stat(), ring.amount());
      case ItemSpec.LifeStealRingSpec ring ->
          new LifeStealRing(
              ring.id(), ring.nameKey(), ring.spriteId(), ring.price(), ring.fraction());
      case ItemSpec.ExecutionerRingSpec ring ->
          new ExecutionerRing(
              ring.id(), ring.nameKey(), ring.spriteId(), ring.price(), ring.threshold());
    };
  }
}
