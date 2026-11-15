package com.epitech.depthdiablo.debug;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Character;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.1.2, F2: one line per event, from its emitter to its receiver. Just one more listener. */
class EventLaserTest {
  private final EventLaser laser = new EventLaser();
  private final Character attacker = aCharacter().withBodyBox(new Aabb(0, 0, 16, 16)).build();
  private final Character victim = aCharacter().withBodyBox(new Aabb(100, 50, 20, 20)).build();

  @Test
  void should_start_disabled_with_no_line() {
    assertFalse(laser.isEnabled());
    assertTrue(laser.lines().isEmpty());
  }

  @Test
  void should_ignore_events_while_disabled() {
    laser.onEvent(aHit());

    assertTrue(laser.lines().isEmpty());
  }

  @Test
  void should_draw_a_line_from_the_source_centre_to_the_receiver_centre() {
    laser.enable();
    GameEvent hit = aHit();

    laser.onEvent(hit);

    EventLaser.Line line = laser.lines().get(0);
    assertSame(hit, line.event());
    assertEquals(8f, line.fromX());
    assertEquals(8f, line.fromY());
    assertEquals(110f, line.toX());
    assertEquals(60f, line.toY());
    assertTrue(line.hasReceiver());
  }

  @Test
  void should_mark_only_the_source_when_the_event_has_no_receiver() {
    laser.enable();

    laser.onEvent(new EntityDied(victim, Optional.empty()));

    EventLaser.Line line = laser.lines().get(0);
    assertFalse(line.hasReceiver());
    assertEquals(line.fromX(), line.toX());
    assertEquals(line.fromY(), line.toY());
  }

  @Test
  void should_keep_a_line_until_its_last_tick() {
    laser.enable();
    laser.onEvent(aHit());

    tick(EventLaser.LINE_LIFETIME_TICKS - 1);

    assertEquals(1, laser.lines().size());
  }

  @Test
  void should_drop_a_line_once_its_lifetime_is_over() {
    laser.enable();
    laser.onEvent(aHit());

    tick(EventLaser.LINE_LIFETIME_TICKS);

    assertTrue(laser.lines().isEmpty());
  }

  @Test
  void should_count_down_the_ticks_left_of_a_line() {
    laser.enable();
    laser.onEvent(aHit());

    tick(1);

    assertEquals(EventLaser.LINE_LIFETIME_TICKS - 1, laser.lines().get(0).ticksLeft());
  }

  @Test
  void should_clear_its_lines_when_disabled() {
    laser.enable();
    laser.onEvent(aHit());

    laser.disable();

    assertTrue(laser.lines().isEmpty());
  }

  @Test
  void should_refuse_changes_through_the_line_list() {
    List<EventLaser.Line> lines = laser.lines();

    assertThrows(UnsupportedOperationException.class, lines::clear);
  }

  @Test
  void should_have_a_name_for_logs() {
    assertEquals("Event laser", laser.name());
  }

  private GameEvent aHit() {
    return new DamageDealt(attacker, victim, 3);
  }

  private void tick(int ticks) {
    for (int i = 1; i <= ticks; i++) {
      laser.onTick(i);
    }
  }
}
