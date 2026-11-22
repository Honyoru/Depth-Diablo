package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The pictures of the sprites, one texture per {@code spriteId} ({@code sprites/<id>.png}), loaded
 * the first time they are asked for and freed on dispose. The items on the ground, the projectiles,
 * the HUD hotbar and the inventory page share it.
 */
final class SpriteTextures implements Disposable {
  private static final Logger LOG = LoggerFactory.getLogger(SpriteTextures.class);

  /** What an unknown picture falls back to: a transparent square. */
  private static final String MISSING_SPRITE = "items/empty";

  private final Map<String, Texture> textures = new HashMap<>();

  /**
   * Returns the picture of a sprite id.
   *
   * @param spriteId the sprite id, e.g. {@code "items/potion"} or {@code "projectiles/arrow"}
   * @return its texture; a transparent one if the file is missing
   */
  Texture get(String spriteId) {
    return textures.computeIfAbsent(spriteId, SpriteTextures::load);
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
