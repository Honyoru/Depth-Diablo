package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.input.PlayerInput;
import java.util.Objects;

/**
 * The character the user controls (GDD 1.3.1). Every tick it reads its {@link PlayerInput} and
 * walks. Knight, Mage and Archer are its concrete classes.
 */
public abstract class Player extends Character {
  private final PlayerInput input;
  private final World world;
  private boolean moving;

  /**
   * Creates a player that stands still.
   *
   * @param bodyBox the box the player occupies in the world; must not be {@code null}
   * @param stats where its stats come from; must not be {@code null}
   * @param hurtbox the player's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param input what the user asks for; must not be {@code null}
   * @param world the world the player walks in, asked what blocks it; must not be {@code null}
   */
  protected Player(
      Aabb bodyBox,
      StatSource stats,
      Aabb hurtbox,
      int invincibilityTicksAfterHit,
      PlayerInput input,
      World world) {
    super(bodyBox, stats, hurtbox, invincibilityTicksAfterHit);
    this.input =
        Objects.requireNonNull(input, "The input should not be null for player to be movable");
    this.world =
        Objects.requireNonNull(world, "The world should not be null player to spawn in it");
  }

  @Override
  /**
   * Walks one tick in the direction of the input, at the SPEED stat (pixels per second). A
   * direction longer than 1 is shortened to 1, so a diagonal is not faster than a straight line.
   */
  protected final void think(long tick) {
    Vector2 direction = input.moveDirection();
    if (direction.len2() > 1f) {
      direction.nor();
    }
    moving = !isDead() && !direction.isZero();
    if (!moving) {
      return;
    }
    Vector2 step = direction.scl(speed() / GameConstants.TICKS_PER_SECOND);
    Vector2 position = position();

    Vector2 alongX = new Vector2(position).add(step.x, 0f);
    if (canStandAt(alongX)) {
      position.set(alongX);
    }
    Vector2 alongY = new Vector2(position).add(0f, step.y);
    if (canStandAt(alongY)) {
      position.set(alongY);
    }
    moveTo(position);
  }

  /** Tells whether the player's body fits at the given bottom-left corner. */
  private boolean canStandAt(Vector2 corner) {
    Aabb body = bodyBox();
    return world.isFree(new Aabb(corner.x, corner.y, body.width(), body.height()), this);
  }

  /**
   * Tells whether the player walked during the last tick. The renderer picks the animation with it.
   *
   * @return true if it is alive and moved
   */
  public boolean isMoving() {
    return moving;
  }

  /**
   * Returns the player's side. Knight, Mage and Archer all fight for it, so it is final.
   *
   * @return {@link Faction#PLAYER}
   */
  @Override
  public final Faction faction() {
    return Faction.PLAYER;
  }
}
