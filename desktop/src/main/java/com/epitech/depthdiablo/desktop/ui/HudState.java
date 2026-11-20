package com.epitech.depthdiablo.desktop.ui;

import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.item.Inventory;
import java.util.Arrays;
import java.util.Objects;

/** Pure-Java model holding HUD values (HP, coins, 3 quick-access slots, and player position). */
public final class HudState {

  /** Number of quick-access inventory slots on the HUD. */
  public static final int SLOT_COUNT = 3;

  /** The picture of a free hotbar slot. */
  private static final String FREE_SLOT_SPRITE = "items/empty";

  private final String[] hotbarSpriteIds = new String[SLOT_COUNT];

  private int hp = 100;

  private int maxHp = 100;

  private int coins;

  private int activeSlot;

  private float playerX;

  private float playerY;

  /** Creates a HUD model with free hotbar slots. */
  public HudState() {
    Arrays.fill(hotbarSpriteIds, FREE_SLOT_SPRITE);
  }

  /** Returns health normalized to {@code [0, 100]} for the Scene2D progress bar. */
  public float getHealthPercentage() {
    return maxHp <= 0 ? 0f : (hp * 100f) / maxHp;
  }

  /** Returns current HP. */
  public int getHp() {
    return hp;
  }

  /** Returns maximum HP. */
  public int getMaxHp() {
    return maxHp;
  }

  /** Returns current coin count. */
  public int getCoins() {
    return coins;
  }

  /** Returns the currently selected quick-access slot index (0, 1, or 2). */
  public int getActiveSlot() {
    return activeSlot;
  }

  /** Returns the player's world X coordinate for the minimap. */
  public float getPlayerX() {
    return playerX;
  }

  /** Returns the player's world Y coordinate for the minimap. */
  public float getPlayerY() {
    return playerY;
  }

  /**
   * Synchronizes HUD health and position from a core {@link Character} entity.
   *
   * @param character the player character; must not be {@code null}
   */
  public void syncFromCharacter(Character character) {
    Objects.requireNonNull(character, "The character shown on the HUD must not be null");
    this.hp = character.getHp();
    this.maxHp = character.getMaxHp();
    this.playerX = character.getBodyBox().x();
    this.playerY = character.getBodyBox().y();
  }

  /**
   * Adds coins to the player's total.
   *
   * @param amount number of coins to add; must not be negative
   */
  public void addCoins(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("The coin amount must not be negative, was " + amount);
    }
    this.coins += amount;
  }

  /**
   * Selects one of the 3 quick-access slots.
   *
   * @param slotIndex index in {@code [0, SLOT_COUNT - 1]}
   */
  public void selectSlot(int slotIndex) {
    if (slotIndex < 0 || slotIndex >= SLOT_COUNT) {
      throw new IllegalArgumentException("slotIndex out of bounds: " + slotIndex);
    }
    this.activeSlot = slotIndex;
  }

  /**
   * Copies the picture of each hotbar consumable; a free slot shows {@code items/empty}.
   *
   * @param inventory the player's inventory; must not be {@code null}
   */
  public void syncHotbar(Inventory inventory) {
    Objects.requireNonNull(inventory, "inventory is null");
    for (int i = 0; i < SLOT_COUNT; i++) {
      hotbarSpriteIds[i] = inventory.hotbarSlot(i).getSpriteId();
    }
  }

  /**
   * Returns the picture of a hotbar slot.
   *
   * @param slot the slot, from 0 to {@link #SLOT_COUNT} - 1
   * @return the sprite id of its consumable, or {@code items/empty} if it is free
   * @throws IndexOutOfBoundsException if {@code slot} is outside the hotbar
   */
  public String getHotbarSpriteId(int slot) {
    return hotbarSpriteIds[Objects.checkIndex(slot, SLOT_COUNT)];
  }
}
