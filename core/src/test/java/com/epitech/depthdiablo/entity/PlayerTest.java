package com.epitech.depthdiablo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.input.PlayerInput;
import com.epitech.depthdiablo.item.Sword;
import com.epitech.depthdiablo.testdata.Items;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1 and 1.5: the player walks where its input points */
class PlayerTest {
  private static final float SPEED = 120f;
  private static final float TOLERANCE = 0.01f;
  private static final int ONE_SECOND = GameConstants.TICKS_PER_SECOND;

  private final FakePlayerInput input = new FakePlayerInput();

  private final World world = new World();

  @Test
  void should_stand_still_when_the_input_has_no_direction() {
    Player player = aKnight();

    tick(player, ONE_SECOND);

    assertEquals(new Vector2(0f, 0f), player.position());
    assertFalse(player.isMoving());
  }

  @Test
  void should_walk_its_speed_in_pixels_when_steered_right_for_one_second() {
    Player player = aKnight();
    input.point(1f, 0f);

    tick(player, ONE_SECOND);

    assertEquals(SPEED, player.position().x, TOLERANCE);
    assertEquals(0f, player.position().y, TOLERANCE);
  }

  @Test
  void should_be_moving_when_it_walked_during_the_tick() {
    Player player = aKnight();
    input.point(0f, 1f);

    tick(player, 1);

    assertTrue(player.isMoving());
  }

  @Test
  void should_walk_no_faster_when_steered_diagonally() {
    Player player = aKnight();
    input.point(1f, 1f);

    tick(player, ONE_SECOND);

    assertEquals(SPEED, player.position().len(), TOLERANCE);
    assertEquals(player.position().x, player.position().y, TOLERANCE);
  }

  @Test
  void should_walk_at_half_speed_when_the_direction_is_half_as_long() {
    Player player = aKnight();
    input.point(-0.5f, 0f);

    tick(player, ONE_SECOND);

    assertEquals(-SPEED / 2f, player.position().x, TOLERANCE);
  }

  @Test
  void should_stop_when_the_direction_returns_to_zero() {
    Player player = aKnight();
    input.point(1f, 0f);
    tick(player, 10);
    float xWhenReleased = player.position().x;

    input.point(0f, 0f);
    tick(player, 10);

    assertEquals(xWhenReleased, player.position().x, TOLERANCE);
    assertFalse(player.isMoving());
  }

  @Test
  void should_keep_the_size_of_its_body_box_when_it_walks() {
    Player player = aKnight();
    input.point(1f, 1f);

    tick(player, 10);

    assertEquals(16f, player.getBodyBox().width());
    assertEquals(16f, player.getBodyBox().height());
  }

  @Test
  void should_not_move_when_dead() {
    Player player = aKnight();
    player.takeDamage(player.getMaxHp());
    input.point(1f, 0f);

    tick(player, 10);

    assertEquals(new Vector2(0f, 0f), player.position());
    assertFalse(player.isMoving());
  }

  @Test
  void should_not_move_when_removed() {
    Player player = aKnight();
    player.remove();
    input.point(1f, 0f);

    tick(player, 10);

    assertEquals(new Vector2(0f, 0f), player.position());
  }

  @Test
  void should_reject_a_null_input() {
    Aabb box = new Aabb(0f, 0f, 16f, 16f);
    StatSource stats = stats();
    Sword sword = Items.sword();

    assertThrows(
        NullPointerException.class, () -> new Knight(box, stats, sword, box, 0, null, world));
  }

  @Test
  void should_fight_for_the_player_side() {
    Player player = aKnight();

    assertEquals(Faction.PLAYER, player.getFaction());
  }

  private Knight aKnight() {
    Aabb box = new Aabb(0f, 0f, 16f, 16f);
    return new Knight(box, stats(), Items.sword(), box, 0, input, world);
  }

  private static BaseStats stats() {
    Map<Stat, Float> values = new EnumMap<>(Stat.class);
    values.put(Stat.MAX_HP, 10f);
    values.put(Stat.ATTACK, 1f);
    values.put(Stat.DEFENSE, 1f);
    values.put(Stat.SPEED, SPEED);
    values.put(Stat.ATTACK_SPEED, 1f);
    values.put(Stat.LUCK, 0f);
    return new BaseStats(values);
  }

  private static void tick(Player player, int ticks) {
    for (int i = 1; i <= ticks; i++) {
      player.update(i);
    }
  }

  /** Hand-written fake (Q117): an input that points where the test says. */
  private static final class FakePlayerInput implements PlayerInput {
    private final Vector2 direction = new Vector2();

    void point(float x, float y) {
      direction.set(x, y);
    }

    @Override
    public Vector2 moveDirection() {
      return new Vector2(direction);
    }
  }

  @Test
  void should_stop_against_a_wall_when_steered_into_it() {
    world.add(new Wall(new Aabb(20f, 0f, 32f, 32f)));
    Player player = aKnight();
    input.point(1f, 0f);

    tick(player, ONE_SECOND);

    assertEquals(4f, player.position().x, TOLERANCE); // right20
    assertEquals(0f, player.position().y, TOLERANCE);
  }

  @Test
  void should_slide_along_a_wall_when_steered_diagonally_into_it() {
    world.add(new Wall(new Aabb(20f, -100f, 32f, 300f)));
    Player player = aKnight();
    input.point(1f, 1f);

    tick(player, ONE_SECOND);

    assertTrue(player.position().x <= 4f);
    assertEquals(SPEED / (float) Math.sqrt(2), player.position().y, TOLERANCE);
  }

  @Test
  void should_walk_away_from_a_wall_it_touches() {
    world.add(new Wall(new Aabb(16f, 0f, 32f, 32f)));
    Player player = aKnight();
    input.point(-1f, 0f);

    tick(player, 1);

    assertEquals(-2f, player.position().x, TOLERANCE);
  }

  @Test
  void should_carry_its_hurtbox_when_it_walks() {
    Aabb body = new Aabb(0f, 0f, 16f, 16f);
    Aabb hurtbox = new Aabb(4f, 8f, 8f, 8f);
    Player player = new Knight(body, stats(), Items.sword(), hurtbox, 0, input, world);
    input.point(1f, 0f);

    tick(player, 10);

    assertEquals(new Aabb(24f, 8f, 8f, 8f), player.getHurtbox());
  }
}
