package com.epitech.depthdiablo.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** R (GDD 1.1.2, Q86): one photo, values read back in the order they were written. */
class StateBufferTest {
  private final StateBuffer buffer = new StateBuffer();

  @Test
  void should_give_back_every_value_in_the_order_written() {
    Object entity = new Object();
    buffer.writeInt(7);
    buffer.writeFloat(1.5f);
    buffer.writeBoolean(true);
    buffer.writeLong(4_000_000_000L);
    buffer.writeRef(entity);
    buffer.writeBoolean(false);

    buffer.startReading();

    assertEquals(7, buffer.readInt());
    assertEquals(1.5f, buffer.readFloat());
    assertTrue(buffer.readBoolean());
    assertEquals(4_000_000_000L, buffer.readLong());
    assertSame(entity, buffer.readRef(Object.class));
    assertFalse(buffer.readBoolean());
  }

  @Test
  void should_give_back_a_null_reference() {
    buffer.writeRef(null);

    buffer.startReading();

    assertNull(buffer.readRef(Object.class));
  }

  @Test
  void should_keep_every_value_when_it_grows_past_its_first_size() {
    for (int i = 0; i < 1000; i++) {
      buffer.writeInt(i);
      buffer.writeFloat(i);
      buffer.writeLong(i);
      buffer.writeRef(i);
    }

    buffer.startReading();

    for (int i = 0; i < 1000; i++) {
      assertEquals(i, buffer.readInt());
      assertEquals(i, buffer.readFloat());
      assertEquals(i, buffer.readLong());
      assertEquals(i, buffer.readRef(Integer.class));
    }
  }

  @Test
  void should_give_only_the_new_values_when_cleared_and_written_again() {
    buffer.writeInt(1);
    buffer.writeRef("old");

    buffer.clear();
    buffer.writeInt(2);
    buffer.startReading();

    assertEquals(2, buffer.readInt());
    assertThrows(IllegalStateException.class, buffer::readInt);
    assertThrows(IllegalStateException.class, () -> buffer.readRef(String.class));
  }

  @Test
  void should_give_the_same_values_when_read_twice() {
    buffer.writeInt(3);
    buffer.startReading();
    buffer.readInt();

    buffer.startReading();

    assertEquals(3, buffer.readInt());
  }

  @Test
  void should_refuse_to_read_more_than_was_written() {
    buffer.writeInt(1);
    buffer.startReading();
    buffer.readInt();

    assertThrows(IllegalStateException.class, buffer::readInt);
    assertThrows(IllegalStateException.class, buffer::readFloat);
    assertThrows(IllegalStateException.class, buffer::readLong);
    assertThrows(IllegalStateException.class, () -> buffer.readRef(Object.class));
  }

  @Test
  void should_refuse_a_reference_of_another_type() {
    buffer.writeRef("text");
    buffer.startReading();

    assertThrows(ClassCastException.class, () -> buffer.readRef(Integer.class));
  }

  @Test
  void should_refuse_a_missing_type() {
    buffer.writeRef("text");
    buffer.startReading();

    assertThrows(NullPointerException.class, () -> buffer.readRef(null));
  }
}
