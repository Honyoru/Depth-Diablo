package com.epitech.depthdiablo.engine.event;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.1.2: the bus queues events and delivers them to listeners once per flush. */
class EventBusTest {
  private final EventBus bus = new EventBus();

  @Test
  void should_not_deliver_when_an_event_is_only_published() {
    RecordingListener listener = new RecordingListener();
    bus.subscribeAll(listener);

    bus.publish(aDamageEvent());

    assertTrue(listener.events.isEmpty());
  }

  @Test
  void should_deliver_once_when_flushed_twice() {
    RecordingListener listener = new RecordingListener();
    bus.subscribeAll(listener);
    bus.publish(aDamageEvent());

    bus.flush();
    bus.flush();

    assertEquals(1, listener.events.size());
  }

  @Test
  void should_deliver_only_the_subscribed_type_when_a_listener_picks_one() {
    RecordingListener listener = new RecordingListener();
    bus.subscribe(EntityDied.class, listener);
    GameEvent death = aDeathEvent();
    bus.publish(aDamageEvent());
    bus.publish(death);

    bus.flush();

    assertEquals(List.of(death), listener.events);
  }

  @Test
  void should_deliver_in_publishing_order_when_several_events_are_queued() {
    RecordingListener listener = new RecordingListener();
    bus.subscribeAll(listener);
    GameEvent first = aDamageEvent();
    GameEvent second = aDeathEvent();
    bus.publish(first);
    bus.publish(second);

    bus.flush();

    assertEquals(List.of(first, second), listener.events);
  }

  @Test
  void should_call_type_listeners_first_when_an_event_has_both_kinds() {
    List<String> calls = new ArrayList<>();
    bus.subscribeAll(event -> calls.add("every event"));
    bus.subscribe(DamageDealt.class, event -> calls.add("type"));
    bus.publish(aDamageEvent());

    bus.flush();

    assertEquals(List.of("type", "every event"), calls);
  }

  @Test
  void should_wait_for_the_next_flush_when_a_listener_publishes_during_a_flush() {
    RecordingListener listener = new RecordingListener();
    GameEvent reply = aDeathEvent();
    bus.subscribe(DamageDealt.class, event -> bus.publish(reply));
    bus.subscribe(EntityDied.class, listener);
    bus.publish(aDamageEvent());

    bus.flush();
    int afterFirstFlush = listener.events.size();
    bus.flush();

    assertEquals(0, afterFirstFlush);
    assertEquals(List.of(reply), listener.events);
  }

  @Test
  void should_stop_delivering_when_a_listener_unsubscribes() {
    RecordingListener listener = new RecordingListener();
    bus.subscribe(DamageDealt.class, listener);
    bus.subscribeAll(listener);
    bus.unsubscribe(listener);
    bus.publish(aDamageEvent());

    bus.flush();

    assertTrue(listener.events.isEmpty());
  }

  @Test
  void should_still_reach_the_others_when_a_listener_unsubscribes_during_a_flush() {
    RecordingListener other = new RecordingListener();
    bus.subscribe(DamageDealt.class, new SelfRemovingListener(bus));
    bus.subscribe(DamageDealt.class, other);
    bus.publish(aDamageEvent());

    bus.flush();

    assertEquals(1, other.events.size());
  }

  @Test
  void should_drop_the_queue_and_keep_the_listeners_when_cleared() {
    RecordingListener listener = new RecordingListener();
    GameEvent kept = aDeathEvent();
    bus.subscribeAll(listener);
    bus.publish(aDamageEvent());

    bus.clear();
    bus.flush();
    bus.publish(kept);
    bus.flush();

    assertEquals(List.of(kept), listener.events);
  }

  @Test
  void should_reject_a_missing_event() {
    assertThrows(NullPointerException.class, () -> bus.publish(null));
  }

  @Test
  void should_reject_a_missing_listener() {
    assertThrows(NullPointerException.class, () -> bus.subscribeAll(null));
  }

  @Test
  void should_reject_a_missing_event_type() {
    RecordingListener listener = new RecordingListener();

    assertThrows(NullPointerException.class, () -> bus.subscribe(null, listener));
  }

  private static GameEvent aDamageEvent() {
    return new DamageDealt(aCharacter().build(), aCharacter().build(), 1);
  }

  private static GameEvent aDeathEvent() {
    return new EntityDied(aCharacter().build(), Optional.empty());
  }

  /** Hand-written spy (Q117): keeps the events it received. */
  private static final class RecordingListener implements EventListener {
    private final List<GameEvent> events = new ArrayList<>();

    @Override
    public void onEvent(GameEvent event) {
      events.add(event);
    }
  }

  /** Hand-written fake (Q117): leaves the bus the first time it is called. */
  private static final class SelfRemovingListener implements EventListener {
    private final EventBus bus;

    SelfRemovingListener(EventBus bus) {
      this.bus = bus;
    }

    @Override
    public void onEvent(GameEvent event) {
      bus.unsubscribe(this);
    }
  }
}
