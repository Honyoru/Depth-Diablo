package com.epitech.depthdiablo.combat;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.engine.event.DamageDealt;
import com.epitech.depthdiablo.engine.event.GameEvent;
import com.epitech.depthdiablo.entity.character.Character;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** GDD 1.3.1: a poison deals its damage every interval until its duration is over, like a burn. */
class PoisonTest {
  private final Character source = aCharacter().build();

  @Test
  void should_be_a_damage_over_time() {
    assertInstanceOf(DamageOverTime.class, new Poison(new DotSpec(1, 2, 4), source));
  }

  @Test
  void should_deal_its_damage_once_per_interval_over_its_whole_duration() {
    Character target = aCharacter().withHp(10).build();
    Poison poison = new Poison(new DotSpec(1, 2, 4), source);

    for (int i = 0; i < 4; i++) {
      poison.tick(target);
    }

    assertEquals(8, target.getHp());
    assertTrue(poison.isOver());
  }

  @Test
  void should_deal_damage_when_the_target_is_invincible() {
    Character target = aCharacter().withHp(10).build();
    target.grantInvincibility(100);
    Poison poison = new Poison(new DotSpec(3, 1, 1), source);

    poison.tick(target);

    assertEquals(7, target.getHp());
  }

  @Test
  void should_credit_its_source_for_the_damage() {
    World world = new World();
    Character target = aCharacter().withHp(10).withWorld(world).build();
    List<GameEvent> events = new ArrayList<>();
    world.getEventBus().subscribe(DamageDealt.class, events::add);
    Poison poison = new Poison(new DotSpec(2, 1, 1), source);

    poison.tick(target);
    world.getEventBus().flush();

    assertEquals(List.of(new DamageDealt(source, target, 2)), events);
  }
}
