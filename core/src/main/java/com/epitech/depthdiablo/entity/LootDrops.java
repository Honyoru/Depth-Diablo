package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.EventListener;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.random.RandomSource;
import java.util.List;
import java.util.Objects;

/**
 * Drops a random item where an enemy dies (GDD 1.4). The pool is every id of {@code
 * data/items.json}, read at each death, so new entries and F5 count at once.
 */
public final class LootDrops implements EventListener {
  private final DataRegistry items;
  private final ItemDropper dropper;
  private final RandomSource random;
  private final double chance;

  /**
   * Creates the loot rule.
   *
   * @param items the entries of {@code data/items.json}
   * @param dropper puts the item on the ground
   * @param random rolls the drop and picks the item
   * @param chance the chance an enemy drops something, from 0 to 1
   * @throws NullPointerException if an object is {@code null}
   * @throws IllegalArgumentException if the chance is outside 0 to 1
   */
  public LootDrops(DataRegistry items, ItemDropper dropper, RandomSource random, double chance) {
    this.items = Objects.requireNonNull(items, "items is null");
    this.dropper = Objects.requireNonNull(dropper, "dropper is null");
    this.random = Objects.requireNonNull(random, "random is null");
    if (!(chance >= 0 && chance <= 1)) {
      throw new IllegalArgumentException("Drop chance must be from 0 to 1: " + chance);
    }
    this.chance = chance;
  }

  /**
   * Rolls a drop when an enemy dies; ignores every other event.
   *
   * @param event the event delivered by the bus
   */
  @Override
  public void onEvent(GameEvent event) {
    if (!(event instanceof EntityDied died) || !(died.source() instanceof Enemy enemy)) {
      return;
    }
    if (random.nextDouble() >= chance) {
      return;
    }
    List<String> ids = items.getIds();
    if (ids.isEmpty()) {
      return;
    }
    Aabb body = enemy.getBodyBox();
    Vector2 centre = new Vector2(body.x() + body.width() / 2, body.y() + body.height() / 2);
    dropper.spawn(ids.get(random.nextInt(ids.size())), centre);
  }
}
