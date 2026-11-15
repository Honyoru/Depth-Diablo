package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.utils.Disposable;

/**
 * One way to draw the world, picked with F1 (GDD 1.1.2). Pattern: Strategy. The game screen keeps
 * one renderer per {@link com.epitech.depthdiablo.debug.ViewMode} and calls the one the {@link
 * com.epitech.depthdiablo.debug.ViewSwitcher} chose.
 */
interface Renderer extends Disposable {

  /**
   * Draws one frame of the world with the frame's camera. Must not change the game state.
   *
   * @param frame what to draw
   */
  void render(Frame frame);
}
