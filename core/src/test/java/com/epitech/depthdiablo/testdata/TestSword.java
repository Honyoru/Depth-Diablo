package com.epitech.depthdiablo.testdata;

import com.epitech.depthdiablo.item.MeleeWeapon;
import com.epitech.depthdiablo.item.Rarity;

/**
 * A real melee weapon with fixed numbers, for tests that must not follow the balance of items.json.
 */
public final class TestSword extends MeleeWeapon {
  /** The length of the blade, in pixels. */
  public static final float RANGE = 24f;

  /** The width of the blade, in pixels. */
  public static final float WIDTH = 8f;

  /** Builds the test sword: attack bonus 2, attack speed 1, reach 24, width 8, arc 90, 12 ticks. */
  public TestSword() {
    super(
        "test_sword",
        "item.sword.name",
        "items/sword",
        Rarity.COMMON,
        30,
        2,
        1f,
        RANGE,
        WIDTH,
        90f,
        12);
  }
}
