package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aRat;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aZombie;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.testdata.EnemyBuilder;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The contact attack of rats and zombies (GDD 1.3.1, 1.6, DECISIONS §5). */
class MeleeEnemyTest {
  private final World world = new World();
  private final List<GameEvent> events = new ArrayList<>();

  MeleeEnemyTest() {
    world.eventBus().subscribeAll(events::add);
  }

  @Test
  void should_hit_its_target_with_the_damage_formula_when_in_reach() {
    MeleeEnemy rat = aRat().build();
    Character player = aPlayerAt(18, 0);
    rat.alert(player)
  }

}
