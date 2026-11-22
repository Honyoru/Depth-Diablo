package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.epitech.depthdiablo.combat.Burn;
import com.epitech.depthdiablo.combat.Poison;
import com.epitech.depthdiablo.engine.event.DamageDealt;
import com.epitech.depthdiablo.engine.event.EntityDied;
import com.epitech.depthdiablo.engine.event.EventListener;
import com.epitech.depthdiablo.engine.event.GameEvent;
import com.epitech.depthdiablo.entity.enemy.Enemy;
import com.epitech.depthdiablo.entity.enemy.EnemyState;
import com.epitech.depthdiablo.geometry.Aabb;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Draws every enemy with its own animations, found from its sprite id: {@code
 * sprites/<spriteId>_<animation>.png}, 32 x 32 frames in one row. The animation follows its state
 * (idle, move, attack, spawn); a hit plays its hit animation, and a death leaves a corpse that
 * plays its death animation once. A missing sheet falls back to idle. An enemy bigger than a frame,
 * like the king, is drawn bigger. It learns about hits and deaths from the world's events, so
 * drawing never changes the game state.
 */
final class EnemyView implements EventListener, Disposable {
  private static final int FRAME_SIZE = 32;
  private static final float FRAME_SECONDS = 0.15f;
  private static final float HIT_FRAME_SECONDS = 0.1f;
  private static final float FRAME_MARGIN = 2;

  /** Tint of a burning enemy. */
  private static final Color BURN_TINT = new Color(1f, 0.45f, 0.35f, 1f);

  /** Tint of a poisoned enemy. */
  private static final Color POISON_TINT = new Color(0.45f, 1f, 0.45f, 1f);

  /** The animations of each sprite id, loaded the first time an enemy of that sprite is drawn. */
  private final Map<String, Sheets> sheets = new HashMap<>();

  /** When each enemy was last hit, on this view's clock. */
  private final Map<Enemy, Float> lastHit = new HashMap<>();

  private final List<Corpse> corpses = new ArrayList<>();
  private float stateTime;

  /**
   * Moves the animation clock forward. Call it once per frame, not once per enemy, or the enemies
   * animate too fast.
   *
   * @param delta frame time in seconds
   */
  void advance(float delta) {
    stateTime += delta;
  }

  /**
   * Draws the current frame of one living enemy, mirrored when its target is on its left. The batch
   * must have begun, with the world camera's projection.
   *
   * @param batch the batch to draw with
   * @param enemy the enemy to draw
   */
  void draw(Batch batch, Enemy enemy) {
    Sheets enemySheets = sheetsOf(enemy.getSpriteId());
    Float hitAt = lastHit.get(enemy);
    TextureRegion frame;
    if (hitAt != null && !enemySheets.hit.isAnimationFinished(stateTime - hitAt)) {
      frame = enemySheets.hit.getKeyFrame(stateTime - hitAt);
    } else {
      frame = enemySheets.of(enemy.getState()).getKeyFrame(stateTime);
    }
    Color tint = tintOf(enemy);
    batch.setColor(tint);
    drawFrame(batch, frame, enemy.getBodyBox(), looksLeft(enemy));
    batch.setColor(Color.WHITE);
  }

  /**
   * Draws the enemies that died lately, each playing its death animation once, then forgets them.
   * The batch must have begun, with the world camera's projection.
   *
   * @param batch the batch to draw with
   */
  void drawCorpses(Batch batch) {
    Iterator<Corpse> iterator = corpses.iterator();
    while (iterator.hasNext()) {
      Corpse corpse = iterator.next();
      Animation<TextureRegion> death = sheetsOf(corpse.spriteId()).death;
      float age = stateTime - corpse.diedAt();
      if (death.isAnimationFinished(age)) {
        iterator.remove();
      } else {
        drawFrame(batch, death.getKeyFrame(age), corpse.body(), corpse.lookingLeft());
      }
    }
  }

  /**
   * Remembers the enemy hit by a {@link DamageDealt} and the corpse of an {@link EntityDied}.
   *
   * @param event an event of the world
   */
  @Override
  public void onEvent(GameEvent event) {
    if (event instanceof DamageDealt damage && damage.target() instanceof Enemy enemy) {
      lastHit.put(enemy, stateTime);
    } else if (event instanceof EntityDied death && death.source() instanceof Enemy enemy) {
      lastHit.remove(enemy);
      corpses.add(new Corpse(enemy.getSpriteId(), enemy.getBodyBox(), looksLeft(enemy), stateTime));
    }
  }

