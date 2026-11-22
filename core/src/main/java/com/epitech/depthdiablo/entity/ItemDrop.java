package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.item.Item;
import java.util.Objects;

/**
 * An item lying on the ground (GDD 1.4): dropped by a monster or by the player, or placed in a
 * room. Characters walk over it; the player takes it with the pickup key when in range.
 */
public final class ItemDrop extends Entity {

  /** The side of its square box, in pixels: half a tile. */
  public static final float SIZE = 16f;

  private final Item item;

  /**
   * Puts an item on the ground.
   *
   * @param item the item lying there
   * @param centre the centre of its box, in pixels
   * @throws NullPointerException if an argument is null
   */
  public ItemDrop(Item item, Vector2 centre) {
    super(boxAround(Objects.requireNonNull(centre, "centre is null")));
    this.item = Objects.requireNonNull(item, "item is null");
  }

  /**
   * Returns the item lying there.
   *
   * @return the item; never null
   */
  public Item getItem() {
    return item;
  }

  /**
   * Returns how long the item still lies there before it vanishes.
   *
   * @return the ticks left, from {@link #LIFETIME_TICKS} down to 0
   */
  public int getTicksLeft()

  private static Aabb boxAround(Vector2 centre) {
    return new Aabb(centre.x - SIZE / 2f, centre.y - SIZE / 2f, SIZE, SIZE);
  }
}
