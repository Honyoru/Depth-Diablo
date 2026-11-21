package com.epitech.depthdiablo.desktop.audio;

import com.epitech.depthdiablo.engine.AttackStarted;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.EventListener;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.engine.ItemDropped;
import com.epitech.depthdiablo.engine.ItemPickedUp;
import com.epitech.depthdiablo.engine.ShopOpened;
import com.epitech.depthdiablo.entity.Character;
import com.epitech.depthdiablo.entity.Player;
import com.epitech.depthdiablo.item.Bow;
import com.epitech.depthdiablo.item.MeleeWeapon;
import com.epitech.depthdiablo.item.Staff;
import com.epitech.depthdiablo.item.Weapon;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Turns game events into sound effects (GDD 4.2): one attack sound per attack, heard from the
 * attacker, and one damage sound per character hit, heard from the one hit.
 *
 * <p>Pattern: Observer (observer), like the enemy view. No default branch: a new event type stops
 * this compiling until it is given a sound, or none.
 */
public final class SoundEvents implements EventListener {

  /** The attack sound of each weapon family; the nearest family of a weapon's class wins. */
  private static final Map<Class<?>, SoundEffect> ATTACK_SOUNDS =
      Map.of(
          MeleeWeapon.class, SoundEffect.ATTACK_MELEE,
          Bow.class, SoundEffect.ATTACK_BOW,
          Staff.class, SoundEffect.ATTACK_STAFF);

  private final GameAudio audio;

  /**
   * Creates the listener.
   *
   * @param audio where the sounds play
   * @throws NullPointerException if the audio is null
   */
  public SoundEvents(GameAudio audio) {
    this.audio = Objects.requireNonNull(audio, "audio is null");
  }

  @Override
  public void onEvent(GameEvent event) {
    switch (event) {
      case AttackStarted attack ->
          attackSoundOf(attack.source()).ifPresent(sound -> audio.play(sound, attack.source()));
      case DamageDealt damage -> audio.play(SoundEffect.DAMAGE, damage.target());
      case EntityDied death -> audio.play(SoundEffect.DEATH, death.source());
      case ItemPickedUp pickup -> audio.play(SoundEffect.PICKUP, pickup.source());
      case ItemDropped drop -> {} // silent
      case ShopOpened shop -> {} // silent
    }
  }

  /** A player sounds like its weapon's family; every enemy has the enemy attack sound. */
  static Optional<SoundEffect> attackSoundOf(Character attacker) {
    if (attacker instanceof Player player) {
      return familySoundOf(player.getInventory().getWeapon());
    }
    return Optional.of(SoundEffect.ATTACK_ENEMY);
  }

  /** Walks up from the weapon's class to the first family that has a sound. */
  static Optional<SoundEffect> familySoundOf(Weapon weapon) {
    for (Class<?> type = weapon.getClass(); type != null; type = type.getSuperclass()) {
      SoundEffect sound = ATTACK_SOUNDS.get(type);
      if (sound != null) {
        return Optional.of(sound);
      }
    }
    return Optional.empty();
  }
}
