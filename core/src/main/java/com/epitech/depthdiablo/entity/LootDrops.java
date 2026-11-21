package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.EventListener;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.item.LootTable;
import com.epitech.depthdiablo.random.RandomSource;
import java.util.Objects;

/**
 * Drops an item of the loot table where an enemy dies (GDD 1.4); the table makes rare items drop
 * less often.
 */
public final class LootDrops implements EventListener {
  private final LootTable table;
  private final ItemDropper dropper;
  private final RandomSource random;
  private final double chance;

  /**
   * Creates the loot rule.
   *
   * @param table picks the item, weighted by rarity
   * @param dropper puts the item on the ground
   * @param random rolls the drop and the item
   * @param chance the chance an enemy drops something, from 0 to 1
   * @throws NullPointerException if an object is {@code null}
   * @throws IllegalArgumentException if the chance is outside 0 to 1
   */
  public LootDrops(LootTable table, ItemDropper dropper, RandomSource random, double chance) {
    this.table = Objects.requireNonNull(table, "table is null");
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
    Aabb body = enemy.getBodyBox();
    Vector2 centre = new Vector2(body.x() + body.width() / 2, body.y() + body.height() / 2);
    table.roll(random).ifPresent(id -> dropper.spawn(id, centre));
  }
}
