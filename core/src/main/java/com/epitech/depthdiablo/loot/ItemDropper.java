package com.epitech.depthdiablo.loot;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.engine.event.EventListener;
import com.epitech.depthdiablo.engine.event.GameEvent;
import com.epitech.depthdiablo.engine.event.ItemDropped;
import com.epitech.depthdiablo.item.Item;
import com.epitech.depthdiablo.item.ItemFactory;
import java.util.Objects;

/**
 * Puts items on the ground (GDD 1.4): the one entry point for an item appearing in the world. A
 * monster's loot (Arena), the items of a room (Endless), the player's dropped items and the weapon
 * or armor it swaps all go through it. It listens to {@link ItemDropped}, so a character only
 * announces what falls and never creates the item on the ground itself.
 */
public final class ItemDropper implements EventListener {
  private final ItemFactory items;
  private final World world;

  /**
   * Creates a dropper for the given world.
   *
   * @param items builds the items it spawns from {@code data/items.json}
   * @param world the world the items lie in
   * @throws NullPointerException if an argument is null
   */
  public ItemDropper(ItemFactory items, World world) {
    this.items = Objects.requireNonNull(items, "items is null");
    this.world = Objects.requireNonNull(world, "world is null");
  }

  /**
   * Puts an existing item on the ground: one the player drops or swaps.
   *
   * @param item the item to put down
   * @param at the centre of the spot, in pixels
   * @return the item on the ground, already in the world
   * @throws NullPointerException if an argument is null
   */
  public ItemDrop drop(Item item, Vector2 at) {
    ItemDrop drop = new ItemDrop(item, at);
    world.add(drop);
    return drop;
  }

  /**
   * Builds an item of {@code data/items.json} and puts it on the ground: a monster's loot or a
   * room's item.
   *
   * @param itemId the item's id, e.g. {@code "potion_small"}
   * @param at the centre of the spot, in pixels
   * @return the item on the ground, already in the world
   * @throws NullPointerException if an argument is null
   * @throws IllegalArgumentException if the id is unknown
   */
  public ItemDrop spawn(String itemId, Vector2 at) {
    return drop(items.create(itemId), at);
  }

  /**
   * Puts on the ground the item of an {@link ItemDropped} event; ignores the other events.
   *
   * @param event the event delivered by the bus
   */
  @Override
  public void onEvent(GameEvent event) {
    if (event instanceof ItemDropped dropped) {
      drop(dropped.item(), dropped.at());
    }
  }
}
