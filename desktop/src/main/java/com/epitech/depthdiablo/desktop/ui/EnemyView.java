package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.epitech.depthdiablo.entity.Enemy;
import com.epitech.depthdiablo.entity.EnemyState;

/**
 * Draws every enemy with the knight's animations, tinted red until the enemies get their own
 * sprites: idle or walking on its body box, mirrored when its target is on its left. One view draws
 * them all, so it keeps no state per enemy.
 */
final class EnemyView implements Disposable {
  private static final int FRAME_SIZE = 32;
  private static final float FRAME_SECONDS = 0.15f;
  private static final float FRAME_MARGIN = 2;
  private final Texture idleSheet;
  private final Texture moveSheet;
  private final Animation<TextureRegion> idle;
  private final Animation<TextureRegion> move;
  private float stateTime;

  /** Multiplies the knight's colours, so an enemy doesn't look like the player. */
  private static final Color TINT = new Color(1f, 0.4f, 0.4f, 1f);

  EnemyView() {
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
   * Moves the animation clock forward. Call it once per frame, not once per enemy, or the enemies
   * animate too fast.
   *
   * @param delta frame time in seconds
   */
  void advance(float delta) {
    stateTime += delta;
  }

  /**
   * Draws the current frame of one enemy: walking while it chases, idle otherwise. The batch must
   * have begun, with the world camera's projection. The batch colour is white again afterwards.
   *
   * @param batch the batch to draw with
   * @param enemy the enemy to draw
   */
  void draw(Batch batch, Enemy enemy) {
    float left = enemy.getBodyBox().x() + (enemy.getBodyBox().width() - FRAME_SIZE) / 2f;
    float bottom = enemy.getBodyBox().y() - FRAME_MARGIN;
    boolean lookingLeft =
        enemy.getTarget().map(t -> t.getBodyBox().x() < enemy.getBodyBox().x()).orElse(false);
    batch.setColor(TINT);
    batch.draw(
        (enemy.getState() == EnemyState.CHASE ? move : idle).getKeyFrame(stateTime),
        lookingLeft ? left + FRAME_SIZE : left,
        bottom,
        lookingLeft ? (float) -FRAME_SIZE : (float) FRAME_SIZE,
        FRAME_SIZE);
    batch.setColor(Color.WHITE);
  }

  @Override
  public void dispose() {
    idleSheet.dispose();
    moveSheet.dispose();
  }
}
