package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.entity.Player;
import com.epitech.depthdiablo.entity.Stat;
import com.epitech.depthdiablo.item.Inventory;
import java.util.Locale;
import java.util.Objects;

/**
 * The inventory page (GDD 1.4), drawn while the game is paused: the weapon, the armor, the stats
 * and the consumables on the left, the 18 passive slots on the right. It shows the player's
 * inventory as it is now, read again every frame.
 */
final class InventoryPage implements Disposable {
  private static final float SLOT = 40f;
  private static final float PASSIVE_SLOT = 52f;
  private static final float BIG_SLOT = 56f;

  /** The HUD font is 32 px, sized for the HUD; the page draws it smaller. */
  private static final float TEXT_SCALE = 0.5f;

  private static final float TITLE_SCALE = 0.65f;
  private static final float ICON_RATIO = 0.7f;
  private static final int GRID_COLUMNS = 6;
  private static final String[] STAT_NAMES = {
    "HP", "Attack", "Defense", "Speed", "Attack speed", "Luck"
  };

  private final Stage stage;
  private final ItemTextures textures;
  private final SlotView weapon;
  private final SlotView armor;
  private final SlotView[] hotbar = new SlotView[Inventory.HOTBAR_SIZE];
  private final SlotView[] passives = new SlotView[Inventory.PASSIVE_SIZE];
  private final Label[] statValues = new Label[STAT_NAMES.length];

  /**
   * Builds the page once; {@link #refresh} only changes its pictures and texts.
   *
   * @param skin the HUD skin, for the font, the slot frame and the page background
   * @param batch the shared sprite batch
   * @param textures the item pictures; the game screen owns and disposes them
   */
  InventoryPage(Skin skin, Batch batch, ItemTextures textures) {
    Objects.requireNonNull(skin, "skin is null");
    this.textures = Objects.requireNonNull(textures, "textures is null");
    this.stage =
        new Stage(
            new FitViewport(GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT),
            Objects.requireNonNull(batch, "batch is null"));
    this.weapon = new SlotView(skin, BIG_SLOT);
    this.armor = new SlotView(skin, BIG_SLOT);
    for (int i = 0; i < hotbar.length; i++) {
      hotbar[i] = new SlotView(skin, SLOT);
    }
    for (int i = 0; i < passives.length; i++) {
      passives[i] = new SlotView(skin, PASSIVE_SLOT);
    }

    Table page = new Table(skin);
    page.setBackground("inventory_bg");
    page.setSize(0.75f * GameConstants.VIRTUAL_WIDTH, 0.75f * GameConstants.VIRTUAL_HEIGHT);
    page.setPosition(
        (GameConstants.VIRTUAL_WIDTH - page.getWidth()) / 2f,
        (GameConstants.VIRTUAL_HEIGHT - page.getHeight()) / 2f);
    page.pad(16f).top();
    page.add(label("Inventory", skin, TITLE_SCALE)).colspan(3).padBottom(8f).row();
    page.add(leftColumn(skin)).top().expand().fill();
    page.add(new Image(skin.getDrawable("gold_line"))).width(2f).growY().padLeft(14f).padRight(14f);
    page.add(rightColumn()).top().expand().fill();
    stage.addActor(page);
  }

  /** The weapon and the armor, the stats, then the consumables. */
  private Table leftColumn(Skin skin) {
    Table column = new Table(skin);
    column.top();
    Table equipment = new Table(skin);
    equipment.add(weapon.cell).size(BIG_SLOT).padLeft(12f).padRight(12f);
    equipment.add(armor.cell).size(BIG_SLOT).padLeft(12f).padRight(12f).row();
    equipment.add(label("Weapon", skin, TEXT_SCALE)).padTop(2f);
    equipment.add(label("Armor", skin, TEXT_SCALE)).padTop(2f);
    column.add(equipment).row();

    column.add(label("Stats", skin, TITLE_SCALE)).padTop(8f).row();
    Table stats = new Table(skin);
    for (int i = 0; i < STAT_NAMES.length; i++) {
      statValues[i] = label("", skin, TEXT_SCALE);
      stats.add(label(STAT_NAMES[i], skin, TEXT_SCALE)).left().expandX();
      stats.add(statValues[i]).right().row();
    }
    column.add(stats).growX().row();

    column.add(label("Consumables", skin, TITLE_SCALE)).padTop(8f).row();
    Table consumables = new Table(skin);
    for (SlotView slot : hotbar) {
      consumables.add(slot.cell).size(SLOT).pad(3f);
    }
    column.add(consumables);
    return column;
  }

  /** The 18 passive slots, 3 rows of 6. */
  private Table rightColumn() {
    Table column = new Table();
    column.top();
    Table grid = new Table();
    for (int i = 0; i < passives.length; i++) {
      grid.add(passives[i].cell).size(PASSIVE_SLOT).pad(5f);
      if ((i + 1) % GRID_COLUMNS == 0) {
        grid.row();
      }
    }
    column.add(grid).row();
    return column;
  }

  /** A label in the HUD font, drawn at the given scale. */
  private static Label label(String text, Skin skin, float scale) {
    Label label = new Label(text, skin);
    label.setFontScale(scale);
    return label;
  }

  /**
   * Reads the player's inventory and stats again; a slot picture changes only when its item does.
   *
   * @param player the player whose inventory is shown
   */
  void refresh(Player player) {
    Inventory inventory = player.getInventory();
    weapon.show(inventory.getWeapon().getSpriteId());
    armor.show(inventory.getArmor().getSpriteId());
    for (int i = 0; i < hotbar.length; i++) {
      hotbar[i].show(inventory.hotbarSlot(i).getSpriteId());
    }
    for (int i = 0; i < passives.length; i++) {
      passives[i].show(inventory.passiveSlot(i).getSpriteId());
    }
    statValues[0].setText(player.getHp() + "/" + player.getMaxHp());
    statValues[1].setText(String.valueOf(player.getAttack()));
    statValues[2].setText(String.valueOf(player.getDefense()));
    statValues[3].setText(String.format(Locale.ROOT, "%.0f", player.getSpeed()));
    statValues[4].setText(String.format(Locale.ROOT, "%.2f", player.getAttackSpeed()));
    statValues[5].setText(
        String.format(Locale.ROOT, "%.0f", player.getBuild().stats().get(Stat.LUCK)));
  }

  /**
   * Draws the page.
   *
   * @param delta frame time in seconds
   */
  void render(float delta) {
    stage.act(delta);
    stage.draw();
  }

  /**
   * Updates the page viewport when the window is resized.
   *
   * @param width new window width in pixels
   * @param height new window height in pixels
   */
  void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  /**
   * Returns the stage of the page, to listen to its clicks.
   *
   * @return its stage
   */
  Stage getStage() {
    return stage;
  }

  @Override
  public void dispose() {
    stage.dispose();
  }

  /** One slot: the hotbar frame with the picture of its item over it. */
  private final class SlotView {
    private final Stack cell;
    private final Image icon = new Image();
    private String shownSpriteId;

    SlotView(Skin skin, float size) {
      Image frame = new Image(skin.getDrawable("hotbar_slot"));
      Table iconCell = new Table();
      iconCell.add(icon).size(size * ICON_RATIO);
      iconCell.setTouchable(Touchable.disabled);
      cell = new Stack(frame, iconCell);
    }

    /** Shows the picture of a sprite id, changing it only when it changes. */
    void show(String spriteId) {
      if (!spriteId.equals(shownSpriteId)) {
        icon.setDrawable(new TextureRegionDrawable(textures.get(spriteId)));
        shownSpriteId = spriteId;
      }
    }
  }
}
