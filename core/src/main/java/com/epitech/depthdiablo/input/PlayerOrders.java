package com.epitech.depthdiablo.input;

import com.badlogic.gdx.math.Vector2;
import java.util.Optional;

/**
 * What the user asks the player to do right now (Q38): a direction to walk in and a point to attack
 * towards. The desktop writes it every frame from the keyboard and the mouse; the player reads it
 * every logic tick, so an order is never lost when a frame runs no tick.
 */
public final class PlayerOrders implements PlayerInput {

  private final Vector2 direction = new Vector2();

  private Optional<Vector2> attackPoint = Optional.empty();

  @Override
  public Vector2 moveDirection() {
    return new Vector2(direction);
  }

  @Override
  public Optional<Vector2> attackPoint() {
    return attackPoint.map(Vector2::new);
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
}
