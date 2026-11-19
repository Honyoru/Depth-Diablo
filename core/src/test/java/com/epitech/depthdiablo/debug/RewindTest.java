package com.epitech.depthdiablo.debug;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.engine.Rewindable;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Character;
import java.util.List;
import org.junit.jupiter.api.Test;

/** R (GDD 1.1.2, Q86): hold R to play the last seconds backwards, one photo per tick. */
class RewindTest {

  /** Hand-written fake (Q117): its whole state is one number. */
  private static final class Counter implements Rewindable {
    private int value;

    @Override
    public void writeState(StateBuffer out) {
      out.writeInt(value);
    }

    @Override
    public void readState(StateBuffer in) {
      value = in.readInt();
    }
  }

  private final Counter counter = new Counter();

  @Test
  void should_be_named_and_enabled_when_created() {
    Rewind rewind = new Rewind(counter, 3);

    assertEquals("Rewind", rewind.getName());
    assertTrue(rewind.isEnabled());
  }

  @Test
  void should_keep_5_seconds_of_photos_by_default() {
    assertEquals(300, Rewind.CAPACITY);
  }

  @Test
  void should_put_back_the_previous_photo_when_stepping_back() {
    Rewind rewind = new Rewind(counter, 10);
    record(rewind, 1, 2, 3);

    assertTrue(rewind.stepBack());

    assertEquals(2, counter.value);
  }

  @Test
  void should_go_back_one_tick_per_step() {
    Rewind rewind = new Rewind(counter, 20);
    record(rewind, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    rewind.stepBack();
    rewind.stepBack();
    rewind.stepBack();

    assertEquals(7, counter.value);
  }

  @Test
  void should_not_step_back_without_an_older_photo() {
    Rewind rewind = new Rewind(counter, 10);
    record(rewind, 1);

    assertFalse(rewind.stepBack());

    assertEquals(1, counter.value);
  }

  @Test
  void should_overwrite_the_oldest_photos_when_full() {
    Rewind rewind = new Rewind(counter, 3);
    record(rewind, 1, 2, 3, 4, 5);

    rewind.stepBack();
    rewind.stepBack();

    assertEquals(3, counter.value);
    assertFalse(rewind.stepBack());
  }

  @Test
  void should_count_the_photos_up_to_its_capacity() {
    Rewind rewind = new Rewind(counter, 3);

    record(rewind, 1, 2);
    assertEquals(2, rewind.getFrameCount());
    record(rewind, 3, 4);
    assertEquals(3, rewind.getFrameCount());
    rewind.stepBack();
    assertEquals(2, rewind.getFrameCount());
  }

  @Test
  void should_record_on_from_the_restored_photo_after_a_rewind() {
    Rewind rewind = new Rewind(counter, 10);
    record(rewind, 1, 2, 3);
    rewind.stepBack();

    record(rewind, 20);
    rewind.stepBack();

    assertEquals(2, counter.value);
  }

  @Test
  void should_forget_every_photo_when_disabled() {
    Rewind rewind = new Rewind(counter, 10);
    record(rewind, 1, 2, 3);

    rewind.disable();
    rewind.enable();

    assertEquals(0, rewind.getFrameCount());
    assertFalse(rewind.stepBack());
  }

  @Test
  void should_not_step_back_while_disabled() {
    Rewind rewind = new Rewind(counter, 10);
    record(rewind, 1, 2, 3);

    rewind.disable();

    assertFalse(rewind.stepBack());
    assertEquals(3, counter.value);
  }

  @Test
  void should_bring_a_dead_character_back_to_the_world_when_rewound_past_its_death() {
    World world = new World();
    Character rat = aCharacter().withHp(10).withWorld(world).build();
    world.add(rat);
    Rewind rewind = new Rewind(world, 10);
    world.tick();
    rewind.onTick(world.getCurrentTick());
    rat.takeDamage(10);
    rat.remove();
    world.tick();
    rewind.onTick(world.getCurrentTick());

    rewind.stepBack();

    assertEquals(List.of(rat), world.getEntities());
    assertEquals(10, rat.getHp());
    assertFalse(rat.isRemoved());
  }

  @Test
  void should_reject_a_missing_world() {
    assertThrows(NullPointerException.class, () -> new Rewind(null, 10));
  }

  @Test
  void should_reject_room_for_fewer_than_2_photos() {
    assertThrows(IllegalArgumentException.class, () -> new Rewind(counter, 1));
  }

  /** Sets the counter to each value and takes a photo after each, like one tick each. */
  private void record(Rewind rewind, int... values) {
    for (int value : values) {
      counter.value = value;
      rewind.onTick(value);
    }
  }
}
