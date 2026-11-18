package com.epitech.depthdiablo.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Q38: the orders the user gives the player, kept until the next logic tick reads them. */
class PlayerOrdersTest {

  @Test
  void should_stand_still_and_not_attack_when_created() {
    PlayerOrders orders = new PlayerOrders();

    assertEquals(new Vector2(0f, 0f), orders.moveDirection());
    assertTrue(orders.attackPoint().isEmpty());
  }

  @Test
  void should_give_the_direction_it_walks_in() {
    PlayerOrders orders = new PlayerOrders();

    orders.walk(1f, -1f);

    assertEquals(new Vector2(1f, -1f), orders.moveDirection());
  }

  @Test
  void should_give_the_point_it_attacks_while_held() {
    PlayerOrders orders = new PlayerOrders();

    orders.holdAttack(40f, 12f);

    assertEquals(Optional.of(new Vector2(40f, 12f)), orders.attackPoint());
  }

  @Test
  void should_stop_attacking_once_released() {
    PlayerOrders orders = new PlayerOrders();
    orders.holdAttack(40f, 12f);

    orders.releaseAttack();

    assertTrue(orders.attackPoint().isEmpty());
  }

  @Test
  void should_not_let_the_given_vectors_change_its_orders() {
    PlayerOrders orders = new PlayerOrders();
    orders.walk(1f, 0f);
    orders.holdAttack(40f, 12f);

    orders.moveDirection().set(9f, 9f);
    orders.attackPoint().orElseThrow().set(9f, 9f);

    assertEquals(new Vector2(1f, 0f), orders.moveDirection());
    assertEquals(Optional.of(new Vector2(40f, 12f)), orders.attackPoint());
  }
}
