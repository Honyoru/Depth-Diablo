package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.Attack;
import java.util.Objects;

/**
 * What a character fights with right now (GDD 1.3.1): its attack and its stats, after its equipment
 * and passive items. It never changes: each {@code with...} method returns a new build. The
 * hurtbox, hitbox and movement parts of the item UML come when an item first needs them.
 *
 * @param attack the attack the character uses: its weapon, maybe wrapped by passive items
 * @param stats where its stats come from, maybe wrapped by passive items
 */
public record CharacterBuild(Attack attack, StatSource stats) {

  /**
   * Creates a build and checks it.
   *
   * @throws NullPointerException if {@code attack} or {@code stats} is {@code null}
   */
  public CharacterBuild {
    Objects.requireNonNull(attack, "attack is null");
    Objects.requireNonNull(stats, "stats is null");
  }

  /**
   * Returns the same build with another attack.
   *
   * @param newAttack the attack to use
   * @return a new build; this one is not changed
   * @throws NullPointerException if {@code newAttack} is {@code null}
   */
  public CharacterBuild withAttack(Attack newAttack) {
    return new CharacterBuild(newAttack, stats);
  }

  /**
   * Returns the same build with other stats.
   *
   * @param newStats the stats to use
   * @return a new build; this one is not changed
   * @throws NullPointerException if {@code newStats} is {@code null}
   */
  public CharacterBuild withStats(StatSource newStats) {
    return new CharacterBuild(attack, newStats);
  }
}
