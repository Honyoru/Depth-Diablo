package com.epitech.depthdiablo.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    assertFalse(orders.wantsPickUp());
    assertFalse(orders.wantsDash());
    assertFalse(orders.wantsDodgeRoll());
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

  @Test
  void should_want_to_pick_up_while_held() {
    PlayerOrders orders = new PlayerOrders();

    orders.holdPickUp();

    assertTrue(orders.wantsPickUp());
  }

  @Test
  void should_stop_wanting_to_pick_up_once_released() {
    PlayerOrders orders = new PlayerOrders();
    orders.holdPickUp();

    orders.releasePickUp();

    assertFalse(orders.wantsPickUp());
  }

  @Test
  void should_select_the_first_hotbar_slot_when_created() {
    assertEquals(0, new PlayerOrders().hotbarSlot());
  }

  @Test
  void should_give_the_hotbar_slot_it_selects() {
    PlayerOrders orders = new PlayerOrders();

    orders.selectHotbarSlot(2);

    assertEquals(2, orders.hotbarSlot());
  }

  @Test
  void should_throw_when_the_selected_hotbar_slot_is_outside_the_hotbar() {
    PlayerOrders orders = new PlayerOrders();

    assertThrows(IndexOutOfBoundsException.class, () -> orders.selectHotbarSlot(3));
  }

  @Test
  void should_want_to_use_while_held_and_stop_once_released() {
    PlayerOrders orders = new PlayerOrders();
    orders.holdUse();
    assertTrue(orders.wantsToUse());

    orders.releaseUse();

    assertFalse(orders.wantsToUse());
  }

  @Test
  void should_want_to_dash_while_held_and_stop_once_released() {
    PlayerOrders orders = new PlayerOrders();
    orders.holdDash();
    assertTrue(orders.wantsDash());

    orders.releaseDash();

    assertFalse(orders.wantsDash());
  }

  @Test
  void should_want_to_roll_while_held_and_stop_once_released() {
    PlayerOrders orders = new PlayerOrders();
    orders.holdDodgeRoll();
    assertTrue(orders.wantsDodgeRoll());

    orders.releaseDodgeRoll();

    assertFalse(orders.wantsDodgeRoll());
  }
}
