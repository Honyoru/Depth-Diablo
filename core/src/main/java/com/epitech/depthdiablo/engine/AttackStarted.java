package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Character;
import java.util.Objects;

/**
 * A character starts an attack: a swing, a shot or a bite (GDD 1.3.1). Published once per attack,
 * whatever it hits, so the desktop plays one attack sound for a swing that hits three rats (GDD
 * 4.2.2). The weapon is not carried: passive items may wrap the attack, so the desktop reads it
 * from the attacker's inventory.
 *
 * @param source the character that attacks
 */
public record AttackStarted(Character source) implements GameEvent {

  /**
   * Checks the value.
   *
   * @throws NullPointerException if the source is null
   */
  public AttackStarted {
    Objects.requireNonNull(source, "source is null");
  }

  /**
   * Returns the character that attacks.
   *
   * @return the source
   */
  @Override
  public Character source() {
    return source;
  }
}
