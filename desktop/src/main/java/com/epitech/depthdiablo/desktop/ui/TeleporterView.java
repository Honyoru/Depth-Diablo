package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.TeleportZone;

/** Draws the teleport zone once it is open: a glowing pad, tinted blue, centred on the zone. */
final class TeleporterView implements Disposable {
  private static final String SHEET = "sprites/environemen/waypoints/waypoint2_white_idle.png";

  /** One frame of the pad: three tiles by three. */
  private static final int FRAME_SIZE = 96;

  private static final float FRAME_SECONDS = 0.12f;

  private static final Color BLUE = new Color(0.55f, 0.75f, 1f, 1f);

  private final Texture sheet;
  private final Animation<TextureRegion> idle;
  private float stateTime;

  TeleporterView() {
    this.sheet = new Texture(Gdx.files.internal(SHEET));
    this.idle =
        new Animation<>(FRAME_SECONDS, TextureRegion.split(sheet, FRAME_SIZE, FRAME_SIZE)[0]);
    this.idle.setPlayMode(Animation.PlayMode.LOOP);
  }

  /**
   * Moves the animation on. Called once per frame, before the zones are drawn.
   *
   * @param delta frame time in seconds
   */
  void advance(float delta) {
    stateTime += delta;
  }

  /**
   * Draws the pad on the zone while it is open; nothing while it is closed. The batch must have
   * begun, with the world camera's projection.
   *
   * @param batch the batch to draw with
   * @param zone the zone to draw
   */
  void draw(Batch batch, TeleportZone zone) {
    if (!zone.isOpen() || zone.isRemoved()) {
      return;
    }
    Aabb box = zone.getBodyBox();
    float left = box.x() + (box.width() - FRAME_SIZE) / 2f;
    float bottom = box.y() + (box.height() - FRAME_SIZE) / 2f;
    batch.setColor(BLUE);
    batch.draw(idle.getKeyFrame(stateTime), left, bottom, FRAME_SIZE, FRAME_SIZE);
    batch.setColor(Color.WHITE);
  }

  @Override
  public void dispose() {
    sheet.dispose();
  }
}
