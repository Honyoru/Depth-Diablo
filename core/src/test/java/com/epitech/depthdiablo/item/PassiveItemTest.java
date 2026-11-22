package com.epitech.depthdiablo.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epitech.depthdiablo.combat.Attack;
import com.epitech.depthdiablo.combat.HitResult;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Character;
import com.epitech.depthdiablo.entity.stats.CharacterBuild;
import com.epitech.depthdiablo.entity.stats.StatSource;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * GDD 1.3.1 and 1.4: every passive has the PASSIVE rarity, modifies the build, and changes nothing
 * by default.
 */
class PassiveItemTest {

  @Test
  void should_have_the_passive_rarity_when_created() {
    PassiveItem passive = new PlainPassive("ring", "item.ring.name", "items/ring", 10);

    Rarity rarity = passive.getRarity();

    assertEquals(Rarity.PASSIVE, rarity);
  }

  @Test
  void should_keep_its_values_when_created() {
    PassiveItem passive = new PlainPassive("ring", "item.ring.name", "items/ring", 10);

    assertEquals("ring", passive.getId());
    assertEquals("item.ring.name", passive.getNameKey());
    assertEquals("items/ring", passive.getSpriteId());
    assertEquals(10, passive.getPrice());
  }

  @Test
  void should_throw_when_a_text_is_null() {
    assertThrows(
        NullPointerException.class,
        () -> new PlainPassive(null, "item.ring.name", "items/ring", 10));
    assertThrows(
        NullPointerException.class, () -> new PlainPassive("ring", null, "items/ring", 10));
    assertThrows(
        NullPointerException.class, () -> new PlainPassive("ring", "item.ring.name", null, 10));
  }

  @Test
  void should_throw_when_the_price_is_negative() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PlainPassive("ring", "item.ring.name", "items/ring", -1));
  }

  @Test
  void should_leave_the_stats_unchanged_by_default() {
    PassiveItem passive = new PlainPassive("ring", "item.ring.name", "items/ring", 10);
    StatSource stats = stat -> 1f;

    StatSource decorated = passive.decorateStats(stats);

    assertSame(stats, decorated);
  }

  @Test
  void should_leave_the_attack_unchanged_by_default() {
    PassiveItem passive = new PlainPassive("ring", "item.ring.name", "items/ring", 10);
    Attack attack = new NoAttack();

    Attack decorated = passive.decorateAttack(attack);

    assertSame(attack, decorated);
  }

  @Test
  void should_throw_when_the_stats_to_decorate_are_null() {
    PassiveItem passive = new PlainPassive("ring", "item.ring.name", "items/ring", 10);

    assertThrows(NullPointerException.class, () -> passive.decorateStats(null));
  }

  @Test
  void should_throw_when_the_attack_to_decorate_is_null() {
    PassiveItem passive = new PlainPassive("ring", "item.ring.name", "items/ring", 10);

    assertThrows(NullPointerException.class, () -> passive.decorateAttack(null));
  }

  @Test
  void should_keep_the_attack_and_the_stats_of_the_build_by_default() {
    PassiveItem passive = new PlainPassive("ring", "item.ring.name", "items/ring", 10);
    Attack attack = new NoAttack();
    StatSource stats = stat -> 1f;

    CharacterBuild modified = passive.modify(new CharacterBuild(attack, stats));

    assertSame(attack, modified.attack());
    assertSame(stats, modified.stats());
  }

  @Test
  void should_throw_when_the_build_is_null() {
    PassiveItem passive = new PlainPassive("ring", "item.ring.name", "items/ring", 10);

    assertThrows(NullPointerException.class, () -> passive.modify(null));
  }

  /** Hand-written fake (Q117): a passive item with no effect of its own. */
  private static final class PlainPassive extends PassiveItem {
    PlainPassive(String id, String nameKey, String spriteId, int price) {
      super(id, nameKey, spriteId, price);
    }

    @Override
    public List<ItemEffect> getEffects() {
      return List.of();
    }
  }

  /** Hand-written fake (Q117): an attack that does nothing. */
  private static final class NoAttack implements Attack {
    @Override
    public HitResult hit(Character attacker, Character target) {
      return HitResult.NONE;
    }

    @Override
    public void attack(Character owner, float aimDegrees, World world) {
      // Never started in these tests.
    }
  }
}
