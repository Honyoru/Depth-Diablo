package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.entity.Player;
import com.epitech.depthdiablo.entity.Stat;
import com.epitech.depthdiablo.item.Inventory;
import com.epitech.depthdiablo.item.Item;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The inventory page (GDD 1.4), drawn while the game is paused: the weapon, the armor, the stats
 * and the consumables on the left, the 18 passive slots on the right. It shows the player's
 * inventory as it is now, read again every frame. A click selects an item and shows what it gives
 * under the grid, with Drop and, for a passive item, Switch; the page only asks the game screen to
 * drop or swap, it never changes the inventory itself.
 */
final class InventoryPage implements Disposable {
  private static final float SLOT = 40f;
  private static final float PASSIVE_SLOT = 52f;
  private static final float BIG_SLOT = 56f;

  /** The height of the details card: fixed, so it never grows with the lines of an item. */
  private static final float DETAILS_HEIGHT = 130f;

  /** The HUD font is 32 px, sized for the HUD; the page draws it smaller. */
  private static final float TEXT_SCALE = 0.5f;

  private static final float TITLE_SCALE = 0.65f;
  private static final float ICON_RATIO = 0.7f;
  private static final int GRID_COLUMNS = 6;
  private static final String[] STAT_NAMES = {
    "HP", "Attack", "Defense", "Speed", "Attack speed", "Luck"
  };

  private final Stage stage;
  private final SpriteTextures textures;
  private final SlotView weapon;
  private final SlotView armor;
  private final SlotView[] hotbar = new SlotView[Inventory.HOTBAR_SIZE];
  private final SlotView[] passives = new SlotView[Inventory.PASSIVE_SIZE];
  private final Label[] statValues = new Label[STAT_NAMES.length];
  private final InventoryPageState state;
  private final Consumer<PageSlot> onDrop;
  private final Consumer<PassiveSwap> onSwap;
  private final Table details;

  /** What the details card shows; hidden when nothing is selected, the card stays in place. */
  private final Table detailsContent;

  private final Label itemName;
  private final Label itemEffects;
  private final Label switchHint;
  private final TextButton dropButton;
  private final TextButton switchButton;

  /** The inventory shown, read again on every refresh; null until the first refresh. */
  private Inventory shown;

  /**
   * Builds the page once; {@link #refresh} only changes its pictures and texts.
   *
   * @param skin the HUD skin, for the font, the slot frame and the page background
   * @param batch the shared sprite batch
   * @param textures the item pictures; the game screen owns and disposes them
   * @param state what is selected and whether a Switch is pending
   * @param onDrop asked to drop the item of a slot on the ground
   * @param onSwap asked to swap two passive slots
   */
  InventoryPage(
      Skin skin,
      Batch batch,
      SpriteTextures textures,
      InventoryPageState state,
      Consumer<PageSlot> onDrop,
      Consumer<PassiveSwap> onSwap) {
    Objects.requireNonNull(skin, "skin is null");
    this.textures = Objects.requireNonNull(textures, "textures is null");
    this.state = Objects.requireNonNull(state, "state is null");
    this.onDrop = Objects.requireNonNull(onDrop, "onDrop is null");
    this.onSwap = Objects.requireNonNull(onSwap, "onSwap is null");
    this.stage =
        new Stage(
            new FitViewport(GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT),
            Objects.requireNonNull(batch, "batch is null"));
    this.weapon = new SlotView(skin, BIG_SLOT, new PageSlot(PageSlot.Kind.WEAPON, 0));
    this.armor = new SlotView(skin, BIG_SLOT, new PageSlot(PageSlot.Kind.ARMOR, 0));
    for (int i = 0; i < hotbar.length; i++) {
      hotbar[i] = new SlotView(skin, SLOT, new PageSlot(PageSlot.Kind.HOTBAR, i));
    }
    for (int i = 0; i < passives.length; i++) {
      passives[i] = new SlotView(skin, PASSIVE_SLOT, new PageSlot(PageSlot.Kind.PASSIVE, i));
    }
    this.itemName = label("", skin, TITLE_SCALE);
    this.itemEffects = label("", skin, TEXT_SCALE);
    this.switchHint = label("Click a passive slot", skin, TEXT_SCALE);
    this.dropButton = button("Drop", skin, () -> state.getSelected().ifPresent(this.onDrop));
    this.switchButton = button("Switch", skin, state::toggleSwitch);
    this.detailsContent = new Table(skin);
    this.details = detailsPanel(skin);

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
    column.add(details).padTop(10f).growX().height(DETAILS_HEIGHT);
    return column;
  }

