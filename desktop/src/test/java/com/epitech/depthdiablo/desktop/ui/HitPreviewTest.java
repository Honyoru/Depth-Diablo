package com.epitech.depthdiablo.desktop.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.desktop.ui.HitPreview.Fan;
import com.epitech.depthdiablo.desktop.ui.HitPreview.Ray;
import com.epitech.depthdiablo.item.ItemFactory;
import com.epitech.depthdiablo.item.MeleeWeapon;
import com.epitech.depthdiablo.item.RangedWeapon;
import org.junit.jupiter.api.Test;

/** GDD 1.5: the player sees the area a swing has swept, and how far a ranged weapon shoots. */
class HitPreviewTest {
  private static final String ITEMS =
      """
      { "items": [
        { "id": "sword", "class": "Sword", "nameKey": "item.sword.name",
          "spriteId": "items/swords/short_sword", "rarity": "COMMON", "price": 30,
          "attackBonus": 2, "attackSpeed": 1, "range": 24, "width": 8, "arcDegrees": 90,
          "swingTicks": 12 },
        { "id": "short_bow", "class": "ShortBow", "nameKey": "item.short_bow.name",
          "spriteId": "items/bows/short_bow", "rarity": "COMMON", "price": 20,
          "attackBonus": 1, "attackSpeed": 1, "range": 240, "projectileSpeed": 480,
          "projectileSpriteId": "projectiles/arrow" }
      ] }
      """;
  private final ItemFactory items = new ItemFactory(new DataRegistry("items", () -> ITEMS));

  @Test
  void should_cover_only_what_the_blade_swept_so_far() {
    MeleeWeapon sword = (MeleeWeapon) items.create("sword");

    Fan swept = HitPreview.sweptBy(sword, 10f, 20f, 90f, 0.5f);

    assertEquals(new Fan(10f, 20f, 24f, 45f, 45f), swept); // from 45° to 90°
  }

  @Test
  void should_reach_as_far_as_a_ranged_weapon_shoots_towards_the_aim() {
    RangedWeapon bow = (RangedWeapon) items.create("short_bow");

    Ray line = HitPreview.rangeOf(bow, 0f, 0f, 90f);

    assertEquals(0f, line.toX(), 0.001f);
    assertEquals(240f, line.toY(), 0.001f);
  }
}
