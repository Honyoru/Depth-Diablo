package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.ItemDrop;
import java.util.Objects;

/** Draws the items lying on the ground (GDD 1.4) from the {@code spriteId} of their item. */
final class ItemView {
  private final ItemTextures textures;

  /**
   * Creates the view.
   *
   * @param textures the item pictures; the game screen owns and disposes them
   */
  ItemView(ItemTextures textures) {
    this.textures = Objects.requireNonNull(textures, "textures is null");
  }

  /**
   * Draws an item on the ground over its box.
   *
   * @param batch the batch, already begun
   * @param drop the item on the ground
   */
  void draw(Batch batch, ItemDrop drop) {
    Aabb box = drop.getBodyBox();
    batch.draw(
        textures.get(drop.getItem().getSpriteId()), box.x(), box.y(), box.width(), box.height());
  }
}
