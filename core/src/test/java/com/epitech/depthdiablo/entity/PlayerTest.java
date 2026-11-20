package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.combat.FireTouch;
import com.epitech.depthdiablo.engine.ItemDropped;
import com.epitech.depthdiablo.engine.StateBuffer;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.input.PlayerOrders;
import com.epitech.depthdiablo.item.Armor;
import com.epitech.depthdiablo.item.FireRing;
import com.epitech.depthdiablo.item.Inventory;
import com.epitech.depthdiablo.item.Item;
import com.epitech.depthdiablo.item.LeatherArmor;
import com.epitech.depthdiablo.item.Potion;
import com.epitech.depthdiablo.item.Sword;
import com.epitech.depthdiablo.item.Weapon;
import com.epitech.depthdiablo.testdata.Items;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1 and 1.5: the player walks where its input points */
class PlayerTest {

  @Test
  void should_get_back_its_walking_when_its_state_is_read_back() {
    Player player = aKnight();
    input.walk(1f, 0f);
    tick(player, 1);
    StateBuffer photo = new StateBuffer();
    player.writeState(photo);
    input.walk(0f, 0f);
    tick(player, 1);

    photo.startReading();
    player.readState(photo);

    assertTrue(player.isMoving());
  }

  private static final float SPEED = 120f;
  private static final float TOLERANCE = 0.01f;
  private static final int ONE_SECOND = GameConstants.TICKS_PER_SECOND;

  private final PlayerOrders input = new PlayerOrders();

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
    input.walk(1f, 0f);

    tick(player, ONE_SECOND);

