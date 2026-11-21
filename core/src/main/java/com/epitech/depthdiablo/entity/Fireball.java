package com.epitech.depthdiablo.entity;

import com.epitech.depthdiablo.combat.Burn;
import com.epitech.depthdiablo.combat.DotSpec;
import com.epitech.depthdiablo.engine.World;
import java.util.Objects;

/**
 * The shot of the Dark Mage (GDD 1.3.1): it burns the first character it hurts, then stops. A hit
 * that lands nothing, on a character rolling for example, does not burn either.
 */
public final class Fireball extends Projectile {
  private final DotSpec burn;

  /**
   * Creates a fireball. Its length along its flight is the width of its body box.
   *
   * @param bodyBox where it starts; must not be {@code null}
   * @param owner the character who cast it; the source of the burn
   * @param aimDegrees the direction it flies to, in degrees (0 = right, 90 = up)
   * @param speed how fast it flies, in pixels per second; above 0
   * @param lifetimeTicks how many ticks it flies before it is removed; above 0
   * @param burn the burn it leaves on the character it hurts, read from the data files (Q93)
   * @param world the world it flies in
   * @throws NullPointerException if {@code bodyBox}, {@code owner}, {@code burn} or {@code world}
   *     is {@code null}
   * @throws IllegalArgumentException if the aim or the speed is not a finite number, the speed is 0
   *     or less, or the lifetime is 0 or less
   */
  public Fireball(
      Aabb bodyBox,
      Character owner,
      float aimDegrees,
      float speed,
      int lifetimeTicks,
      DotSpec burn,
      World world) {
    super(bodyBox, owner, aimDegrees, speed, lifetimeTicks, world);
    this.burn = Objects.requireNonNull(burn, "The burn of a fireball must not be null");
  }

  /**
   * Burns the character it hurt, then stops: it does not go through it.
   *
   * @param target the character it just hurt
   */
  @Override
  protected void onHit(Character target) {
    target.applyStatus(new Burn(burn, getOwner()));
    remove();
  }
}
