package com.epitech.depthdiablo.input;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.item.Inventory;
import java.util.Objects;
import java.util.Optional;

/**
 * What the user asks the player to do right now (Q38): a direction to walk in and a point to attack
 * towards. The desktop writes it every frame from the keyboard and the mouse; the player reads it
 * every logic tick, so an order is never lost when a frame runs no tick.
 */
public final class PlayerOrders implements PlayerInput {

  private final Vector2 direction = new Vector2();

  private Optional<Vector2> attackPoint = Optional.empty();

  private boolean pickUp;

  private int hotbarSlot;

  private boolean use;

  private boolean dash;

  private boolean dodgeRoll;

  @Override
  public Vector2 moveDirection() {
    return new Vector2(direction);
  }

  @Override
  public Optional<Vector2> attackPoint() {
    return attackPoint.map(Vector2::new);
  }

  @Override
  public boolean wantsPickUp() {
    return pickUp;
  }

  @Override
  public int hotbarSlot() {
    return hotbarSlot;
  }

  @Override
  public boolean wantsToUse() {
    return use;
  }

  @Override
  public boolean wantsDash() {
    return dash;
  }

  @Override
  public boolean wantsDodgeRoll() {
    return dodgeRoll;
  }

  /**
   * Walks in this direction until another order.
   *
   * @param x the horizontal part, from -1 (left) to 1 (right)
   * @param y the vertical part, from -1 (down) to 1 (up)
   */
  public void walk(float x, float y) {
    direction.set(x, y);
  }

  /**
   * Attacks towards this point of the world for as long as it is held.
   *
   * @param x the point's x in world pixels
   * @param y the point's y in world pixels
   */
  public void holdAttack(float x, float y) {
    attackPoint = Optional.of(new Vector2(x, y));
  }

  /** Stops attacking. */
  public void releaseAttack() {
    attackPoint = Optional.empty();
  }

  /** The user holds the pickup key. */
  public void holdPickUp() {
    pickUp = true;
  }

  /** The user lets go of the pickup key. */
  public void releasePickUp() {
    pickUp = false;
  }

  /**
   * The user selects a hotbar slot (keys 1, 2, 3).
   *
   * @param slot the slot, from 0 to {@link Inventory#HOTBAR_SIZE} - 1
   * @throws IndexOutOfBoundsException if {@code slot} is outside the hotbar
   */
  public void selectHotbarSlot(int slot) {
    this.hotbarSlot = Objects.checkIndex(slot, Inventory.HOTBAR_SIZE);
  }

  /** The user holds the use key. */
  public void holdUse() {
    use = true;
  }

  /** The user lets go of the use key. */
  public void releaseUse() {
    use = false;
  }

  /** The user holds the dash key. */
  public void holdDash() {
    dash = true;
  }

  /** The user lets go of the dash key. */
  public void releaseDash() {
    dash = false;
  }

  /** The user holds the dodge roll key. */
  public void holdDodgeRoll() {
    dodgeRoll = true;
  }

  /** The user lets go of the dodge roll key. */
  public void releaseDodgeRoll() {
    dodgeRoll = false;
  }
}
