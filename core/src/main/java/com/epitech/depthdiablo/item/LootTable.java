package com.epitech.depthdiablo.item;

import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.random.RandomSource;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Picks the item a dead enemy drops (GDD 1.4): each entry of {@code data/items.json} weighs as much
 * as its rarity, so commons drop often and legendaries rarely. The entries are read at each roll,
 * so new items and F5 count at once.
 */
public final class LootTable {
  /**
   * The weight of one entry of each rarity: a common entry drops about 17 times as often as a
   * legendary one.
   */
  public static final Map<Rarity, Integer> DEFAULT_WEIGHTS =
      Map.of(
          Rarity.COMMON, 50,
          Rarity.RARE, 25,
          Rarity.EPIC, 10,
          Rarity.LEGENDARY, 3,
          Rarity.CONSUMABLE, 40,
          Rarity.PASSIVE, 3);

  private final DataRegistry items;
  private final ItemFactory factory;
  private final Map<Rarity, Integer> weights;

  /**
   * Creates a loot table.
   *
   * @param items the entries of {@code data/items.json}
   * @param factory builds an entry to read its rarity
   * @param weights the weight of each rarity; a missing rarity weighs 0 and never drops
   * @throws NullPointerException if an argument or a weight is {@code null}
   * @throws IllegalArgumentException if a weight is negative
   */
  public LootTable(DataRegistry items, ItemFactory factory, Map<Rarity, Integer> weights) {
    this.items = Objects.requireNonNull(items, "items is null");
    this.factory = Objects.requireNonNull(factory, "factory is null");
    Objects.requireNonNull(weights, "weights is null");
    Map<Rarity, Integer> copy = new EnumMap<>(Rarity.class);
    weights.forEach((rarity, weight) -> {
      if (weight < 0) {
        throw new IllegalArgumentException("Weight of " + rarity + " is negative: " + weight);
      }
      copy.put(rarity, weight);
    });
    this.weights = copy;
  }

  /**
   * Picks an item id, each entry weighing as much as its rarity.
   *
   * @param random rolls the pick
   * @return the id, or empty when no entry has a weight above 0
   * @throws NullPointerException if the random source is {@code null}
   */
  public Optional<String> roll(RandomSource random) {
    Objects.requireNonNull(random, "random is null");
    List<String> ids = items.getIds();
    int[] entryWeights = new int[ids.size()];
    int total = 0;
    for (int i = 0; i < ids.size(); i++) {
      entryWeights[i] = weights.getOrDefault(factory.create(ids.get(i)).getRarity(), 0);
      total += entryWeights[i];
    }
    if (total == 0) {
      return Optional.empty();
    }
    int pick = random.nextInt(total);
    int i = 0;
    while (pick >= entryWeights[i]) {
      pick -= entryWeights[i];
      i++;
    }
    return Optional.of(ids.get(i));
  }
}
