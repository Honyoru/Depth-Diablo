package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.input.PlayerInput;
import com.epitech.depthdiablo.item.Inventory;
import com.epitech.depthdiablo.item.Weapon;
import java.util.Objects;

/**
 * The character the user controls (GDD 1.3.1). Every tick it reads its {@link PlayerInput} and
 * walks. Knight, Mage and Archer are its concrete classes.
 */
public abstract class Player extends Character {

  private final PlayerInput input;

  private final Inventory inventory;

  private boolean moving;

  /**
   * Creates a player that stands still.
   *
   * @param bodyBox the box the player occupies in the world; must not be {@code null}
   * @param stats where its stats come from; must not be {@code null}
   * @param startingWeapon the weapon it starts with: its base attack and the first item of its
   *     inventory; must not be {@code null}
   * @param hurtbox the player's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param input what the user asks for; must not be {@code null}
   * @param world the world the player walks in, asked what blocks it; must not be {@code null}
   */
  protected Player(
      Aabb bodyBox,
      StatSource stats,
      Weapon startingWeapon,
      Aabb hurtbox,
      int invincibilityTicksAfterHit,
      PlayerInput input,
      World world) {
    super(bodyBox, stats, startingWeapon, hurtbox, invincibilityTicksAfterHit, world);
    this.input = Objects.requireNonNull(input, "The input of a player must not be null");
    this.inventory = new Inventory(startingWeapon);
    rebuild(inventory.getModifiers());
  }

  /**
   * Returns the player's bag: its hotbar and its equipped weapon (GDD 1.4).
   *
   * @return the inventory; never null
   */
  public Inventory getInventory() {
    return inventory;
  }

  /**
   * Returns the player's side. Knight, Mage and Archer all fight for it, so it is final.
   *
   * @return {@link Faction#PLAYER}
   */
  @Override
  public final Faction getFaction() {
    return Faction.PLAYER;
  }

  /**
   * Walks one tick in the direction of the input, then, while the user attacks, starts an attack
   * towards the point as soon as it can (see {@link #canAttack()}).
   */
  @Override
  protected final void think(long tick) {
    walk();
    input.attackPoint().filter(point -> canAttack()).ifPresent(this::attackTowards);
  }

  /**
   * Walks one tick in the direction of the input, at the SPEED stat (pixels per second). A
   * direction longer than 1 is shortened to 1, so a diagonal is not faster than a straight line.
   */
  private void walk() {
    Vector2 direction = input.moveDirection();
    if (direction.len2() > 1f) {
      direction.nor();
    }
    moving = !isDead() && !direction.isZero();
    if (!moving) {
      return;
    }
    Vector2 step = direction.scl(getSpeed() / GameConstants.TICKS_PER_SECOND);
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

  /** Attacks with the attack of the build, aimed from the player's centre to the point. */
  private void attackTowards(Vector2 point) {
    Aabb body = getBodyBox();
    float aim =
        new Vector2(point)
            .sub(body.x() + body.width() / 2f, body.y() + body.height() / 2f)
            .angleDeg();
    startAttack(getBuild().attack(), aim, world());
  }

  /** Tells whether the player's body fits at the given bottom-left corner. */
  private boolean canStandAt(Vector2 corner) {
    Aabb body = getBodyBox();
    return world().isFree(new Aabb(corner.x, corner.y, body.width(), body.height()), this);
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
   * Writes the character part, then whether the player walks, so the rewind shows the right
   * animation even though no tick runs while R is held.
   *
   * @param out the photo being taken
   */
  @Override
  public void writeState(StateBuffer out) {
    super.writeState(out);
    out.writeBoolean(moving);
  }

  /**
   * Puts back what {@link #writeState} wrote, in the same order.
   *
   * @param in the photo being restored
   */
  @Override
  public void readState(StateBuffer in) {
    super.readState(in);
    moving = in.readBoolean();
  }
}
