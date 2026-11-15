package com.epitech.depthdiablo.desktop.ui;

import com.epitech.depthdiablo.entity.Character;
import java.util.Objects;

/** Pure-Java model holding HUD values (HP, coins, 3 quick-access slots, and player position). */
public final class HudState {

  /** Number of quick-access inventory slots on the HUD. */
  public static final int SLOT_COUNT = 3;

  private int hp = 100;
  private int maxHp = 100;
  private int coins;
  private int activeSlot;
  private float playerX;
  private float playerY;

  /**
   * Synchronizes HUD health and position from a core {@link Character} entity.
   *
   * @param character the player character; must not be {@code null}
   */
  public void syncFromCharacter(Character character) {
    Objects.requireNonNull(character, "The character shown on the HUD must not be null");
    this.hp = character.hp();
    this.maxHp = character.maxHp();
    this.playerX = character.bodyBox().x();
    this.playerY = character.bodyBox().y();
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

  /** Returns health normalized to {@code [0, 100]} for the Scene2D progress bar. */
  public float healthPercentage() {
    return maxHp <= 0 ? 0f : (hp * 100f) / maxHp;
  }

  /** Returns current HP. */
  public int hp() {
    return hp;
  }

  /** Returns maximum HP. */
  public int maxHp() {
    return maxHp;
  }

  /** Returns current coin count. */
  public int coins() {
    return coins;
  }

  /** Returns the currently selected quick-access slot index (0, 1, or 2). */
  public int activeSlot() {
    return activeSlot;
  }

  /** Returns the player's world X coordinate for the minimap. */
  public float playerX() {
    return playerX;
  }

  /** Returns the player's world Y coordinate for the minimap. */
  public float playerY() {
    return playerY;
  }
}
