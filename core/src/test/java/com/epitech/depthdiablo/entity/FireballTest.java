package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.DotSpec;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.World;
import org.junit.jupiter.api.Test;

/** The fireball (GDD 1.3.1): the Dark Mage's shot, which burns the character it hurts. */
class FireballTest {
  private static final float TEN_PIXELS_PER_TICK = 10f * GameConstants.TICKS_PER_SECOND;
  private static final int LIFETIME_TICKS = 30;
  private static final DotSpec BURN = new DotSpec(2, 1, 3);

  private final World world = new World();

  /** Casts every fireball; on the ENEMY side, like the Dark Mage. */
  private final Character owner = aCharacter().withWorld(world).build();

  private Fireball fireballAt(float x, float y) {
    Fireball fireball =
        new Fireball(
            new Aabb(x, y, 16, 16),
            owner,
            0,
            TEN_PIXELS_PER_TICK,
            LIFETIME_TICKS,
            "projectiles/fireball",
            BURN,
            world);
    world.add(fireball);
    return fireball;
  }

  private Character playerAt(float x, float y, int invincibilityTicks) {
    Aabb box = new Aabb(x, y, 16, 16);
    Character player =
        aCharacter()
            .withBodyBox(box)
            .withHurtbox(box)
            .withFaction(Faction.PLAYER)
            .withInvincibilityAfterHit(invincibilityTicks)
            .withWorld(world)
            .build();
    world.add(player);
    return player;
  }

  @Test
  void should_stop_when_it_hurts_a_character() {
    Fireball fireball = fireballAt(100, 100);
    playerAt(115, 96, 0);

    world.tick();

    assertTrue(fireball.isRemoved());
  }

  @Test
  void should_burn_the_character_it_hurts() {
    fireballAt(100, 100);
    Character player = playerAt(115, 96, 0);
    world.tick();
    int hpAfterTheHit = player.getHp();

    world.tick();

    assertTrue(player.getHp() < hpAfterTheHit);
  }

  @Test
  void should_not_burn_a_character_while_it_is_invincible() {
    fireballAt(100, 100);
    Character dodging = playerAt(115, 96, 60);
    dodging.takeDamage(1);
    world.tick();

    world.tick();

    assertEquals(9, dodging.getHp());
  }

  @Test
  void should_reject_a_null_burn() {
    Aabb body = new Aabb(0, 0, 16, 16);

    assertThrows(
        NullPointerException.class,
        () ->
            new Fireball(
                body,
                owner,
                0,
                TEN_PIXELS_PER_TICK,
                LIFETIME_TICKS,
                "projectiles/fireball",
                null,
                world));
  }
}
