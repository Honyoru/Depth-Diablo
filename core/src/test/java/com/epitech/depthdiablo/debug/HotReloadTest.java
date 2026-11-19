package com.epitech.depthdiablo.debug;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.data.Reloadable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** F5 (GDD 1.1.2): the data files are read again between two ticks, never mid-tick. */
class HotReloadTest {

  /** Counts its reloads, and fails while {@code failing} is true. */
  private static final class Target implements Reloadable {
    private int reloads;
    private boolean failing;

    @Override
    public void reload() {
      reloads++;
      if (failing) {
        throw new IllegalArgumentException("SPEED of rat must not be negative");
      }
    }
  }

  private final Target first = new Target();
  private final Target second = new Target();
  private final HotReload hotReload = new HotReload(List.of(first, second));

  @Test
  void should_be_named_and_enabled_when_created() {
    assertEquals("Hot reload", hotReload.getName());
    assertTrue(hotReload.isEnabled());
  }

  @Test
  void should_reload_every_target_once_on_the_tick_after_a_request() {
    hotReload.request();

    hotReload.onTick(1);

    assertEquals(1, first.reloads);
    assertEquals(1, second.reloads);
  }

  @Test
  void should_not_reload_before_the_next_tick() {
    hotReload.request();

    assertEquals(0, first.reloads);
  }

  @Test
  void should_reload_nothing_on_a_tick_without_request() {
    hotReload.onTick(1);

    assertEquals(0, first.reloads);
  }

  @Test
  void should_reload_once_when_requested_twice_before_a_tick() {
    hotReload.request();
    hotReload.request();

    hotReload.onTick(1);

    assertEquals(1, first.reloads);
  }

  @Test
  void should_reload_only_on_the_first_tick_after_a_request() {
    hotReload.request();

    hotReload.onTick(1);
    hotReload.onTick(2);

    assertEquals(1, first.reloads);
  }

  @Test
  void should_ignore_a_request_while_disabled() {
    hotReload.disable();

    hotReload.request();
    hotReload.onTick(1);

    assertEquals(0, first.reloads);
  }

  @Test
  void should_forget_a_request_when_disabled_before_the_tick() {
    hotReload.request();
    hotReload.disable();
    hotReload.enable();

    hotReload.onTick(1);

    assertEquals(0, first.reloads);
  }

  @Test
  void should_have_no_error_before_any_reload() {
    assertEquals(Optional.empty(), hotReload.getLastError());
  }

  @Test
  void should_keep_the_error_and_reload_the_other_targets_when_one_fails() {
    first.failing = true;
    hotReload.request();

    assertDoesNotThrow(() -> hotReload.onTick(1));

    assertEquals(1, second.reloads);
    assertEquals(Optional.of("SPEED of rat must not be negative"), hotReload.getLastError());
  }

  @Test
  void should_clear_the_error_when_the_next_reload_works() {
    first.failing = true;
    hotReload.request();
    hotReload.onTick(1);
    first.failing = false;

    hotReload.request();
    hotReload.onTick(2);

    assertEquals(Optional.empty(), hotReload.getLastError());
  }

  @Test
  void should_reject_missing_targets() {
    assertThrows(NullPointerException.class, () -> new HotReload(null));
  }

  @Test
  void should_reject_a_missing_target_in_the_list() {
    List<Reloadable> withNull = Arrays.asList(first, null);

    assertThrows(NullPointerException.class, () -> new HotReload(withNull));
  }
}