  /**
   * The name and effects of the selected item, with its buttons; hidden when nothing is selected.
   */
  private Table detailsPanel(Skin skin) {
    detailsContent.top().left();
    detailsContent.add(itemName).left().row();
    detailsContent.add(itemEffects).left().padTop(2f).row();
    Table buttons = new Table(skin);
    buttons.add(dropButton).padRight(8f);
    buttons.add(switchButton);
    detailsContent.add(buttons).left().padTop(6f).row();
    detailsContent.add(switchHint).left().padTop(4f);
    detailsContent.setVisible(false);
    Table panel = new Table(skin);
    panel.setBackground("inventory_card_bg");
    panel.pad(8f, 10f, 8f, 10f);
    panel.top().left();
    panel.add(detailsContent).top().left().expand();
    return panel;
  }

  /** A button in the HUD style with its text drawn at the page size. */
  private static TextButton button(String text, Skin skin, Runnable action) {
    TextButton button = new TextButton(text, skin);
    button.getLabel().setFontScale(TEXT_SCALE);
    button.pad(4f, 10f, 4f, 10f);
    button.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            action.run();
          }
        });
    return button;
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
    shown = inventory;
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
    showSelection(inventory);
  }

  /** Highlights the selected slot and fills the details panel with its item. */
  private void showSelection(Inventory inventory) {
    PageSlot selected = state.getSelected().orElse(null);
    for (SlotView slot : allSlots()) {
      slot.highlight(slot.slot.equals(selected));
    }
    detailsContent.setVisible(selected != null);
    if (selected == null) {
      return;
    }
    Item item = itemIn(inventory, selected);
    itemName.setText(ItemEffectText.name(item.getId()));
    itemEffects.setText(String.join("\n", ItemEffectText.lines(item.getEffects())));
    boolean equipment =
        selected.kind() == PageSlot.Kind.WEAPON || selected.kind() == PageSlot.Kind.ARMOR;
    dropButton.setVisible(!equipment);
    switchButton.setVisible(selected.isPassive());
    switchButton.setText(state.isSwitching() ? "Cancel" : "Switch");
    switchHint.setVisible(state.isSwitching());
  }

  /** The item a slot of the page holds. */
  private static Item itemIn(Inventory inventory, PageSlot slot) {
    return switch (slot.kind()) {
      case WEAPON -> inventory.getWeapon();
      case ARMOR -> inventory.getArmor();
      case HOTBAR -> inventory.hotbarSlot(slot.index());
      case PASSIVE -> inventory.passiveSlot(slot.index());
    };
  }

  /** Tells whether a slot of the page holds no item; the weapon and the armor never are free. */
  private static boolean isFree(Inventory inventory, PageSlot slot) {
    return switch (slot.kind()) {
      case WEAPON, ARMOR -> false;
      case HOTBAR -> inventory.isHotbarSlotFree(slot.index());
      case PASSIVE -> inventory.isPassiveSlotFree(slot.index());
    };
  }

  private SlotView[] allSlots() {
    SlotView[] all = new SlotView[2 + hotbar.length + passives.length];
    all[0] = weapon;
    all[1] = armor;
    System.arraycopy(hotbar, 0, all, 2, hotbar.length);
    System.arraycopy(passives, 0, all, 2 + hotbar.length, passives.length);
    return all;
  }

  /** A click on a slot: the model selects it or completes a Switch, which the game screen does. */
  private void clicked(PageSlot slot) {
    if (shown == null) {
      return;
    }
    state.click(slot, isFree(shown, slot)).ifPresent(onSwap);
  }

  /**
   * Draws the page.
   *
   * @param delta frame time in seconds
   */
  void render(float delta) {
    stage.getViewport().apply(); // its 16:9 area, the one its clicks are read in
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

  /** One slot: the hotbar frame with the picture of its item over it; a click selects it. */
  private final class SlotView {
    private final Stack cell;
    private final Image frame;
    private final Image icon = new Image();
    private final PageSlot slot;
    private String shownSpriteId;

    SlotView(Skin skin, float size, PageSlot slot) {
      this.slot = slot;
      frame = new Image(skin.getDrawable("hotbar_slot"));
      Table iconCell = new Table();
      iconCell.add(icon).size(size * ICON_RATIO);
      iconCell.setTouchable(Touchable.disabled);
      cell = new Stack(frame, iconCell);
      cell.addListener(
          new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
              InventoryPage.this.clicked(slot);
            }
          });
    }

    /** Tints the frame gold while the slot is selected. */
    void highlight(boolean selected) {
      frame.setColor(selected ? Color.GOLD : Color.WHITE);
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
