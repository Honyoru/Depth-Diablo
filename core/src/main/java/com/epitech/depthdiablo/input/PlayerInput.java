package com.epitech.depthdiablo.input;

import com.badlogic.gdx.math.Vector2;
import java.util.Optional;

/**
 * What the user asks the player to do, whatever the device (keyboard, gamepad, a scripted fake in
 * tests). The core only knows this interface, never the keys (Q100).
 */
public interface PlayerInput {

  /**
   * Returns the direction the user wants to walk in.
   *
   * @return a new vector, safe to change; (0, 0) to stand still, at most 1 long for full speed
   */
  Vector2 moveDirection();

  /**
   * Returns the point of the world the user attacks towards, while they ask to attack (the left
   * button held on desktop). The player attacks as soon as it can, aiming from its centre to it.
   *
   * @return a new vector in world pixels, safe to change; empty while the user does not attack
   */
  Optional<Vector2> attackPoint();
}
