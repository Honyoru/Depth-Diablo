package com.epitech.depthdiablo.loot;

import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.item.ItemFactory;
import com.epitech.depthdiablo.item.Rarity;
import com.epitech.depthdiablo.random.RandomSource;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Picks the item a dead enemy drops (GDD 1.4): first a rarity, by its share of the drops, then one
 * entry of {@code data/items.json} of that rarity, each with the same chance. Adding items to a
 * rarity never changes how often the other rarities drop. The entries are read at each roll, so new
 * items and F5 count at once.
 */
public final class LootTable {
  /**
   * The share of the drops of each rarity, in percent: potions and rings most of the time, a
   * legendary 2 drops in 100.
   */
  public static final Map<Rarity, Integer> DEFAULT_SHARES =
      Map.of(
          Rarity.CONSUMABLE, 35,
          Rarity.PASSIVE, 25,
          Rarity.COMMON, 20,
          Rarity.RARE, 13,
          Rarity.EPIC, 5,
          Rarity.LEGENDARY, 2);

  private final DataRegistry items;
  private final ItemFactory factory;
  private final Map<Rarity, Integer> shares;

  /**
   * Creates a loot table.
   *
   * @param items the entries of {@code data/items.json}
   * @param factory builds an entry to read its rarity
   * @param shares the share of the drops of each rarity; a missing rarity never drops
   * @throws NullPointerException if an argument or a share is {@code null}
   * @throws IllegalArgumentException if a share is negative
   */
  public LootTable(DataRegistry items, ItemFactory factory, Map<Rarity, Integer> shares) {
    this.items = Objects.requireNonNull(items, "items is null");
    this.factory = Objects.requireNonNull(factory, "factory is null");
    Objects.requireNonNull(shares, "shares is null");
    Map<Rarity, Integer> copy = new EnumMap<>(Rarity.class);
    shares.forEach((rarity, share) -> {
      if (share < 0) {
        throw new IllegalArgumentException("Share of " + rarity + " is negative: " + share);
      }
      copy.put(rarity, share);
    });
    this.shares = copy;
  }

  /**
   * Picks an item id: a rarity by its share, among the rarities that have at least one entry, then
   * one entry of it, each with the same chance.
   *
   * @param random rolls the pick
   * @return the id, or empty when no rarity with a share above 0 has an entry
   * @throws NullPointerException if the random source is {@code null}
   */
  public Optional<String> roll(RandomSource random) {
    Objects.requireNonNull(random, "random is null");
    Map<Rarity, List<String>> byRarity = new EnumMap<>(Rarity.class);
    for (String id : items.getIds()) {
      byRarity.computeIfAbsent(factory.create(id).getRarity(), r -> new ArrayList<>()).add(id);
    }
    byRarity.keySet().removeIf(rarity -> shares.getOrDefault(rarity, 0) == 0);
    int total = byRarity.keySet().stream().mapToInt(shares::get).sum();
    if (total == 0) {
      return Optional.empty();
    }
    int pick = random.nextInt(total);
    for (Map.Entry<Rarity, List<String>> entry : byRarity.entrySet()) {
      pick -= shares.get(entry.getKey());
      if (pick < 0) {
        List<String> ids = entry.getValue();
        return Optional.of(ids.get(random.nextInt(ids.size())));
      }
    }
    throw new IllegalStateException("The pick went past every rarity");
  }
}
