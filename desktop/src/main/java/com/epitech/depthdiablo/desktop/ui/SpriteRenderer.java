package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import java.util.Objects;

/** The normal view: the Tiled map, then the player's sprite on top of it. */
final class SpriteRenderer implements Renderer {
  private final OrthogonalTiledMapRenderer mapRenderer;
  private final Batch batch;
  private final PlayerView playerView;

  /**
   * Creates the sprite renderer. It borrows its tools: the game screen still owns and disposes
   * them.
   *
   * @param mapRenderer draws the Tiled map
   * @param batch the shared sprite batch
   * @param playerView draws the player's animation
   */
  SpriteRenderer(OrthogonalTiledMapRenderer mapRenderer, Batch batch, PlayerView playerView) {
    this.mapRenderer = Objects.requireNonNull(mapRenderer, "The map renderer must not be null");
    this.batch = Objects.requireNonNull(batch, "The sprite batch must not be null");
    this.playerView = Objects.requireNonNull(playerView, "The player view must not be null");
  }

  @Override
  public void render(Frame frame) {
    mapRenderer.setView(frame.camera());
    mapRenderer.render();

    batch.setProjectionMatrix(frame.camera().combined);
    batch.begin();
    playerView.draw(batch, frame.player(), frame.playerDrawPosition(), frame.delta());
    batch.end();
  }

  /** Does nothing: the map renderer, batch and player view belong to the game screen. */
  @Override
  public void dispose() {
    // Borrowed tools, disposed by their owner.
  }
}
