package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.epitech.depthdiablo.entity.Player;

/** Draws the player: idle or walking animation on its body box, mirrored when it looks left. */
final class PlayerView implements Disposable {
  private static final int FRAME_SIZE = 32;
  private static final float FRAME_SECONDS = 0.15f;
  private static final float FRAME_MARGIN = 2;
  private final Texture idleSheet;
  private final Texture moveSheet;
  private final Animation<TextureRegion> idle;
  private final Animation<TextureRegion> move;
  private float stateTime;
  private boolean lookingLeft;

  PlayerView() {
    this.idleSheet = new Texture(Gdx.files.internal("sprites/player/knight_idle.png"));
    this.moveSheet = new Texture(Gdx.files.internal("sprites/player/knight_move.png"));
    this.idle = animation(idleSheet);
    this.move = animation(moveSheet);
  }

  private static Animation<TextureRegion> animation(Texture sheet) {
    TextureRegion[] frames = TextureRegion.split(sheet, FRAME_SIZE, FRAME_SIZE)[0];
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
   * Draws the current frame. The batch must have begun, with the world camera's projection.
   *
   * @param batch the batch to draw with
   * @param player the player to draw
   * @param position where to draw the bottom-left corner of its body box
   * @param delta frame time in seconds
   */
  void draw(Batch batch, Player player, Vector2 position, float delta) {
    stateTime += delta;
    TextureRegion frame = (player.isMoving() ? move : idle).getKeyFrame(stateTime);
    float left = position.x + (player.bodyBox().width() - FRAME_SIZE) / 2f;
    float bottom = position.y - FRAME_MARGIN;
    if (lookingLeft) {
      batch.draw(frame, left + FRAME_SIZE, bottom, -FRAME_SIZE, FRAME_SIZE);
    } else {
      batch.draw(frame, left, bottom, FRAME_SIZE, FRAME_SIZE);
    }
  }

  @Override
  public void dispose() {
    idleSheet.dispose();
    moveSheet.dispose();
  }
}
