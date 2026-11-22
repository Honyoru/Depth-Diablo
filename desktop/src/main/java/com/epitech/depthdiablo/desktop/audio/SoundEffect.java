package com.epitech.depthdiablo.desktop.audio;

/**
 * The short sound effects of the game (GDD 4.2), each loaded whole into memory from {@code
 * sounds/<file>.ogg} so it plays with no delay. Two effects may share a file. The music loop is not
 * here: it streams (GDD 4.3). Each effect has its own volume, so a loud file can be turned down
 * without editing it.
 */
public enum SoundEffect {
  /** A player swings a melee weapon: every {@code MeleeWeapon} ({@code AttackStarted}). */
  ATTACK_MELEE("attack_melee", 600),
  /** A player shoots a bow: every {@code Bow}. */
  ATTACK_BOW("attack_bow", 449),
  /** A player casts with a staff: every {@code Staff}. */
  ATTACK_STAFF("attack_staff", 896, 0.7f),
  /** An enemy attacks; the melee sound for now, its own file later. */
  ATTACK_ENEMY("attack_melee", 600),
  /** A character takes damage, heard from the one hit ({@code DamageDealt}). */
  DAMAGE("damage", 313),
  /** A character dies ({@code EntityDied}). */
  DEATH("death", 261),
  /** The player keeps an item from the ground ({@code ItemPickedUp}). */
  PICKUP("pickup", 661),
  /** The player takes a step while walking. */
  FOOTSTEP("footstep", 323),
  /** A menu button gets the focus. */
  MENU_MOVE("menu_move", 383, 0.8f),
  /** A menu button is pressed. */
  MENU_CONFIRM("menu_confirm", 322, 0.8f);

  /** The volume of an effect that is not turned down. */
  private static final float FULL_VOLUME = 1f;

  private final String path;
  private final long durationMillis;
  private final float volume;

  SoundEffect(String file, long durationMillis) {
    this(file, durationMillis, FULL_VOLUME);
  }

  SoundEffect(String file, long durationMillis, float volume) {
    this.path = "sounds/" + file + ".ogg";
    this.durationMillis = durationMillis;
    this.volume = volume;
  }

  /**
   * Returns where the file lies in the assets.
   *
   * @return the internal path of the sound file
   */
  public String getPath() {
    return path;
  }

  /**
   * Returns how long the sound lasts, measured from its file. Must change with the file.
   *
   * @return the length of the sound, in milliseconds
   */
  public long getDurationMillis() {
    return durationMillis;
  }

  /**
   * Returns how loud the effect plays, tuned by ear per file.
   *
   * @return the volume, above 0 and at most 1
   */
  public float getVolume() {
    return volume;
  }
}
