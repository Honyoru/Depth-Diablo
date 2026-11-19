package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Holds everything alive during one run and advances it, one logic tick at a time. Owns the {@link
 * EventBus} of the run (GDD 1.1.2).
 */
public final class World implements Rewindable {

  private final EventBus eventBus = new EventBus();

  private final List<Entity> entities = new ArrayList<>();

  private final List<Entity> entitiesToAdd = new ArrayList<>();

  private long tick;

  private boolean ticking;

  /**
   * Returns the bus to pass to emitters and listeners.
   *
   * @return the event bus of this run
   */
  public EventBus getEventBus() {
    return eventBus;
  }

  /**
   * Returns the entities currently in the world, in the order they joined.
   *
   * @return a read-only view of the entities
   */
  public List<Entity> getEntities() {
    return Collections.unmodifiableList(entities);
  }

  /**
   * Returns the number of the last tick that ran.
   *
   * @return the tick number, 0 before the first tick
   */
  public long getCurrentTick() {
    return tick;
  }

  /**
   * Adds an entity to the world. During a tick, the entity joins at the end of that tick, so it is
   * first updated on the next one.
   *
   * @param entity the entity to add
   * @throws NullPointerException if the entity is null
   */
  public void add(Entity entity) {
    Objects.requireNonNull(entity, "The entity to add to the world must not be null");
    if (ticking) {
      entitiesToAdd.add(entity);
    } else {
      entities.add(entity);
    }
  }

  /**
   * Advances the world by one tick: updates every entity, tells every pair of overlapping entities
   * that they touch, delivers the queued events, then adds the waiting entities and drops the
   * removed ones.
   */
  public void tick() {
    ticking = true;
    tick++;
    for (Entity entity : entities) {
      entity.update(tick);
    }
    resolveContacts();
    eventBus.flush();
    entities.addAll(entitiesToAdd);
    entitiesToAdd.clear();
    entities.removeIf(Entity::isRemoved);
    ticking = false;
  }

  /**
   * Tells wether a mover can stand in the given box: no other blocking entity overlaps it. A mover
   * cheks the box before moving, so walls, closed doors and chest stop it (GDD 1.5).
   *
   * @param box the box the mover whant to occupy
   * @param mover the entity that wants to move there; it never blocks itself
   * @return thrue if no blocking entity other than the mover overlaps the box
   * @throws NullPointerException if box or mover is null
   */
  public boolean isFree(Aabb box, Entity mover) {
    Objects.requireNonNull(box, "The box to check for walls must not be null");
    Objects.requireNonNull(mover, "The entity trying to move must not be null");
    for (Entity other : entities) {
      if (other.equals(mover) || other.isRemoved() || !other.blocks()) {
        continue;
      }
      if (other.getBodyBox().overlaps(box)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Calls {@link Entity#onContact(Entity)} on both entities of every overlapping pair. An entity
   * removed earlier in the tick touches nothing.
   */
  private void resolveContacts() {
    for (int i = 0; i < entities.size(); i++) {
      Entity first = entities.get(i);
      for (int j = i + 1; j < entities.size(); j++) {
        Entity second = entities.get(j);
        if (touch(first, second)) {
          first.onContact(second);
          second.onContact(first);
        }
      }
    }
  }

  private static boolean touch(Entity first, Entity second) {
    return !first.isRemoved()
        && !second.isRemoved()
        && first.getBodyBox().overlaps(second.getBodyBox());
  }

  /**
   * Takes the photo of the whole world for R (GDD 1.1.2): the tick number, then for every entity
   * the entity itself and its own state. Keeping the entities themselves is what lets a rewind
   * bring back a dead one and drop one spawned since.
   *
   * @param out the photo being taken
   * @throws IllegalStateException if called during a tick, when the world is half updated
   */
  @Override
  public void writeState(StateBuffer out) {
    if (ticking) {
      throw new IllegalStateException(
          "The world cannot be saved during a tick: take the photo between two ticks");
    }
    out.writeLong(tick);
    out.writeInt(entities.size());
    for (Entity entity : entities) {
      out.writeRef(entity);
      entity.writeState(out);
    }
  }

  /**
   * Puts the world back as it was in the photo: the tick number, the list of entities rebuilt from
   * the photo, each with its state. Events still queued belong to the undone future, so they are
   * dropped.
   *
   * @param in the photo being restored
   * @throws IllegalStateException if called during a tick
   */
  @Override
  public void readState(StateBuffer in) {
    if (ticking) {
      throw new IllegalStateException(
          "The world cannot be restored during a tick: rewind between two ticks");
    }
    tick = in.readLong();
    int count = in.readInt();
    entities.clear();
    for (int i = 0; i < count; i++) {
      Entity entity = in.readRef(Entity.class);
      entity.readState(in);
      entities.add(entity);
    }
    eventBus.clear();
  }
}
