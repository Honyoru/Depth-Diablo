package com.epitech.depthdiablo.desktop.ui;

/**
 * Two passive slots to swap, asked by a Switch on the inventory page.
 *
 * @param from the slot of the item being switched
 * @param to the slot clicked after Switch
 */
public record PassiveSwap(int from, int to) {}
