package com.epitech.depthdiablo.desktop.ui;

import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.arena.ArenaCycle;
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

  private String progressText = "";

  private String timerText = "";

  private boolean bossShown;

  private float bossHealthPercentage;

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

  /**
   * Returns what the bottom-left corner shows: the wave in the Arena, the floor in Endless.
   *
   * @return e.g. "Wave 3", "Wave 5 - BOSS" or "Floor 1"; empty before the first sync
   */
  public String getProgressText() {
    return progressText;
  }

  /**
   * Returns what the top of the screen shows in the Arena: the countdown of the break or the shop.
   *
   * @return e.g. "Next wave in 4 s" or "Shop open"; empty while a wave fights and in Endless
   */
  public String getTimerText() {
    return timerText;
  }

  /**
   * Tells whether the boss bar shows: a boss is alive in the world.
   *
   * @return true while a boss fights
   */
  public boolean isBossShown() {
    return bossShown;
  }

  /**
   * Returns the boss's life, for the bar at the top.
   *
   * @return from 0 to 100; 0 while no boss fights
   */
  public float getBossHealthPercentage() {
    return bossHealthPercentage;
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

  /**
   * Reads the wave and the timer from the Arena cycle; called every frame in Arena mode.
   *
   * @param cycle the arena cycle
   * @throws NullPointerException if {@code cycle} is {@code null}
   */
  public void syncArena(ArenaCycle cycle) {
    Objects.requireNonNull(cycle, "cycle is null");
    boolean bossFights = cycle.getPhase() == ArenaCycle.Phase.WAVE && cycle.isBossWave();
    progressText = "Wave " + cycle.getWaveNumber() + (bossFights ? " - BOSS" : "");
    timerText =
        switch (cycle.getPhase()) {
          case BREAK -> "Next wave in " + secondsLeft(cycle.getBreakTicksLeft()) + " s";
          case SHOP -> "Shop open";
          case WAVE -> "";
        };
  }

  /**
   * Shows the floor in Endless mode, which has no timer.
   *
   * @param floor the floor the player is on; 1 or more
   * @throws IllegalArgumentException if {@code floor} is below 1
   */
  public void syncFloor(int floor) {
    if (floor < 1) {
      throw new IllegalArgumentException("The floor must be 1 or more, was " + floor);
    }
    progressText = "Floor " + floor;
    timerText = "";
  }

  /**
   * Shows the boss bar with the boss's life.
   *
   * @param hp the boss's HP; 0 or more
   * @param maxHp its maximum HP; above 0
   * @throws IllegalArgumentException if {@code hp} is negative or {@code maxHp} is 0 or less
   */
  public void syncBoss(int hp, int maxHp) {
    if (hp < 0 || maxHp <= 0) {
      throw new IllegalArgumentException(
          "Boss HP must be 0 or more out of more than 0, was " + hp + " / " + maxHp);
    }
    bossShown = true;
    bossHealthPercentage = 100f * Math.min(hp, maxHp) / maxHp;
  }

  /** Hides the boss bar: no boss fights. */
  public void hideBoss() {
    bossShown = false;
    bossHealthPercentage = 0f;
  }

  /** Whole seconds left, rounded up: 59 ticks still show 1 s, never 0 s. */
  private static int secondsLeft(int ticks) {
    return (ticks + GameConstants.TICKS_PER_SECOND - 1) / GameConstants.TICKS_PER_SECOND;
  }
}
