package com.epitech.depthdiablo.item;

/**
 * The sword: the Knight's melee weapon (GDD 1.2, 1.6). Its blade sweeps a quarter turn around the
 * aim. Its values are placeholders until balancing (DECISIONS §5).
 */
public final class Sword extends MeleeWeapon {
  private static final int PRICE = 30;
  private static final int ATTACK_BONUS = 2;
  private static final float RANGE = 24f;
  private static final float WIDTH = 8f;
  private static final float ARC_DEGREES = 90f;
  private static final int SWING_TICKS = 12;

  /** Creates the sword with its placeholder values. */
  public Sword() {
    super(
        "sword",
        "item.sword.name",
        "items/sword",
        Rarity.COMMON,
        PRICE,
        ATTACK_BONUS,
        RANGE,
        WIDTH,
        ARC_DEGREES,
        SWING_TICKS);
  }
}