    assertEquals(SPEED, player.position().x, TOLERANCE);
    assertEquals(0f, player.position().y, TOLERANCE);
  }

  @Test
  void should_be_moving_when_it_walked_during_the_tick() {
    Player player = aKnight();
    input.walk(0f, 1f);

    tick(player, 1);

    assertTrue(player.isMoving());
  }

  @Test
  void should_walk_no_faster_when_steered_diagonally() {
    Player player = aKnight();
    input.walk(1f, 1f);

    tick(player, ONE_SECOND);

    assertEquals(SPEED, player.position().len(), TOLERANCE);
    assertEquals(player.position().x, player.position().y, TOLERANCE);
  }

  @Test
  void should_walk_at_half_speed_when_the_direction_is_half_as_long() {
    Player player = aKnight();
    input.walk(-0.5f, 0f);

    tick(player, ONE_SECOND);

    assertEquals(-SPEED / 2f, player.position().x, TOLERANCE);
  }

  @Test
  void should_stop_when_the_direction_returns_to_zero() {
    Player player = aKnight();
    input.walk(1f, 0f);
    tick(player, 10);
    float xWhenReleased = player.position().x;

    input.walk(0f, 0f);
    tick(player, 10);

    assertEquals(xWhenReleased, player.position().x, TOLERANCE);
    assertFalse(player.isMoving());
  }

  @Test
  void should_keep_the_size_of_its_body_box_when_it_walks() {
    Player player = aKnight();
    input.walk(1f, 1f);

    tick(player, 10);

    assertEquals(16f, player.getBodyBox().width());
    assertEquals(16f, player.getBodyBox().height());
  }

  @Test
  void should_not_move_when_dead() {
    Player player = aKnight();
    player.takeDamage(player.getMaxHp());
    input.walk(1f, 0f);

    tick(player, 10);

    assertEquals(new Vector2(0f, 0f), player.position());
    assertFalse(player.isMoving());
  }

  @Test
  void should_not_move_when_removed() {
    Player player = aKnight();
    player.remove();
    input.walk(1f, 0f);

    tick(player, 10);

    assertEquals(new Vector2(0f, 0f), player.position());
  }

  @Test
  void should_reject_a_null_input() {
    Aabb box = new Aabb(0f, 0f, 16f, 16f);
    StatSource stats = stats();
    Sword sword = Items.sword();
    LeatherArmor armor = Items.leatherArmor();

    assertThrows(
        NullPointerException.class,
        () -> new Knight(box, stats, sword, armor, box, 0, null, world));
  }

  @Test
  void should_fight_for_the_player_side() {
    Player player = aKnight();

    assertEquals(Faction.PLAYER, player.getFaction());
  }

  @Test
  void should_hold_its_starting_weapon_in_its_inventory() {
    Sword sword = Items.sword();
    Aabb box = new Aabb(0f, 0f, 16f, 16f);

    Player player = new Knight(box, stats(), sword, Items.leatherArmor(), box, 0, input, world);

    assertSame(sword, player.getInventory().getWeapon());
  }

  @Test
  void should_count_its_starting_weapon_in_its_attack() {
    Player player = aKnight();

    assertEquals(1 + Items.sword().getAttackBonus(), player.getAttack());
  }

  @Test
  void should_swing_towards_the_point_it_attacks() {
    Player player = aKnight();
    input.holdAttack(108f, 8f);

    tick(player, 1);

    assertEquals(0f, player.getCurrentSwing().orElseThrow().getAimDegrees(), TOLERANCE);
  }

  @Test
  void should_swing_up_when_it_attacks_a_point_above() {
    Player player = aKnight();
    input.holdAttack(8f, 108f);

    tick(player, 1);

    assertEquals(90f, player.getCurrentSwing().orElseThrow().getAimDegrees(), TOLERANCE);
  }

  @Test
  void should_not_swing_when_it_does_not_attack() {
    Player player = aKnight();

    tick(player, 1);

    assertTrue(player.getCurrentSwing().isEmpty());
  }

  @Test
  void should_hit_the_enemy_its_swing_reaches() {
    Player player = aKnight();
    Aabb box = new Aabb(20f, 0f, 16f, 16f);
    Character enemy =
        aCharacter().withFaction(Faction.ENEMY).withBodyBox(box).withHurtbox(box).build();
    world.add(enemy);
    input.holdAttack(28f, 8f);

    tick(player, 12);

    assertEquals(7, enemy.getHp());
  }

  @Test
  void should_count_its_starting_armor_in_its_defense() {
    Player player = aKnight();

    assertEquals(1 + Items.leatherArmor().getDefenseBonus(), player.getDefense());
  }

  @Test
  void should_put_a_picked_up_consumable_in_its_hotbar() {
    Player player = aKnight();
    Potion potion = Items.potion();

    assertEquals(Optional.empty(), player.pickUp(potion));
    assertSame(potion, player.getInventory().hotbarSlot(0));
  }

  @Test
  void should_leave_a_consumable_on_the_ground_when_its_hotbar_is_full() {
    Player player = aKnight();
    for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
      player.pickUp(Items.potion());
    }
    Potion extra = Items.potion();

    assertEquals(Optional.of(extra), player.pickUp(extra));
  }

  @Test
  void should_put_a_picked_up_passive_item_in_its_passive_slots() {
    Player player = aKnight();
    FireRing ring = Items.fireRing();

    assertEquals(Optional.empty(), player.pickUp(ring));
    assertSame(ring, player.getInventory().passiveSlot(0));
  }

  @Test
  void should_attack_through_a_picked_up_passive_item() {
    Player player = aKnight();

    player.pickUp(Items.fireRing());

    assertInstanceOf(FireTouch.class, player.getBuild().attack());
  }

  @Test
  void should_leave_a_passive_item_on_the_ground_when_its_passive_slots_are_full() {
    Player player = aKnight();
    for (int i = 0; i < Inventory.PASSIVE_SIZE; i++) {
      player.pickUp(Items.fireRing());
    }
    FireRing extra = Items.fireRing();

    assertEquals(Optional.of(extra), player.pickUp(extra));
  }

  @Test
  void should_leave_its_old_weapon_on_the_ground_when_it_picks_up_a_weapon() {
    Player player = aKnight();
    Weapon old = player.getInventory().getWeapon();

    assertEquals(Optional.of(old), player.pickUp(Items.sword()));
  }

  @Test
  void should_attack_with_a_picked_up_weapon() {
    Player player = aKnight();
    Sword newSword = Items.sword();

    player.pickUp(newSword);

    assertSame(newSword, player.getBuild().attack());
  }

  @Test
  void should_leave_its_old_armor_on_the_ground_when_it_picks_up_an_armor() {
    Player player = aKnight();
    Armor old = player.getInventory().getArmor();
    LeatherArmor newArmor = Items.leatherArmor();

    assertEquals(Optional.of(old), player.pickUp(newArmor));
    assertSame(newArmor, player.getInventory().getArmor());
  }

  @Test
  void should_stop_attacking_through_a_passive_item_it_drops() {
    Player player = aKnight();
    FireRing ring = Items.fireRing();
    player.pickUp(ring);

    assertEquals(Optional.of(ring), player.dropPassive(0));
    assertSame(player.getInventory().getWeapon(), player.getBuild().attack());
  }

  @Test
  void should_give_nothing_when_it_drops_a_free_passive_slot() {
    assertEquals(Optional.empty(), aKnight().dropPassive(0));
  }

  @Test
  void should_give_back_the_consumable_it_drops() {
    Player player = aKnight();
    Potion potion = Items.potion();
    player.pickUp(potion);

    assertEquals(Optional.of(potion), player.dropConsumable(0));
    assertTrue(player.getInventory().isHotbarSlotFree(0));
  }

  @Test
  void should_swap_two_of_its_passive_slots() {
    Player player = aKnight();
    FireRing ring = Items.fireRing();
    player.pickUp(ring);

    player.swapPassiveSlots(0, 4);

    assertSame(ring, player.getInventory().passiveSlot(4));
    assertInstanceOf(FireTouch.class, player.getBuild().attack());
  }

  @Test
  void should_throw_when_the_picked_up_item_is_null() {
    Player player = aKnight();

    assertThrows(NullPointerException.class, () -> player.pickUp(null));
  }

  @Test
  void should_target_an_item_in_range() {
    Player player = aKnight();
    ItemDrop drop = aDropAt(30, 8); // 22 px from its centre

    assertEquals(Optional.of(drop), player.getPickUpTarget());
  }

  @Test
  void should_target_nothing_when_the_items_are_out_of_range() {
    Player player = aKnight();
    aDropAt(60, 8); // 52 px away

    assertEquals(Optional.empty(), player.getPickUpTarget());
  }

  @Test
  void should_target_the_nearest_item_in_range() {
    Player player = aKnight();
    aDropAt(30, 8);
    ItemDrop nearest = aDropAt(12, 8);

    assertEquals(Optional.of(nearest), player.getPickUpTarget());
  }

  @Test
  void should_not_target_an_item_already_taken() {
    Player player = aKnight();
    aDropAt(12, 8).remove();

    assertEquals(Optional.empty(), player.getPickUpTarget());
  }

  @Test
  void should_pick_up_the_targeted_item_when_the_pickup_key_is_pressed() {
    Player player = aKnight();
    ItemDrop drop = aDropAt(12, 8);
    input.holdPickUp();

    tick(player, 1);

    assertSame(drop.getItem(), player.getInventory().hotbarSlot(0));
    assertTrue(drop.isRemoved());
  }

  @Test
  void should_pick_up_only_one_item_per_press() {
    Player player = aKnight();
    aDropAt(12, 8);
    ItemDrop second = aDropAt(14, 8);
    input.holdPickUp();

    tick(player, 3); // held for 3 ticks

    assertFalse(second.isRemoved());
  }

  @Test
  void should_pick_up_again_once_the_key_is_pressed_again() {
    Player player = aKnight();
    aDropAt(12, 8);
    ItemDrop second = aDropAt(14, 8);
    input.holdPickUp();
    player.update(1);
    input.releasePickUp();
    player.update(2);
    input.holdPickUp();

    player.update(3);

    assertTrue(second.isRemoved());
  }

  @Test
  void should_drop_its_old_weapon_where_it_stands_when_it_picks_up_a_weapon() {
    Player player = aKnight();
    Weapon old = player.getInventory().getWeapon();
    world.getEventBus().subscribe(ItemDropped.class, new ItemDropper(Items.factory(), world));
    world.add(new ItemDrop(Items.sword(), new Vector2(12, 8)));
    input.holdPickUp();

    tick(player, 1);
    world.getEventBus().flush();

    assertTrue(liesOnTheGround(old));
  }

  @Test
  void should_leave_the_item_on_the_ground_when_its_bag_is_full() {
    Player player = aKnight();
    for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
      player.pickUp(Items.potion());
    }
    Potion extra = Items.potion();
    world.getEventBus().subscribe(ItemDropped.class, new ItemDropper(Items.factory(), world));
    world.add(new ItemDrop(extra, new Vector2(12, 8)));
    input.holdPickUp();

    tick(player, 1);
    world.getEventBus().flush();

    assertTrue(liesOnTheGround(extra));
  }

  /** Puts a potion on the ground at the given centre. */
  private ItemDrop aDropAt(float x, float y) {
    ItemDrop drop = new ItemDrop(Items.potion(), new Vector2(x, y));
    world.add(drop);
    return drop;
  }

  /** Tells whether an item lies on the ground, not taken. */
  private boolean liesOnTheGround(Item item) {
    return world.getEntities().stream()
        .anyMatch(e -> e instanceof ItemDrop drop && drop.getItem() == item && !drop.isRemoved());
  }

  @Test
  void should_drop_a_passive_item_picked_up_after_the_photo_when_its_state_is_read_back() {
    Player player = aKnight();
    StateBuffer photo = new StateBuffer();
    player.writeState(photo);
    player.pickUp(Items.fireRing());

    photo.startReading();
    player.readState(photo);

    assertTrue(player.getInventory().isPassiveSlotFree(0));
    assertSame(player.getInventory().getWeapon(), player.getBuild().attack());
  }

  @Test
  void should_use_the_consumable_of_the_selected_slot_when_the_use_key_is_pressed() {
    Player player = aKnight();
    player.pickUp(Items.potion());
    player.pickUp(Items.potion());
    player.takeDamage(5);
    input.selectHotbarSlot(1);
    input.holdUse();

    tick(player, 1);

    assertTrue(player.getInventory().isHotbarSlotFree(1));
    assertFalse(player.getInventory().isHotbarSlotFree(0));
  }

  @Test
  void should_use_only_one_consumable_per_press() {
    Player player = aKnight();
    player.pickUp(Items.potion());
    player.pickUp(Items.potion());
    player.takeDamage(5);
    input.holdUse();

    tick(player, 3);

    assertFalse(player.getInventory().isHotbarSlotFree(1));
  }

  @Test
  void should_get_back_a_used_potion_and_its_hp_when_its_state_is_read_back() {
    Player player = aKnight();
    Potion potion = Items.potion();
    player.pickUp(potion);
    player.takeDamage(5);
    int hpBefore = player.getHp();
    StateBuffer photo = new StateBuffer();
    player.writeState(photo);
    input.holdUse();
    tick(player, 1);

    photo.startReading();
    player.readState(photo);

    assertSame(potion, player.getInventory().hotbarSlot(0));
    assertEquals(hpBefore, player.getHp());
  }

  private Knight aKnight() {
    Aabb box = new Aabb(0f, 0f, 16f, 16f);
    return new Knight(box, stats(), Items.sword(), Items.leatherArmor(), box, 0, input, world);
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

  @Test
  void should_stop_against_a_wall_when_steered_into_it() {
    world.add(new Wall(new Aabb(20f, 0f, 32f, 32f)));
    Player player = aKnight();
    input.walk(1f, 0f);

    tick(player, ONE_SECOND);

    assertEquals(4f, player.position().x, TOLERANCE); // right20
    assertEquals(0f, player.position().y, TOLERANCE);
  }

  @Test
  void should_slide_along_a_wall_when_steered_diagonally_into_it() {
    world.add(new Wall(new Aabb(20f, -100f, 32f, 300f)));
    Player player = aKnight();
    input.walk(1f, 1f);

    tick(player, ONE_SECOND);

    assertTrue(player.position().x <= 4f);
    assertEquals(SPEED / (float) Math.sqrt(2), player.position().y, TOLERANCE);
  }

  @Test
  void should_dash_seventy_two_pixels_in_twelve_ticks() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDash();

    tick(player, 12);

    assertEquals(72f, player.position().x, TOLERANCE);
    assertTrue(player.getEvade().isEmpty());
  }

  @Test
  void should_not_dash_when_standing_still() {
    Player player = aKnight();
    input.holdDash();

    tick(player, 1);

    assertTrue(player.getEvade().isEmpty());
    assertEquals(0f, player.position().x, TOLERANCE);
  }

  @Test
  void should_keep_its_dash_direction_when_steered_elsewhere() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDash();
    tick(player, 1);

    input.walk(0f, 1f);
    tick(player, 11);

    assertEquals(72f, player.position().x, TOLERANCE);
    assertEquals(0f, player.position().y, TOLERANCE);
  }

  @Test
  void should_dash_once_per_press_when_the_key_is_held() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDash();

    tick(player, 12 + 30 + 12);

    assertEquals(72f + 42 * 2f, player.position().x, TOLERANCE);
  }

  @Test
  void should_not_dash_again_before_the_cooldown_is_over() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDash();
    tick(player, 12);
    input.releaseDash();
    tick(player, 29);

    input.holdDash();
    tick(player, 1);

    assertTrue(player.getEvade().isEmpty());
  }

  @Test
  void should_dash_again_once_the_cooldown_is_over() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDash();
    tick(player, 12);
    input.releaseDash();
    tick(player, 30);

    input.holdDash();
    tick(player, 12);

    assertEquals(72f + 30 * 2f + 72f, player.position().x, TOLERANCE);
  }

  @Test
  void should_still_be_hit_while_dashing() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDash();
    tick(player, 1);
    int hpBefore = player.getHp();

    player.takeDamage(3);

    assertTrue(player.getHp() < hpBefore);
  }

  @Test
  void should_stop_against_a_wall_while_dashing() {
    world.add(new Wall(new Aabb(20f, 0f, 32f, 32f)));
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDash();

    tick(player, 12);

    assertEquals(4f, player.position().x, TOLERANCE);
  }

  @Test
  void should_roll_seventy_two_pixels_in_twenty_four_ticks() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDodgeRoll();

    tick(player, 24);

    assertEquals(72f, player.position().x, TOLERANCE);
  }

  @Test
  void should_take_no_damage_while_rolling() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDodgeRoll();
    tick(player, 1);
    int hpBefore = player.getHp();

    player.takeDamage(3);

    assertEquals(hpBefore, player.getHp());
  }

  @Test
  void should_be_open_to_hits_again_once_the_roll_is_over() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDodgeRoll();
    tick(player, 24);
    assertTrue(player.isInvincible()); // its last rolling tick, still covered

    tick(player, 1);

    assertFalse(player.isInvincible());
  }

  @Test
  void should_roll_rather_than_dash_when_both_are_pressed_on_the_same_tick() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDodgeRoll();
    input.holdDash();

    tick(player, 1);

    assertEquals(Optional.of(Evade.DODGE_ROLL), player.getEvade());
  }

  @Test
  void should_dash_right_after_a_roll_as_their_cooldowns_are_apart() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDodgeRoll();
    tick(player, 24);

    input.holdDash();
    tick(player, 1);

    assertEquals(Optional.of(Evade.DASH), player.getEvade());
  }

  @Test
  void should_dash_when_both_are_pressed_while_the_roll_cools_down() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDodgeRoll();
    tick(player, 24);
    input.releaseDodgeRoll();
    tick(player, 1);

    input.holdDodgeRoll();
    input.holdDash();
    tick(player, 1);

    assertEquals(Optional.of(Evade.DASH), player.getEvade());
  }

  @Test
  void should_finish_its_dash_from_where_the_photo_was_taken() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDash();
    tick(player, 4);
    StateBuffer photo = new StateBuffer();
    player.writeState(photo);
    tick(player, 8);

    photo.startReading();
    player.readState(photo);
    assertEquals(Optional.of(Evade.DASH), player.getEvade());
    input.walk(0f, 1f);
    tick(player, 8);

    assertEquals(72f, player.position().x, TOLERANCE);
    assertEquals(0f, player.position().y, TOLERANCE);
  }

  @Test
  void should_get_its_dash_cooldown_back_when_its_state_is_read_back() {
    Player player = aKnight();
    input.walk(1f, 0f);
    input.holdDash();
    tick(player, 12);
    StateBuffer photo = new StateBuffer();
    player.writeState(photo);
    input.releaseDash();
    tick(player, 30);

    photo.startReading();
    player.readState(photo);
    input.holdDash();
    tick(player, 1);

    assertTrue(player.getEvade().isEmpty());
  }

  @Test
  void should_walk_up_to_a_wall_even_when_its_steps_do_not_fit_the_gap() {
    world.add(new Wall(new Aabb(21f, 0f, 32f, 32f)));
    Player player = aKnight();
    input.walk(1f, 0f);

    tick(player, ONE_SECOND);

    assertEquals(5f, player.position().x, TOLERANCE);
  }

  @Test
  void should_walk_away_from_a_wall_it_touches() {
    world.add(new Wall(new Aabb(16f, 0f, 32f, 32f)));
    Player player = aKnight();
    input.walk(-1f, 0f);

    tick(player, 1);

    assertEquals(-2f, player.position().x, TOLERANCE);
  }

  @Test
  void should_carry_its_hurtbox_when_it_walks() {
    Aabb body = new Aabb(0f, 0f, 16f, 16f);
    Aabb hurtbox = new Aabb(4f, 8f, 8f, 8f);
    Player player =
        new Knight(body, stats(), Items.sword(), Items.leatherArmor(), hurtbox, 0, input, world);
    input.walk(1f, 0f);

    tick(player, 10);

    assertEquals(new Aabb(24f, 8f, 8f, 8f), player.getHurtbox());
  }
}
