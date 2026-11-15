package com.epitech.depthdiablo.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * Carries game events from emitters to listeners, so that neither holds a reference to the other
 * (GDD 1.1.2).
 *
 * <p>Pattern: Observer (subject). {@link #publish(GameEvent)} only queues an event; {@link
 * #flush()} delivers the queue, once per tick. There is no singleton: the bus is passed in like any
 * other dependency (Q103).
 */
public final class EventBus {
  private final Map<Class<? extends GameEvent>, List<EventListener>> listeners = new HashMap<>();
  private final List<EventListener> everyEvent = new ArrayList<>();
  private final Queue<GameEvent> queue = new ArrayDeque<>();

  /**
   * Registers a listener for one type of event.
   *
   * @param type the type of event to receive
   * @param listener the listener to call
   * @throws NullPointerException if the type or the listener is null
   */
  public void subscribe(Class<? extends GameEvent> type, EventListener listener) {
    Objects.requireNonNull(type, "The event type to listen to must not be null");
    Objects.requireNonNull(listener, "The listener to subscribe must not be null");
    listeners.computeIfAbsent(type, key -> new ArrayList<>()).add(listener);
  }

  /**
   * Registers a listener for every event, such as the event laser (F2).
   *
   * @param listener the listener to call
   * @throws NullPointerException if the listener is null
   */
  public void subscribeAll(EventListener listener) {
    everyEvent.add(Objects.requireNonNull(listener, "The listener to subscribe must not be null"));
  }

  /**
   * Removes a listener from every subscription it holds. Does nothing if it has none.
   *
   * @param listener the listener to remove
   * @throws NullPointerException if the listener is null
   */
  public void unsubscribe(EventListener listener) {
    Objects.requireNonNull(listener, "The listener to unsubscribe must not be null");
    listeners.values().forEach(subscribers -> subscribers.removeIf(listener::equals));
    everyEvent.removeIf(listener::equals);
  }

  /**
   * Queues an event for the next {@link #flush()}.
   *
   * @param event the event to queue
   * @throws NullPointerException if the event is null
   */
  public void publish(GameEvent event) {
    queue.add(Objects.requireNonNull(event, "The event to publish must not be null"));
  }

  /**
   * Delivers the queued events in publishing order: each one reaches the listeners of its type,
   * then the listeners of every event. An event published during the flush waits for the next one,
   * so a listener cannot start a loop within a tick.
   */
  public void flush() {
    List<GameEvent> events = new ArrayList<>(queue);
    queue.clear();
    for (GameEvent event : events) {
      deliver(event, listeners.getOrDefault(event.getClass(), List.of()));
      deliver(event, everyEvent);
    }
  }

  /** Drops the queued events and keeps the listeners. Rewind uses it to undo a tick (Q86). */
  public void clear() {
    queue.clear();
  }

  private static void deliver(GameEvent event, List<EventListener> subscribers) {
    // A copy, because a listener may unsubscribe while it handles the event.
    for (EventListener subscriber : List.copyOf(subscribers)) {
      subscriber.onEvent(event);
    }
  }
}
