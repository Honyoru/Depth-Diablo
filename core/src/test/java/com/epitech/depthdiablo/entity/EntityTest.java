package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.engine.StateBuffer;
import org.junit.jupiter.api.Test;

class EntityTest {
  private static final Aabb BOX = new Aabb(0, 0, 10, 10);

  @Test
  void should_get_back_its_position_and_presence_when_its_state_is_read_back() {
    CountingEntity entity = new CountingEntity(BOX);
    StateBuffer photo = new StateBuffer();
    entity.writeState(photo);
    entity.moveTo(new Vector2(40, 50));
    entity.remove();

    photo.startReading();
    entity.readState(photo);

    assertEquals(BOX, entity.getBodyBox());
    assertFalse(entity.isRemoved());
  }

  @Test
  void should_keep_its_body_box_when_created() {
    CountingEntity entity = new CountingEntity(BOX);

    Aabb box = entity.getBodyBox();

    assertEquals(BOX, box);
  }

  @Test
  void should_reject_a_missing_body_box() {
    Aabb box = null;

    assertThrows(NullPointerException.class, () -> new CountingEntity(box));
  }

  @Test
  void should_call_the_hook_once_when_updated() {
    CountingEntity entity = new CountingEntity(BOX);

    entity.update(1);

    assertEquals(1, entity.updates);
  }

  @Test
  void should_pass_the_tick_to_the_hook_when_updated() {
    CountingEntity entity = new CountingEntity(BOX);

    entity.update(40);

    assertEquals(40, entity.lastTick);
  }

  @Test
  void should_not_be_removed_when_created() {
    CountingEntity entity = new CountingEntity(BOX);

    boolean removed = entity.isRemoved();

    assertFalse(removed);
  }

  @Test
  void should_be_removed_when_remove_is_called() {
    CountingEntity entity = new CountingEntity(BOX);

    entity.remove();

    assertTrue(entity.isRemoved());
  }

  @Test
  void should_skip_the_hook_when_the_entity_is_removed() {
    CountingEntity entity = new CountingEntity(BOX);
    entity.remove();

    entity.update(1);

    assertEquals(0, entity.updates);
  }

  @Test
  void should_not_block_movement_when_created() {
    CountingEntity entity = new CountingEntity(BOX);

    boolean blocks = entity.blocks();

    assertFalse(blocks);
  }

  @Test
  void should_change_nothing_when_touched_by_default() {
    CountingEntity entity = new CountingEntity(BOX);
    CountingEntity other = new CountingEntity(BOX);

    entity.onContact(other);

    assertEquals(BOX, entity.getBodyBox());
    assertFalse(entity.isRemoved());
  }

  @Test
  void should_reject_a_missing_entity_when_touched() {
    CountingEntity entity = new CountingEntity(BOX);
    CountingEntity other = null;

    assertThrows(NullPointerException.class, () -> entity.onContact(other));
  }

  @Test
  void should_report_the_bottom_left_corner_as_its_position() {
    CountingEntity entity = new CountingEntity(new Aabb(3, 4, 10, 10));

    Vector2 position = entity.position();

    assertEquals(new Vector2(3, 4), position);
  }

  @Test
  void should_keep_this_size_when_moved() {
    CountingEntity entity = new CountingEntity(BOX);

    entity.moveTo(new Vector2(20, 30));

    assertEquals(new Aabb(20, 30, 10, 10), entity.getBodyBox());
  }

  /** Hand-written fake (Q117): counts the calls to its hook. */
  private static final class CountingEntity extends Entity {
    private int updates;
    private long lastTick;

    CountingEntity(Aabb bodyBox) {
      super(bodyBox);
    }

    @Override
    protected void onUpdate(long tick) {
      updates++;
      lastTick = tick;
    }
  }
}
