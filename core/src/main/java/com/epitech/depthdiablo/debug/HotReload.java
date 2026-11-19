package com.epitech.depthdiablo.debug;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SerializationException;
import com.epitech.depthdiablo.data.Reloadable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The F5 debug feature: reads the data files again while the game runs (GDD 1.1.2). F5 only asks
 * for a reload; it runs at the start of the next tick, so no tick ever sees half old and half new
 * values. A file that fails is logged and kept as the last error, the other files are still read,
 * and the game keeps running on the old values of that file.
 *
 * <p>Pattern: part of the data-driven engine; each {@link Reloadable} applies its own data.
 */
public final class HotReload implements DebugFeature {

  private static final Logger LOG = LoggerFactory.getLogger(HotReload.class);

  private final List<Reloadable> targets;
  private boolean enabled = true;
  private boolean requested;
  private Optional<String> lastError = Optional.empty();

  /**
   * Creates the feature, enabled, for the given data.
   *
   * @param targets what F5 reads again, in this order, e.g. the item registry and the character
   *     factory
   * @throws NullPointerException if {@code targets} or one of them is null
   */
  public HotReload(List<Reloadable> targets) {
    this.targets =
        List.copyOf(Objects.requireNonNull(targets, "target of a reload should not be null"));
  }

  @Override
  public String getName() {
    return "Hot reload";
  }

  @Override
  public void enable() {
    enabled = true;
  }

  /** Switches F5 off and forgets a reload asked for but not run yet. */
  @Override
  public void disable() {
    enabled = false;
    requested = false;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Asks for a reload at the next tick; F5 calls it. Asking again before that tick still gives one
   * reload. Ignored while the feature is disabled.
   */
  public void request() {
    if (enabled) {
      requested = true;
    }
  }

  /**
   * Returns why the last reload failed.
   *
   * @return the message of the last file that failed, or empty if the last reload worked or none
   *     ran yet
   */
  public Optional<String> getLastError() {
    return lastError;
  }

  /**
   * Runs the reload asked for since the last tick, if any: every target is read again, in order.
   *
   * @param tick the number of the current logic tick
   */
  @Override
  public void onTick(long tick) {
    if (!requested) {
      return;
    }
    requested = false;
    lastError = Optional.empty();
    for (Reloadable target : targets) {
      try {
        target.reload();
      } catch (IllegalArgumentException | SerializationException | GdxRuntimeException e) {
        // a bad value or id, broken JSON, or a file that cannot be read: keep the old values
        lastError = Optional.ofNullable(e.getMessage());
        LOG.warn("Hot reload failed: {}", e.getMessage());
      }
    }
  }
}
