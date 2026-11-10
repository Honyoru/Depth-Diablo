package com.epitech.depthdiablo.engine;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The tick order of a run: update every entity, deliver events, then add and drop entities. */
class WorldTest {
  private final World world = new World();

  @Test
  void should_start_at_tick_0_when_created() {
    long tick = world.currentTick();

    assertEquals(0, tick);
  }

  @Test
  void should_count_the_ticks_when_ticked() {
    world.tick();
    world.tick();

    assertEquals(2, world.currentTick());
  }

  @Test
  void should_update_every_entity_with_the_tick_number_when_ticked() {
    FakeEntity first = new FakeEntity();
    FakeEntity second = new FakeEntity();
    world.add(first);
    world.add(second);

    world.tick();

    assertEquals(List.of(1L), first.ticks);
    assertEquals(List.of(1L), second.ticks);
  }

  @Test
  void should_hold_an_entity_at_once_when_added_outside_a_tick() {
    FakeEntity entity = new FakeEntity();

    world.add(entity);

    assertEquals(List.of(entity), world.entities());
  }

  @Test
  void should_deliver_events_after_every_update_when_ticked() {
    FakeEntity emitter = new FakeEntity();
    FakeEntity last = new FakeEntity();
    List<Integer> updatesSeen = new ArrayList<>();
    emitter.onTick = () -> world.eventBus().publish(aDeathEvent());
    world.eventBus().subscribeAll(event -> updatesSeen.add(last.ticks.size()));
    world.add(emitter);
    world.add(last);

    world.tick();

    assertEquals(List.of(1), updatesSeen);
  }

  @Test
  void should_add_a_spawned_entity_at_the_end_of_the_tick_when_added_during_one() {
    FakeEntity spawner = new FakeEntity();
    FakeEntity spawned = new FakeEntity();
    spawner.onTick = () -> world.add(spawned);
    world.add(spawner);

    world.tick();

    assertTrue(spawned.ticks.isEmpty());
    assertEquals(List.of(spawner, spawned), world.entities());
  }

  @Test
  void should_add_a_spawned_entity_at_the_end_of_the_tick_when_a_listener_adds_it() {
    FakeEntity emitter = new FakeEntity();
    FakeEntity spawned = new FakeEntity();
    emitter.onTick = () -> world.eventBus().publish(aDeathEvent());
    world.eventBus().subscribeAll(event -> world.add(spawned));
    world.add(emitter);

    world.tick();

    assertTrue(spawned.ticks.isEmpty());
    assertEquals(List.of(emitter, spawned), world.entities());
  }

  @Test
  void should_drop_a_removed_entity_at_the_end_of_the_tick() {
    FakeEntity entity = new FakeEntity();
    world.add(entity);
    entity.remove();

    world.tick();

    assertTrue(world.entities().isEmpty());
  }

  @Test
  void should_never_hold_an_entity_when_it_is_added_and_removed_in_the_same_tick() {
    FakeEntity spawner = new FakeEntity();
    FakeEntity ghost = new FakeEntity();
    spawner.onTick =
        () -> {
          world.add(ghost);
          ghost.remove();
        };
    world.add(spawner);

    world.tick();

    assertEquals(List.of(spawner), world.entities());
  }

  @Test
  void should_add_at_once_again_when_the_tick_is_over() {
    FakeEntity entity = new FakeEntity();
    world.tick();

    world.add(entity);

    assertEquals(List.of(entity), world.entities());
  }

  @Test
  void should_refuse_changes_through_the_entity_list() {
    List<Entity> entities = world.entities();

    assertThrows(UnsupportedOperationException.class, entities::clear);
  }

  @Test
  void should_reject_a_missing_entity() {
    assertThrows(NullPointerException.class, () -> world.add(null));
  }

  @Test
  void should_find_a_box_free_when_no_blocking_entity_overlaps_it() {
    FakeEntity mover = new FakeEntity();
    world.add(mover);
    world.add(aWallAt(100, 0));

    boolean free = world.isFree(new Aabb(10, 0, 16, 16), mover);

    assertTrue(free);
  }

  @Test
  void should_find_a_box_taken_when_a_wall_overlaps_it() {
    FakeEntity mover = new FakeEntity();
    world.add(mover);
    world.add(aWallAt(20, 0));

    boolean free = world.isFree(new Aabb(10, 0, 16, 16), mover);

    assertFalse(free);
  }

  @Test
  void should_never_block_a_mover_with_itself() {
    FakeEntity mover = aWallAt(0, 0);
    world.add(mover);

    boolean free = world.isFree(new Aabb(4, 0, 16, 16), mover);

    assertTrue(free);
  }

  @Test
  void should_let_a_mover_through_an_entity_that_does_not_block() {
    FakeEntity mover = new FakeEntity();
    world.add(mover);
    world.add(anEntityAt(20, 0));

    boolean free = world.isFree(new Aabb(10, 0, 16, 16), mover);

    assertTrue(free);
  }

  @Test
  void should_tell_both_entities_when_their_bodies_overlap() {
    FakeEntity first = anEntityAt(0, 0);
    FakeEntity second = anEntityAt(8, 0);
    world.add(first);
    world.add(second);

    world.tick();

    assertEquals(List.of(second), first.contacts);
    assertEquals(List.of(first), second.contacts);
  }

  @Test
  void should_tell_no_one_when_bodies_only_touch_on_an_edge() {
    FakeEntity first = anEntityAt(0, 0);
    FakeEntity second = anEntityAt(16, 0);
    world.add(first);
    world.add(second);

    world.tick();

    assertTrue(first.contacts.isEmpty());
    assertTrue(second.contacts.isEmpty());
  }

  @Test
  void should_skip_the_contact_when_an_entity_was_removed_during_the_tick() {
    FakeEntity first = anEntityAt(0, 0);
    FakeEntity second = anEntityAt(8, 0);
    second.onTick = second::remove;
    world.add(first);
    world.add(second);

    world.tick();

    assertTrue(first.contacts.isEmpty());
  }

  @Test
  void should_reject_a_missing_box_when_checking_if_it_is_free() {
    FakeEntity mover = new FakeEntity();

    assertThrows(NullPointerException.class, () -> world.isFree(null, mover));
  }

  private static GameEvent aDeathEvent() {
    return new EntityDied(aCharacter().build(), Optional.empty());
  }

  private static FakeEntity aWallAt(float x, float y) {
    return new FakeEntity(new Aabb(x, y, 16, 16), true);
  }

  private static FakeEntity anEntityAt(float x, float y) {
    return new FakeEntity(new Aabb(x, y, 16, 16), false);
  }

  /**
   * Character don't exist yet. Create a fake entity to test (Q117) > records its tick and contact,
   * and blocks only if asked to.
   */
  private static final class FakeEntity extends Entity {
    private final List<Long> ticks = new ArrayList<>();
    private final List<Entity> contacts = new ArrayList<>();
    private final boolean blocking;
    private Runnable onTick = () -> {};

    FakeEntity() {
      this(new Aabb(0, 0, 16, 16), false);
    }

    FakeEntity(Aabb bodyBox, boolean blocking) {
      super(bodyBox);
      this.blocking = blocking;
    }

    @Override
    public boolean blocks() {
      return blocking;
    }

    @Override
    public void onContact(Entity other) {
      contacts.add(other);
    }

    @Override
    protected void onUpdate(long tick) {
      ticks.add(tick);
      onTick.run();
    }
  }
}
