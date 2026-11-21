package com.epitech.depthdiablo.shop;

import com.epitech.depthdiablo.arena.ArenaCycle;
import com.epitech.depthdiablo.engine.ShopOpened;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.entity.Interactable;
import com.epitech.depthdiablo.entity.Player;
import java.util.Objects;

/**
 * The shopkeeper, the only NPC (GDD 1.3, 1.4). In Arena mode he trades after each boss wave, while
 * the arena cycle is in its {@link ArenaCycle.Phase#SHOP} phase, and the player starts the next
 * round through him (GDD 1.2). The rest of the time he is away: not drawn, and talking to him does
 * nothing. He is an entity, not a character, so he never fights and can never be hit. Buying,
 * selling and the roulette come with the coins.
 */
public final class Shopkeeper extends Entity implements Interactable {
  private final ArenaCycle cycle;
  private final World world;

  /**
   * Puts the shopkeeper at his place; he trades only during the shop phase of the cycle.
   *
   * @param box where he stands, in pixels
   * @param cycle the arena cycle whose shop phase he trades in
   * @param world the world whose event bus hears that the shop opens
   * @throws NullPointerException if an argument is {@code null}
   */
  public Shopkeeper(Aabb box, ArenaCycle cycle, World world) {
    super(box);
    this.cycle = Objects.requireNonNull(cycle, "cycle is null");
    this.world = Objects.requireNonNull(world, "world is null");
  }

  /**
   * Tells whether he trades now: from the end of a boss wave until the player starts the next
   * round.
   *
   * @return true during the shop phase of the cycle
   */
  public boolean isOpen() {
    return cycle.getPhase() == ArenaCycle.Phase.SHOP;
  }

  /**
   * Opens the shop for a living player while he trades: publishes a {@link ShopOpened}, which opens
   * the shop page. Does nothing while he is away.
   *
   * @param player the player who talks to him
   * @throws NullPointerException if {@code player} is {@code null}
   */
  @Override
  public void interact(Player player) {
    Objects.requireNonNull(player, "player is null");
    if (isOpen() && !player.isDead()) {
      world.getEventBus().publish(new ShopOpened(player, this));
    }
  }

  /**
   * Ends the shop: the next arena cycle starts with its first wave, and he goes away until the next
   * boss wave is cleared (GDD 1.2).
   *
   * @return true if the next round started; false while he is away
   */
  public boolean startNextRound() {
    return cycle.startNextCycle();
  }

  @Override
  protected void onUpdate(long tick) {
    // The shopkeeper stands still.
  }
}
