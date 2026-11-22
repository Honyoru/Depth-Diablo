package com.epitech.depthdiablo.entity.enemy;

import com.epitech.depthdiablo.combat.ContactAttack;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.character.Character;
import java.util.Objects;
import java.util.Optional;

/**
 * The enemies that hit at contact: Rat, Zombie, Orc (GDD 1.3.1). They attack with their {@link
 * ContactAttack}, aimed at their target's centre: the attack decides the area, around or in front,
 * and the damage formula (GDD 1.6).
 */
public abstract class MeleeEnemy extends Enemy {

  private final ContactAttack attack;

  /**
   * Creates a melee enemy.
   *
   * @param setup the values every enemy shares; must not be {@code null}
   * @param attack its contact attack, which holds its reach; it is also its base attack; must not
   *     be {@code null}
   * @throws NullPointerException if {@code setup} or {@code attack} is {@code null}
   */
  protected MeleeEnemy(EnemySetup setup, ContactAttack attack) {
    super(setup, attack);
    this.attack = Objects.requireNonNull(attack, "attack is null");
  }

  /**
   * Returns how far this enemy's attack goes past its body box.
   *
   * @return the reach in pixels, 0 or more
   */
  public float getReach() {
    return attack.getReach();
  }

  /**
   * Attacks the target with its contact attack, aimed at the target's centre, if it still sees it
   * and its attack touches it. The attack starts the cooldown and hits through the build (GDD
   * 1.3.1, 1.6).
   *
   * @param world the world the enemy lives in
   */
  @Override
  protected void attackTarget(World world) {
    Optional<Character> target = getTarget().filter(this::canSee).filter(this::canReach);
    if (target.isPresent()) {
      startAttack(attack, aimAt(target.get()), world);
    }
  }

  /**
   * Tells whether its contact attack, aimed at the target's centre, touches the target's hurtbox.
   *
   * @param target the character to reach
   * @return true if an attack now would touch it
   */
  @Override
  protected boolean canReach(Character target) {
    return attack.touches(getBodyBox(), aimAt(target), target.getHurtbox());
  }
}
