package com.epitech.depthdiablo.entity;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.ItemDropped;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.input.PlayerInput;
import com.epitech.depthdiablo.item.Armor;
import com.epitech.depthdiablo.item.Consumable;
import com.epitech.depthdiablo.item.Inventory;
import com.epitech.depthdiablo.item.Item;
import com.epitech.depthdiablo.item.PassiveItem;
import com.epitech.depthdiablo.item.Weapon;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * The character the user controls (GDD 1.3.1). Every tick it reads its {@link PlayerInput} and
 * walks. Knight, Mage and Archer are its concrete classes.
 */
public abstract class Player extends Character {

  private final PlayerInput input;

  private final Inventory inventory;

  /** How far an item on the ground can be from the player's centre to be picked up, in pixels. */
  public static final float PICK_UP_RANGE = 32f;

  private boolean moving;

  /** Whether the pickup key was held on the previous tick: one pickup per press. */
  private boolean pickUpHeld;

  /**
   * Creates a player that stands still.
   *
   * @param bodyBox the box the player occupies in the world; must not be {@code null}
   * @param stats where its stats come from; must not be {@code null}
   * @param startingWeapon the weapon it starts with: its base attack and the first item of its
   *     inventory; must not be {@code null}
   * @param startingArmor the armor it starts with, in its inventory; must not be {@code null}
   * @param hurtbox the player's hurtbox; must not be {@code null}
   * @param invincibilityTicksAfterHit invincibility ticks granted after a hit; 0 or more
   * @param input what the user asks for; must not be {@code null}
   * @param world the world the player walks in, asked what blocks it; must not be {@code null}
   */
  protected Player(
      Aabb bodyBox,
      StatSource stats,
      Weapon startingWeapon,
      Armor startingArmor,
      Aabb hurtbox,
      int invincibilityTicksAfterHit,
      PlayerInput input,
      World world) {
    super(bodyBox, stats, startingWeapon, hurtbox, invincibilityTicksAfterHit, world);
    this.input = Objects.requireNonNull(input, "The input of a player must not be null");
    this.inventory = new Inventory(startingWeapon, startingArmor);
    rebuild(inventory.getModifiers());
  }

  /**
   * Returns the player's bag: its hotbar, its equipped weapon and its worn armor (GDD 1.4).
   *
   * @return the inventory; never null
   */
  public Inventory getInventory() {
    return inventory;
  }

  /**
   * Picks up an item and rebuilds (GDD 1.4): a consumable goes in the hotbar, a passive item in the
   * first free passive slot, a weapon or an armor replaces the one held.
   *
   * @param item the item the player walks on
   * @return what is left on the ground: the old weapon or armor, the item itself when no slot takes
   *     it, or nothing
   * @throws NullPointerException if {@code item} is {@code null}
   * @throws IllegalArgumentException if the item is of no kind the inventory takes
   */
  public final Optional<Item> pickUp(Item item) {
    Objects.requireNonNull(item, "item is null");
    Optional<Item> left =
        switch (item) {
          case Consumable consumable ->
              inventory.add(consumable) ? Optional.empty() : Optional.of(item);
          case PassiveItem passive -> inventory.add(passive) ? Optional.empty() : Optional.of(item);
          case Weapon weapon -> Optional.of(inventory.equip(weapon));
          case Armor armor -> Optional.of(inventory.equip(armor));
          default ->
              throw new IllegalArgumentException(item.getId() + " cannot go in an inventory");
        };
    rebuild(inventory.getModifiers());
    return left;
  }

  /**
   * Takes the passive item out of a slot and rebuilds without it.
   *
   * @param slot the passive slot, from 0 to {@link Inventory#PASSIVE_SIZE} - 1
   * @return the item to drop on the ground, or nothing if the slot was free
   * @throws IndexOutOfBoundsException if {@code slot} is outside the passive slots
   */
  public final Optional<PassiveItem> dropPassive(int slot) {
    Optional<PassiveItem> dropped = inventory.removePassive(slot);
    rebuild(inventory.getModifiers());
    return dropped;
  }

  /**
   * Takes the consumable out of a hotbar slot; consumables do not change the build.
   *
   * @param slot the hotbar slot, from 0 to {@link Inventory#HOTBAR_SIZE} - 1
   * @return the consumable to drop on the ground, or nothing if the slot was free
   * @throws IndexOutOfBoundsException if {@code slot} is outside the hotbar
   */
  public final Optional<Consumable> dropConsumable(int slot) {
    return inventory.removeConsumable(slot);
  }

  /**
   * Swaps two passive slots and rebuilds, since passive items apply in slot order.
   *
   * @param first one passive slot, from 0 to {@link Inventory#PASSIVE_SIZE} - 1
   * @param second the other passive slot, from 0 to {@link Inventory#PASSIVE_SIZE} - 1
   * @throws IndexOutOfBoundsException if a slot is outside the passive slots
   */
  public final void swapPassiveSlots(int first, int second) {
    inventory.swapPassiveSlots(first, second);
    rebuild(inventory.getModifiers());
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
    boolean pressed = input.wantsPickUp() && !pickUpHeld;
    pickUpHeld = input.wantsPickUp();
    if (pressed) {
      getPickUpTarget().ifPresent(this::take);
    }
  }

  /**
   * Returns the item on the ground the player would pick up now: the nearest one whose centre is
   * within {@link #PICK_UP_RANGE} of the player's centre, not taken yet (GDD 1.4).
   *
   * @return that item on the ground, or nothing if none is in range
   */
  public final Optional<ItemDrop> getPickUpTarget() {
    Vector2 centre = centre();
    return world().getEntities().stream()
        .filter(ItemDrop.class::isInstance)
        .map(ItemDrop.class::cast)
        .filter(drop -> !drop.isRemoved())
        .filter(drop -> distanceTo(drop, centre) <= PICK_UP_RANGE)
        .min(Comparator.comparingDouble(drop -> distanceTo(drop, centre)));
  }

  /** Takes an item off the ground; what does not fit falls back where the player stands. */
  private void take(ItemDrop drop) {
    drop.remove();
    pickUp(drop.getItem())
        .ifPresent(left -> world().getEventBus().publish(new ItemDropped(this, left, centre())));
  }

  /** Returns the centre of the player's body, in pixels. */
  private Vector2 centre() {
    Aabb body = getBodyBox();
    return new Vector2(body.x() + body.width() / 2f, body.y() + body.height() / 2f);
  }

  /** Returns how far the centre of an item on the ground is from a point, in pixels. */
  private static float distanceTo(ItemDrop drop, Vector2 point) {
    Aabb box = drop.getBodyBox();
    return point.dst(box.x() + box.width() / 2f, box.y() + box.height() / 2f);
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
