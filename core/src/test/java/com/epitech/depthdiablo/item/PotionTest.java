package com.epitech.depthdiablo.item;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.entity.Character;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1 and 1.4: a potion restores HP when used from the hotbar. */
class PotionTest {

  @Test
  void should_keep_its_heal_amount_when_created() {
    Potion potion = new Potion("potion_small", "item.potion_small.name", "potion", 10, 5);

    int healAmount = potion.getHealAmount();

    assertEquals(5, healAmount);
  }

  @Test
  void should_have_the_consumable_rarity_when_created() {
    Potion potion = new Potion("potion_small", "item.potion_small.name", "potion", 10, 5);

    Rarity rarity = potion.getRarity();

    assertEquals(Rarity.CONSUMABLE, rarity);
  }

  @Test
  void should_throw_when_the_heal_amount_is_below_1() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Potion("potion_small", "item.potion_small.name", "potion", 10, 0));
  }

  @Test
  void should_heal_the_user_when_used() {
    Character user = aCharacter().withHp(10).build();
    user.takeDamage(6);
    Potion potion = new Potion("potion_small", "item.potion_small.name", "potion", 10, 4);

    potion.use(user);

    assertEquals(8, user.getHp());
  }

  @Test
  void should_be_spent_when_used() {
    Character user = aCharacter().withHp(10).build();
    user.takeDamage(6);
    Potion potion = new Potion("potion_small", "item.potion_small.name", "potion", 10, 4);

    boolean spent = potion.use(user);

    assertTrue(spent);
  }

  @Test
  void should_be_spent_when_the_user_is_at_full_hp() {
    Character user = aCharacter().withHp(10).build();
    Potion potion = new Potion("potion_small", "item.potion_small.name", "potion", 10, 4);

    boolean spent = potion.use(user);

    assertTrue(spent);
    assertEquals(10, user.getHp());
  }

  @Test
  void should_throw_when_the_user_is_null() {
    Potion potion = new Potion("potion_small", "item.potion_small.name", "potion", 10, 4);

    assertThrows(NullPointerException.class, () -> potion.use(null));
  }
}
