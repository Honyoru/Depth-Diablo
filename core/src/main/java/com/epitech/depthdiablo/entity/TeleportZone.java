package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;

/**
 * The teleport zone to the next floor (GDD 1.1 step 3, Q11). It stays closed while an enemy of the
 * floor is alive, then opens. A living player who stands in it, the centre of its body inside the
 * zone, charges it; after {@link #CHARGE_TICKS} ticks in a row the zone is ready and the game moves
 * to the next floor. Stepping out resets the charge. Nothing is stopped by it.
 */
public final class TeleportZone extends Entity {

  /** How long the player stands in the open zone before it sends them on: 3 seconds. */
  public static final int CHARGE_TICKS = 3 * GameConstants.TICKS_PER_SECOND;

  private final World world;

  private boolean open;

  /** Ticks in a row the player has stood in the open zone, up to {@link #CHARGE_TICKS}. */
  private int chargeTicks;

  /** Whether a living player stood in the open zone during the contacts of the last tick. */
  private boolean playerInside;

  /**
   * Puts a closed zone on the floor.
   *
   * @param box where the zone lies, in pixels
   * @param world the world whose enemies keep the zone closed while one of them lives
   * @throws NullPointerException if an argument is {@code null}
   */
  public TeleportZone(Aabb box, World world) {
    super(box);
    this.world = Objects.requireNonNull(world, "world is null");
  }

  /**
   * Opens the zone once no enemy of the world is alive, then charges it while the player stood in
   * it during the last tick, or resets the charge when they did not.
   *
   * @param tick the number of the current logic tick
   */
  @Override
  protected void onUpdate(long tick) {
    if (!open && noEnemyAlive()) {
      open = true;
    }
    if (open && playerInside) {
      chargeTicks = Math.min(CHARGE_TICKS, chargeTicks + 1);
    } else {
      chargeTicks = 0;
    }
    playerInside = false;
  }

  /**
   * Notes that a living player stands in the open zone, when the centre of its body is inside it.
   *
   * @param other the entity that touches the zone
   * @throws NullPointerException if {@code other} is {@code null}
   */
  @Override
  public void onContact(Entity other) {
    super.onContact(other);
    if (open && other instanceof Player player && !player.isDead() && holdsCentreOf(player)) {
      playerInside = true;
    }
  }

  /**
   * Tells whether the zone is open: no enemy of the floor is alive any more.
   *
   * @return true once it opened; it never closes again
   */
  public boolean isOpen() {
    return open;
  }

  /**
   * Tells whether the player stands in the open zone and charges it.
   *
   * @return true from the first tick in the zone until the player steps out
   */
  public boolean isCharging() {
    return chargeTicks > 0;
  }

  /**
   * Tells whether the player stood in the open zone long enough: the next floor can load.
   *
   * @return true after {@link #CHARGE_TICKS} ticks in a row in the zone
   */
  public boolean isReady() {
    return chargeTicks == CHARGE_TICKS;
  }

  /**
   * Writes the entity part, then whether the zone is open, its charge and whether the player stood
   * in it during the last tick.
   *
   * @param out the photo being taken
   */
  @Override
  public void writeState(StateBuffer out) {
    super.writeState(out);
    out.writeBoolean(open);
    out.writeInt(chargeTicks);
    out.writeBoolean(playerInside);
  }

  /**
   * Puts back what {@link #writeState} wrote, in the same order.
   *
   * @param in the photo being restored
   */
  @Override
  public void readState(StateBuffer in) {
    super.readState(in);
    open = in.readBoolean();
    chargeTicks = in.readInt();
    playerInside = in.readBoolean();
  }

  private boolean noEnemyAlive() {
    for (Entity entity : world.getEntities()) {
      if (entity instanceof Enemy enemy && !enemy.isDead() && !enemy.isRemoved()) {
        return false;
      }
    }
    return true;
  }

  private boolean holdsCentreOf(Player player) {
    Aabb body = player.getBodyBox();
    float x = body.x() + body.width() / 2f;
    float y = body.y() + body.height() / 2f;
    Aabb zone = getBodyBox();
    return x >= zone.x()
        && x <= zone.x() + zone.width()
        && y >= zone.y()
        && y <= zone.y() + zone.height();
  }
}
