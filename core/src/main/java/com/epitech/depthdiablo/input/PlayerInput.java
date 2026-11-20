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

  /**
   * Tells whether the user holds the pickup key (GDD 1.4). The player picks up once per press.
   *
   * @return true while the key is held
   */
  boolean wantsPickUp();

  /**
   * Returns the hotbar slot the user selected (keys 1, 2, 3): the one the use key uses.
   *
   * @return the slot, from 0 to {@link com.epitech.depthdiablo.item.Inventory#HOTBAR_SIZE} - 1
   */
  int hotbarSlot();

  /**
   * Tells whether the user holds the use key (GDD 1.4). The player uses one consumable per press.
   *
   * @return true while the key is held
   */
  boolean wantsToUse();

  /**
   * Tells whether the user holds the dash key (GDD 2.4, Q40). The player dashes once per press.
   *
   * @return true while the key is held
   */
  boolean wantsDash();

  /**
   * Tells whether the user holds the dodge roll key (GDD 2.4, Q40). The player rolls once per
   * press.
   *
   * @return true while the key is held
   */
  boolean wantsDodgeRoll();
}
