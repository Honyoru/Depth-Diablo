package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.epitech.depthdiablo.entity.Enemy;
import com.epitech.depthdiablo.entity.Entity;
import com.epitech.depthdiablo.entity.ItemDrop;
import com.epitech.depthdiablo.entity.Projectile;
import java.util.Objects;

/**
 * The normal view: the Tiled map, then the dying enemies, the items on the ground, the living
 * enemies, the projectiles, and the player's sprite on top of them.
 */
final class SpriteRenderer implements Renderer {
  private final OrthogonalTiledMapRenderer mapRenderer;
  private final Batch batch;
  private final PlayerView playerView;
  private final EnemyView enemyView;
  private final ItemView itemView;
  private final ProjectileView projectileView;
  private final HitPreviewView hitPreview;

  /**
   * Creates the sprite renderer. It borrows its tools: the game screen still owns and disposes
   * them.
   *
   * @param mapRenderer draws the Tiled map
   * @param batch the shared sprite batch
   * @param playerView draws the player's animation
   * @param enemyView draws the enemies' animation
   * @param itemView draws the items on the ground
   * @param projectileView draws the projectiles in flight
   * @param hitPreview draws where the player's weapon hurts, under the sprites
   */
  SpriteRenderer(
      OrthogonalTiledMapRenderer mapRenderer,
      Batch batch,
      PlayerView playerView,
      EnemyView enemyView,
      ItemView itemView,
      ProjectileView projectileView,
      HitPreviewView hitPreview) {
    this.mapRenderer = Objects.requireNonNull(mapRenderer, "The map renderer must not be null");
    this.batch = Objects.requireNonNull(batch, "The sprite batch must not be null");
    this.playerView = Objects.requireNonNull(playerView, "The player view must not be null");
    this.enemyView = Objects.requireNonNull(enemyView, "The enemy view must not be null");
    this.itemView = Objects.requireNonNull(itemView, "itemView is null");
    this.projectileView = Objects.requireNonNull(projectileView, "projectileView is null");
    this.hitPreview = Objects.requireNonNull(hitPreview, "hitPreview is null");
  }

  @Override
  public void render(Frame frame) {
    mapRenderer.setView(frame.camera());
    mapRenderer.render();
    hitPreview.draw(frame); // on the floor, under every sprite; outside the batch

    batch.setProjectionMatrix(frame.camera().combined);
    batch.begin();
    enemyView.advance(frame.delta());
    enemyView.drawCorpses(batch); // under the living enemies
    for (Entity entity : frame.world().getEntities()) {
      if (entity instanceof ItemDrop drop && !drop.isRemoved()) {
        itemView.draw(batch, drop);
      }
    }
    for (Entity entity : frame.world().getEntities()) {
      if (entity instanceof Enemy enemy) {
        enemyView.draw(batch, enemy);
      }
    }
    for (Entity entity : frame.world().getEntities()) {
      if (entity instanceof Projectile projectile && !projectile.isRemoved()) {
        projectileView.draw(batch, projectile);
      }
    }
    playerView.draw(batch, frame.player(), frame.playerDrawPosition(), frame.delta());
    batch.end();
  }

  /** Does nothing: the map renderer, batch and player view belong to the game screen. */
  @Override
  public void dispose() {
    // Borrowed tools, disposed by their owner.
  }
}
