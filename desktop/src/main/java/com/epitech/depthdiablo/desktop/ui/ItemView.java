package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.ItemDrop;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Draws the items lying on the ground (GDD 1.4) from the {@code spriteId} of their item. Each
 * picture is loaded once and kept until the view is disposed.
 */
final class ItemView implements Disposable {
  private static final Logger LOG = LoggerFactory.getLogger(ItemView.class);

  /** What an unknown picture falls back to: a transparent square. */
  private static final String MISSING_SPRITE = "items/empty";

  private final Map<String, Texture> textures = new HashMap<>();

  /**
   * Draws an item on the ground over its box.
   *
   * @param batch the batch, already begun
   * @param drop the item on the ground
   */
  void draw(Batch batch, ItemDrop drop) {
    Aabb box = drop.getBodyBox();
    batch.draw(
        textureOf(drop.getItem().getSpriteId()), box.x(), box.y(), box.width(), box.height());
  }

  /** Returns the picture of a sprite id, loading it the first time it is asked for. */
  private Texture textureOf(String spriteId) {
    return textures.computeIfAbsent(spriteId, ItemView::load);
  }

  private static Texture load(String spriteId) {
    FileHandle file = Gdx.files.internal("sprites/" + spriteId + ".png");
    if (file.exists()) {
      return new Texture(file);
    }
    LOG.warn("No picture for sprite {}; drawn as {}", spriteId, MISSING_SPRITE);
    return new Texture(Gdx.files.internal("sprites/" + MISSING_SPRITE + ".png"));
  }

  @Override
  public void dispose() {
    textures.values().forEach(Texture::dispose);
    textures.clear();
  }
}
