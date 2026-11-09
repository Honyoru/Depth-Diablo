package com.epitech.depthdiablo.engine;

/**
 * Receives game events from an {@link EventBus}.
 *
 * <p>Pattern: Observer (observer).
 */
public interface EventListener {
  /**
   * Handles one delivered event.
   *
   * @param event the event
   */
  void onEvent(GameEvent event);
}
