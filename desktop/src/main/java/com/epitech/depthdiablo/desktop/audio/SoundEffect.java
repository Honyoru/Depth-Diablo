package com.epitech.depthdiablo.desktop.audio;

/**
 * The short sound effects of the game (GDD 4.2), each loaded whole into memory from {@code
 * sounds/<name>.ogg} so it plays with no delay. The music loop is not here: it streams (GDD 4.3).
 */
public enum SoundEffect {
  /** A character takes damage ({@code DamageDealt}). */
  HIT("hit"),
  /** A character dies ({@code EntityDied}). */
  DEATH("death"),
  /** The player keeps an item from the ground ({@code ItemPickedUp}). */
  PICKUP("pickup"),
  /** A menu button gets the focus. */
  MENU_MOVE("menu_move"),
  /** A menu button is pressed. */
  MENU_CONFIRM("menu_confirm");

  private final String path;

  SoundEffect(String name) {
    this.path = "sounds/" + name + ".ogg";
  }

  /**
   * Returns where the file lies in the assets.
   *
   * @return the internal path of the sound file
   */
  public String getPath() {
    return path;
  }
}
