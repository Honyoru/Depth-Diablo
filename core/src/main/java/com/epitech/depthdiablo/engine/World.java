package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Holds everything alive during one run and advances it, one logic tick at a time. Owns the {@link
 * EventBus} of the run (GDD 1.1.2).
 */
public final class World {
  private final EventBus eventBus = new EventBus();
  private final List<Entity> entities = new ArrayList<>();
  private final List<Entity> entitiesToAdd = new ArrayList<>();
  private long tick;
  private boolean ticking;

  /**
   * Adds an entity to the world. During a tick, the entity joins at the end of that tick, so it is
   * first updated on the next one.
   *
   * @param entity the entity to add
   * @throws NullPointerException if the entity is null
   */
  public void add(Entity entity) {
    Objects.requireNonNull(entity, "entity");
    if (ticking) {
      entitiesToAdd.add(entity);
    } else {
      entities.add(entity);
    }
  }

  /**
   * Advances the world by one tick: updates every entity, delivers the queued events, then adds the
   * waiting entities and drops the removed ones.
   */
  public void tick() {
    ticking = true;
    tick++;
    for (Entity entity : entities) {
      entity.update(tick);
    }
    eventBus.flush();
    entities.addAll(entitiesToAdd);
    entitiesToAdd.clear();
    entities.removeIf(Entity::isRemoved);
    ticking = false;
  }

  /**
   * Returns the bus to pass to emitters and listeners.
   *
   * @return the event bus of this run
   */
  public EventBus eventBus() {
    return eventBus;
  }

  /**
   * Returns the entities currently in the world, in the order they joined.
   *
   * @return a read-only view of the entities
   */
  public List<Entity> entities() {
    return Collections.unmodifiableList(entities);
  }

  /**
   * Returns the number of the last tick that ran.
   *
   * @return the tick number, 0 before the first tick
   */
  public long currentTick() {
    return tick;
  }
}
