package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EntityTest {
  private static final Aabb BOX = new Aabb(0, 0, 10, 10);

  @Test
  void should_keep_its_body_box_when_created() {
    CountingEntity entity = new CountingEntity(BOX);

    Aabb box = entity.bodyBox();

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

  /**Hand-written fake (Q117): counts the call of its hook.*/
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
