package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.epitech.depthdiablo.combat.Swing;
import com.epitech.depthdiablo.entity.Player;
import java.util.Optional;

/**
 * Draws the player: idle, walking or attacking animation on its body box, mirrored when it looks
 * left.
 */
final class PlayerView implements Disposable {
  private static final float FRAME_SECONDS = 0.15f;

  /** How many times a second the sprite pulses blue while the player charges a teleport zone. */
  private static final float PULSES_PER_SECOND = 2f;

  /** The size one frame is drawn at, in world pixels. */
  private final float drawSize;

  /** The empty space under the feet in a drawn frame, so the feet touch the body box bottom. */
  private final float footMargin;

  private final Texture idleSheet;
  private final Texture moveSheet;
  private final Texture attackSheet;
  private final Animation<TextureRegion> idle;
  private final Animation<TextureRegion> move;
  private final Animation<TextureRegion> attack;
  private float stateTime;
  private boolean lookingLeft;
  private boolean pulsingBlue;
  private final Color tint = new Color();

  /**
   * Returns the view of a hero, from its id in {@code data/characters.json}.
   *
   * @param characterId e.g. {@code "knight"}
   * @return its view; the knight's for an unknown id
   */
  static PlayerView of(String characterId) {
    return switch (characterId) {
      case "assassin" ->
          new PlayerView(
              "sprites/player/assassin/assassin_idle_east.png",
              "sprites/player/assassin/assassin_move_east.png",
              "sprites/player/assassin/assassin_attack_east.png",
              256,
              54, // 167 px tall in 256 px frames -> 35 px, the knight's height
              9); // 42 empty px under the feet, at 54/256
      default ->
          new PlayerView(
              "sprites/player/knight_idle.png",
              "sprites/player/knight_move.png",
              "sprites/player/knight_attack.png",
              64,
              64,
              2);
    };
  }

  private PlayerView(
      String idlePath,
      String movePath,
      String attackPath,
      int frameSize,
      float drawSize,
      float footMargin) {
    this.drawSize = drawSize;
    this.footMargin = footMargin;
    this.idleSheet = new Texture(Gdx.files.internal(idlePath));
    this.moveSheet = new Texture(Gdx.files.internal(movePath));
    this.attackSheet = new Texture(Gdx.files.internal(attackPath));
    this.idle = animation(idleSheet, frameSize);
    this.move = animation(moveSheet, frameSize);
    this.attack = animation(attackSheet, frameSize);
  }

  private static Animation<TextureRegion> animation(Texture sheet, int frameSize) {
    TextureRegion[] frames = TextureRegion.split(sheet, frameSize, frameSize)[0];
    Animation<TextureRegion> animation = new Animation<>(FRAME_SECONDS, frames);
    animation.setPlayMode(Animation.PlayMode.LOOP);
    return animation;
  }

  /**
   * Turns the sprite towards the horizontal direction of the input. 0 keeps the current side.
   *
   * @param x negative for left, positive for right
   */
  void lookTowards(float x) {
    if (x != 0f) {
      lookingLeft = x < 0f;
    }
  }

  /**
   * Makes the sprite pulse blue or stops it, such as while the player stands in a teleport zone.
   *
   * @param on true to pulse blue, false for the normal colours
   */
  void pulseBlue(boolean on) {
    pulsingBlue = on;
  }

  /**
   * Draws the current frame. The batch must have begun, with the world camera's projection.
   *
   * @param batch the batch to draw with
   * @param player the player to draw
   * @param position where to draw the bottom-left corner of its body box
   * @param delta frame time in seconds
   */
  void draw(Batch batch, Player player, Vector2 position, float delta) {
    stateTime += delta;
    Optional<Swing> swing = player.getCurrentSwing();
    TextureRegion frame;
    if (swing.isPresent()) {
      lookTowardsAim(swing.get().getAimDegrees());
      frame = attackFrame(swing.get().getProgress());
    } else {
      frame = (player.isMoving() ? move : idle).getKeyFrame(stateTime);
    }
    float left = position.x + (player.getBodyBox().width() - drawSize) / 2f;
    float bottom = position.y - footMargin;
    if (pulsingBlue) {
      float strength = 0.5f + 0.5f * MathUtils.sin(stateTime * MathUtils.PI2 * PULSES_PER_SECOND);
      batch.setColor(tint.set(1f - 0.6f * strength, 1f - 0.35f * strength, 1f, 1f));
    }
    if (lookingLeft) {
      batch.draw(frame, left + drawSize, bottom, -drawSize, drawSize);
    } else {
      batch.draw(frame, left, bottom, drawSize, drawSize);
    }
    batch.setColor(Color.WHITE);
  }

  /** Picks the attack frame from how far the swing is, so the sprite follows the real blade. */
  private TextureRegion attackFrame(float progress) {
    TextureRegion[] frames = attack.getKeyFrames();
    int index = Math.min(frames.length - 1, (int) (progress * frames.length));
    return frames[index];
  }

  /** Turns the sprite towards the side of the aim: left between 90 and 270 degrees. */
  private void lookTowardsAim(float aimDegrees) {
    lookingLeft = aimDegrees > 90f && aimDegrees < 270f;
  }

  @Override
  public void dispose() {
    idleSheet.dispose();
    moveSheet.dispose();
    attackSheet.dispose();
  }
}
