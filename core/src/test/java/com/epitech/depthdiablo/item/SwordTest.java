package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.epitech.depthdiablo.testdata.Items;
import org.junit.jupiter.api.Test;

/** GDD 1.2, 1.6: the sword of the data file is a melee weapon. */
class SwordTest {

  @Test
  void should_be_a_melee_weapon() {
    assertInstanceOf(MeleeWeapon.class, Items.sword());
  }
}
