package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.epitech.depthdiablo.input.PlayerOrders;

/**
 * Reads the keyboard and the mouse into the player's orders (GDD, Q38): WASD to walk, the left
 * button held to attack towards the mouse.
 */
final class KeyboardPlayerInput {

  private final Vector3 mouse = new Vector3();

  /**
   * Writes what the user does this frame into the orders. The mouse is turned from screen pixels
   * into a point of the world through the camera.
   *
   * @param orders the orders the player reads every tick
   * @param camera the camera that shows the world
   */
  void readInto(PlayerOrders orders, Camera camera) {
    orders.walk(axis(Input.Keys.A, Input.Keys.D), axis(Input.Keys.S, Input.Keys.W));
    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
      camera.unproject(mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0f));
      orders.holdAttack(mouse.x, mouse.y);
    } else {
      orders.releaseAttack();
    }
    if (Gdx.input.isKeyPressed(Input.Keys.E)) {
      orders.holdPickUp();
    } else {
      orders.releasePickUp();
    }
    if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
      orders.holdUse();
    } else {
      orders.releaseUse();
    }
    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
      orders.holdDash();
    } else {
      orders.releaseDash();
    }
    if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
      orders.holdDodgeRoll();
    } else {
      orders.releaseDodgeRoll();
    }
  }

  private static float axis(int negative, int positive) {
    float value = 0f;
    if (Gdx.input.isKeyPressed(negative)) {
      value -= 1f;
    }
    if (Gdx.input.isKeyPressed(positive)) {
      value += 1f;
    }
    return value;
  }
}
