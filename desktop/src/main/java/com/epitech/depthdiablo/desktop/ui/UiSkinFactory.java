package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/** Loads {@code ui/hud/uiskin.json} and injects fallback Label and TextButton styles. */
public final class UiSkinFactory {

  private static final String HUD_SKIN_PATH = "ui/hud/uiskin.json";

  private UiSkinFactory() {}

  /**
   * Loads the HUD skin and registers {@code default} Label and TextButton styles using {@code
   * newfont}.
   *
   * @return the configured {@link Skin}
   */
  public static Skin loadHudSkin() {
    Skin skin = new Skin(Gdx.files.internal(HUD_SKIN_PATH));
    BitmapFont font = skin.getFont("newfont");
    Color gold = skin.getColor("RGBA_224_206_89_255");
    String name = "default";

    if (!skin.has(name, LabelStyle.class)) {
      skin.add(name, new LabelStyle(font, gold));
    }

    if (!skin.has(name, TextButtonStyle.class)) {
      TextButtonStyle btnStyle = new TextButtonStyle();
      btnStyle.font = font;
      btnStyle.fontColor = gold;
      btnStyle.overFontColor = Color.WHITE;
      btnStyle.disabledFontColor = Color.GRAY;
      btnStyle.up = skin.getDrawable("hotbar_slot");
      btnStyle.down = skin.getDrawable("hotbar_slot");
      skin.add(name, btnStyle);
    }

    if (!skin.has("minimap_bg", TextureRegionDrawable.class)) {
      Pixmap pixmap = new Pixmap(96, 96, Pixmap.Format.RGBA8888);
      pixmap.setColor(0.08f, 0.08f, 0.12f, 0.85f);
      pixmap.fill();
      pixmap.setColor(gold);
      pixmap.drawRectangle(0, 0, 96, 96);
      Texture tex = new Texture(pixmap);
      pixmap.dispose();
      skin.add("minimap_tex", tex);
      skin.add("minimap_bg", new TextureRegionDrawable(tex));
    }

    if (!skin.has("inventory_bg", Drawable.class)) {
      Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
      pixmap.setColor(0.06f, 0.06f, 0.09f, 0.92f);
      pixmap.fill();
      pixmap.setColor(gold);
      pixmap.drawRectangle(0, 0, 16, 16);
      Texture tex = new Texture(pixmap);
      pixmap.dispose();
      skin.add("inventory_tex", tex);
      skin.add(
          "inventory_bg", new NinePatchDrawable(new NinePatch(tex, 1, 1, 1, 1)), Drawable.class);
    }

    if (!skin.has("inventory_card_bg", Drawable.class)) {
      Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
      pixmap.setColor(0.02f, 0.02f, 0.04f, 0.96f); // a little darker than the page
      pixmap.fill();
      pixmap.setColor(gold);
      pixmap.drawRectangle(0, 0, 16, 16);
      Texture tex = new Texture(pixmap);
      pixmap.dispose();
      skin.add("inventory_card_tex", tex);
      skin.add(
          "inventory_card_bg",
          new NinePatchDrawable(new NinePatch(tex, 1, 1, 1, 1)),
          Drawable.class);
    }

    if (!skin.has("gold_line", Drawable.class)) {
      Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
      pixmap.setColor(gold);
      pixmap.fill();
      Texture tex = new Texture(pixmap);
      pixmap.dispose();
      skin.add("gold_line_tex", tex);
      skin.add("gold_line", new TextureRegionDrawable(tex), Drawable.class);
    }
    return skin;
  }
}
