package com.epitech.depthdiablo.engine;

import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Entity;
import java.util.Optional;

/**
 * A character hit another one.
 *
 * @param source the attacker
 * @param target the character that took the damage
 * @param amount the HP the target lost
 */
public record DamageDealt(Character source, Character target, int amount) implements GameEvent {
  @Override
  public Character source() {
    return source;
  }

  /**
   * Returns the character that took the damage.
   *
   * @return the target, always present
   */
  @Override
  public Optional<Entity> receiver() {
    return Optional.of(target);
  }
}
