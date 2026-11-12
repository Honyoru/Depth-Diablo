package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.input.PlayerInput;

/** Reads the player's orders from the keyboard: WASD to walk (GDD, Q38). */
final class KeyboardPlayerInput implements PlayerInput {

  @Override
  public Vector2 moveDirection() {
    float x = 0f;
    float y = 0f;
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      x -= 1f;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      x += 1f;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      y -= 1f;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      y += 1f;
    }
    return new Vector2(x, y);
  }
}
