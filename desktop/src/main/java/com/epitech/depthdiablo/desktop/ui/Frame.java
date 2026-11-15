package com.epitech.depthdiablo.desktop.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.engine.World;
import com.epitech.depthdiablo.entity.Player;

/**
 * Everything a {@link Renderer} needs to draw one frame. Renderers only read it: drawing never
 * changes the game state (GDD 1.1.2, Q67).
 *
 * @param camera the world camera, already centred on the player
 * @param world the world to draw
 * @param player the player, drawn at {@code playerDrawPosition} instead of its logic position
 * @param playerDrawPosition where to draw the bottom-left corner of the player's body box, between
 *     the last two ticks so the movement looks smooth
 * @param playerVelocity how far the player moved during the last tick, in pixels
 * @param delta frame time in seconds, for animations
 */
record Frame(
    OrthographicCamera camera,
    World world,
    Player player,
    Vector2 playerDrawPosition,
    Vector2 playerVelocity,
    float delta) {}
