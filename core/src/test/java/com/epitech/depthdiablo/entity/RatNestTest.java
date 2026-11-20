package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static com.epitech.depthdiablo.testdata.EnemyBuilder.aRat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.World;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * The rat nest (GDD 1.3, 1.7): a static summoner that spawns rats while it sees its target, at its
 * attack speed, never more than its cap alive. Its rats are summoned (Q16).
 */
class RatNestTest {
  /** One summon per second at attack speed 1: 60 ticks between two summons. */
  private static final int TICKS_BETWEEN_SUMMONS = 60;

  private static final Aabb NEST_BODY = new Aabb(100, 100, 28, 24);
  private final World world = new World();

  /** Every rat the fake summoner built, in order. */
  private final List<Enemy> summoned = new ArrayList<>();

  /** The centre each summon was asked for. */
  private final List<Vector2> centres = new ArrayList<>();

  /** Builds a summoned rat on the asked centre, like the CharacterFactory would. */
  private final Summoner summoner =
      (id, centre) -> {
        centres.add(centre.cpy());
        Enemy rat = aRat().summoned().at(centre.x - 8, centre.y - 8).withWorld(world).build();
        summoned.add(rat);
        return rat;
      };

  private final Character player = aCharacter().withFaction(Faction.PLAYER).build();

  @Test
  void should_stay_idle_and_summon_nothing_without_a_target() {
    RatNest nest = aNest(2);
    world.add(nest);

    ticks(100);

    assertEquals(EnemyState.IDLE, nest.getState());
    assertTrue(summoned.isEmpty());
  }

  @Test
  void should_switch_to_summon_when_it_sees_its_target() {
    RatNest nest = alertedNest(2);

    ticks(1);

    assertEquals(EnemyState.SUMMON, nest.getState());
  }

  @Test
  void should_summon_one_rat_on_its_first_summoning_tick() {
    alertedNest(2);

    ticks(2); // tick 1: IDLE -> SUMMON; tick 2: the first summon

    assertEquals(1, summoned.size());
  }

  @Test
  void should_summon_on_its_centre() {
    alertedNest(2);

    ticks(2);

    assertEquals(new Vector2(114, 112), centres.get(0));
  }

  @Test
  void should_add_its_summon_to_the_world_alerted_to_its_target() {
    alertedNest(2);

    ticks(2);

    Enemy rat = summoned.get(0);
    assertTrue(world.getEntities().contains(rat));
    assertSame(player, rat.getTarget().orElseThrow());
    assertTrue(rat.isSummoned());
  }

  @Test
  void should_wait_its_cooldown_before_the_next_summon() {
    alertedNest(2);
    ticks(2);

    ticks(TICKS_BETWEEN_SUMMONS - 1);
    int beforeTheCooldownEnds = summoned.size();
    ticks(1);

    assertEquals(1, beforeTheCooldownEnds);
    assertEquals(2, summoned.size());
  }

  @Test
  void should_never_keep_more_summons_alive_than_its_cap() {
    RatNest nest = alertedNest(2);

    ticks(2 + 5 * TICKS_BETWEEN_SUMMONS);

    assertEquals(2, summoned.size());
    assertEquals(2, nest.getLivingSummons());
  }

  @Test
  void should_summon_again_once_one_of_its_rats_is_dead() {
    RatNest nest = alertedNest(2);
    ticks(2 + TICKS_BETWEEN_SUMMONS); // two rats: the cap is reached
    Enemy first = summoned.get(0);
    first.takeDamage(first.getHp());
    int livingAfterTheDeath = nest.getLivingSummons();

    ticks(TICKS_BETWEEN_SUMMONS);

    assertEquals(1, livingAfterTheDeath);
    assertEquals(3, summoned.size());
    assertEquals(2, nest.getLivingSummons());
  }

  @Test
  void should_never_move() {
    RatNest nest = alertedNest(2);

    ticks(200);

    assertEquals(NEST_BODY, nest.getBodyBox());
  }

  @Test
  void should_go_idle_and_stop_summoning_when_its_target_dies() {
    RatNest nest = alertedNest(3);
    ticks(2);
    player.takeDamage(player.getHp());

    ticks(2 * TICKS_BETWEEN_SUMMONS);

    assertEquals(EnemyState.IDLE, nest.getState());
    assertEquals(1, summoned.size());
  }

  @Test
  void should_reach_any_target_it_sees_whatever_the_distance() {
    RatNest nest = aNest(1);
    Character far =
        aCharacter().withFaction(Faction.PLAYER).withBodyBox(new Aabb(5000, 5000, 16, 16)).build();

    assertTrue(nest.canReach(far));
  }

  @Test
  void should_take_its_sprite_id_from_its_setup() {
    assertEquals("enemies/rat_nest", aNest(1).getSpriteId());
  }

  @Test
  void should_reject_a_blank_summon_id() {
    assertThrows(IllegalArgumentException.class, () -> new RatNest(setup(), " ", 2, summoner));
  }

  @Test
  void should_reject_a_cap_below_1() {
    assertThrows(IllegalArgumentException.class, () -> new RatNest(setup(), "rat", 0, summoner));
  }

  @Test
  void should_reject_a_missing_summon_id_or_summoner() {
    assertThrows(NullPointerException.class, () -> new RatNest(setup(), null, 2, summoner));
    assertThrows(NullPointerException.class, () -> new RatNest(setup(), "rat", 2, null));
  }

  private RatNest alertedNest(int maxSummons) {
    RatNest nest = aNest(maxSummons);
    world.add(nest);
    nest.alert(player);
    return nest;
  }

  private RatNest aNest(int maxSummons) {
    return new RatNest(setup(), "rat", maxSummons, summoner);
  }

  /** A nest body with attack speed 1 (one summon per second) and speed 0. */
  private EnemySetup setup() {
    Map<Stat, Float> stats = new EnumMap<>(Stat.class);
    stats.put(Stat.MAX_HP, 25f);
    stats.put(Stat.ATTACK, 0f);
    stats.put(Stat.DEFENSE, 1f);
    stats.put(Stat.SPEED, 0f);
    stats.put(Stat.ATTACK_SPEED, 1f);
    stats.put(Stat.LUCK, 0f);
    return new EnemySetup(NEST_BODY, new BaseStats(stats), "enemies/rat_nest", 0, false, world);
  }

  private void ticks(int count) {
    for (int i = 0; i < count; i++) {
      world.tick();
    }
  }
}
