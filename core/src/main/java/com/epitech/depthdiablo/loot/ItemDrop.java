package com.epitech.depthdiablo.loot;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.geometry.Aabb;
import com.epitech.depthdiablo.item.Item;
import java.util.Objects;

/**
 * An item lying on the ground (GDD 1.4): dropped by a monster or by the player, or placed in a
 * room. Characters walk over it; the player takes it with the pickup key when in range.
 */
public final class ItemDrop extends Entity {

  /** The side of its square box, in pixels: half a tile. */
  public static final float SIZE = 16f;

  /** How long an item lies on the ground before it vanishes: 2 minutes. */
  public static final int LIFETIME_TICKS = 120 * GameConstants.TICKS_PER_SECOND;

  private final Item item;
  private int ticksLeft = LIFETIME_TICKS;

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
  public int getTicksLeft() {
    return ticksLeft;
  }

  /**
   * Counts its lifetime down by one tick; at 0 the item leaves the ground.
   *
   * @param tick the number of the current logic tick
   */
  @Override
  protected void onUpdate(long tick) {
    ticksLeft--;
    if (ticksLeft <= 0) {
      remove();
    }
  }

  /**
   * Writes the entity part, then the ticks it still lies there, so a rewind gives it its time back.
   *
   * @param out the photo being taken
   */
  @Override
  public void writeState(StateBuffer out) {
    super.writeState(out);
    out.writeInt(ticksLeft);
  }

  /**
   * Puts back what {@link #writeState} wrote, in the same order.
   *
   * @param in the photo being restored
   */
  @Override
  public void readState(StateBuffer in) {
    super.readState(in);
    ticksLeft = in.readInt();
  }

  private static Aabb boxAround(Vector2 centre) {
    return new Aabb(centre.x - SIZE / 2f, centre.y - SIZE / 2f, SIZE, SIZE);
  }
}