  private Sheets sheetsOf(String spriteId) {
    return sheets.computeIfAbsent(spriteId, Sheets::new);
  }

  /** True when the enemy's target is on its left. */
  private static boolean looksLeft(Enemy enemy) {
    return enemy.getTarget().map(t -> t.getBodyBox().x() < enemy.getBodyBox().x()).orElse(false);
  }

  /** Draws a frame centred on the body, at least a frame wide, standing on the body's bottom. */
  private static void drawFrame(Batch batch, TextureRegion frame, Aabb body, boolean lookingLeft) {
    float size = Math.max(FRAME_SIZE, body.width() + 2 * FRAME_MARGIN);
    float left = body.x() + (body.width() - size) / 2f;
    float bottom = body.y() - FRAME_MARGIN;
    if (lookingLeft) {
      batch.draw(frame, left + size, bottom, -size, size);
    } else {
      batch.draw(frame, left, bottom, size, size);
    }
  }

  @Override
  public void dispose() {
    sheets.values().forEach(Sheets::dispose);
    sheets.clear();
  }

  /** A dead enemy still drawn: what it looked like when it died. */
  private record Corpse(String spriteId, Aabb body, boolean lookingLeft, float diedAt) {}

  /** The animations of one sprite id, and the textures they cut their frames from. */
  private static final class Sheets implements Disposable {
    private final List<Texture> textures = new ArrayList<>();
    private final Animation<TextureRegion> idle;
    private final Animation<TextureRegion> move;
    private final Animation<TextureRegion> attack;
    private final Animation<TextureRegion> spawn;
    private final Animation<TextureRegion> hit;
    private final Animation<TextureRegion> death;

    Sheets(String spriteId) {
      this.idle =
          load(spriteId, "idle", FRAME_SECONDS, Animation.PlayMode.LOOP)
              .orElseThrow(() ->
                  new IllegalStateException(
                      "No idle sheet for " + spriteId + ": " + path(spriteId, "idle")));
      this.move = load(spriteId, "move", FRAME_SECONDS, Animation.PlayMode.LOOP).orElse(idle);
      this.attack = load(spriteId, "attack", FRAME_SECONDS, Animation.PlayMode.LOOP).orElse(idle);
      this.spawn = load(spriteId, "spawn", FRAME_SECONDS, Animation.PlayMode.LOOP).orElse(idle);
      this.hit = load(spriteId, "hit", HIT_FRAME_SECONDS, Animation.PlayMode.NORMAL).orElse(idle);
      this.death = load(spriteId, "death", FRAME_SECONDS, Animation.PlayMode.NORMAL).orElse(idle);
    }

    /** The animation of a state: moving while it chases, keeps its distance or flees. */
    Animation<TextureRegion> of(EnemyState state) {
      return switch (state) {
        case CHASE, KEEP_DISTANCE, FLEE -> move;
        case ATTACK -> attack;
        case SUMMON -> spawn;
        case HIT -> hit;
        case IDLE, DEAD -> idle;
      };
    }

    private Optional<Animation<TextureRegion>> load(
        String spriteId, String name, float frameSeconds, Animation.PlayMode mode) {
      FileHandle file = Gdx.files.internal(path(spriteId, name));
      if (!file.exists()) {
        return Optional.empty();
      }
      Texture sheet = new Texture(file);
      textures.add(sheet);
      Animation<TextureRegion> animation =
          new Animation<>(frameSeconds, TextureRegion.split(sheet, FRAME_SIZE, FRAME_SIZE)[0]);
      animation.setPlayMode(mode);
      return Optional.of(animation);
    }

    private static String path(String spriteId, String name) {
      return "sprites/" + spriteId + "_" + name + ".png";
    }

    @Override
    public void dispose() {
      textures.forEach(Texture::dispose);
    }
  }

  /** Poison wins over burn when both are active; no effect keeps the sprite's own colours. */
  private static Color tintOf(Enemy enemy) {
    if (enemy.hasStatus(Poison.class)) {
      return POISON_TINT;
    }
    if (enemy.hasStatus(Burn.class)) {
      return BURN_TINT;
    }
    return Color.WHITE;
  }
}
