package com.epitech.depthdiablo.engine.event;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.1.2: a listener receives the events the bus delivers to it, and only those. */
class EventListenerTest {
  private final EventBus bus = new EventBus();
  private final RecordingListener listener = new RecordingListener();

  @Test
  void should_receive_the_event_when_the_bus_flushes() {
    GameEvent event = aDeathEvent();
    bus.subscribe(EntityDied.class, listener);
    bus.publish(event);

    bus.flush();

    assertEquals(List.of(event), listener.events);
  }

  @Test
  void should_receive_nothing_when_the_bus_has_not_flushed() {
    bus.subscribe(EntityDied.class, listener);

    bus.publish(aDeathEvent());

    assertTrue(listener.events.isEmpty());
  }

  @Test
  void should_receive_nothing_when_it_subscribed_to_another_type() {
    bus.subscribe(DamageDealt.class, listener);
    bus.publish(aDeathEvent());

    bus.flush();

    assertTrue(listener.events.isEmpty());
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
}
