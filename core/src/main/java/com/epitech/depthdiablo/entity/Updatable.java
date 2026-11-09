package com.epitech.depthdiablo.entity;

/** Something the worl update by logic tick. (Q42) */
public interface Updatable {
  /**
   * Avance once by tick
   *
   * @param tick nuber of curent tick
   */
  void update(long tick);
}
