/// <reference types="@mapeditor/tiled-api" />
/*
 * generate-floors.js - writes many Endless floors in one go with the "Generate BSP Floor"
 * extension (bsp-floor.js, in Tiled's extensions folder), then fixes each floor for the game:
 *
 *   - the player spawn: bsp-floor.js paints tile 150 on the "Map" layer, but GameScreen reads the
 *     player spawn on the "spawn" layer only. The tile becomes plain floor and the spawn marker
 *     (tile 244, spawn = true) goes on the "spawn" layer.
 *   - the teleporter: bsp-floor.js puts one 128 px waypoints.tsx tile, a picture with no property.
 *     It is not placed; the 4 x 4 stele of waypoint1.tsx goes on the "spawner_boss" layer at the
 *     same spot, with its tp tiles (the teleport zone) and its spawn_boss tile.
 *
 * Floors are written to assets/ as Floor<first>.tmx, Floor<first + 1>.tmx, ... An existing file
 * is never replaced unless "replace" is given. The seed of each floor is saved as its bsp_seed
 * property, like the editor action does.
 *
 * Usage, Tiled under Wine (run from anywhere):
 *   cd ~/.wine/drive_c/Program\ Files/Tiled
 *   WINEPREFIX=~/.wine wine tiled.exe --evaluate Z:<repo>/tools/tiled/generate-floors.js 7 200
 * Arguments: first floor number (default 7), how many floors (default 200), "replace" (optional).
 */
(function () {
  "use strict";

  const ARENA = {
    FLOOR: 31, // 32 floor_center
    PLAYER_SPAWN_FROM_BSP: 150, // 151 floor_inner_top_left, what bsp-floor.js paints
    SPAWN_MARKER: 244, // 245 spawn = true, the marker the hand-made floors use
  };
  const STELE_SIZE = 4; // waypoint1.tsx: 4 x 4 tiles of 32 px

  const args = tiled.scriptArguments;
  const first = parseInt(args[0] || "7", 10);
  const count = parseInt(args[1] || "200", 10);
  const replace = args[2] === "replace";
  const assets = FileInfo.cleanPath(FileInfo.path(__filename) + "/../../assets");

  function fail(message) {
    tiled.error("[floors] " + message);
    throw new Error(message);
  }

  function findTileset(map, name) {
    for (const ts of map.tilesets) if (ts.name === name) return ts;
    return null;
  }

  function layerNamed(map, name) {
    for (let i = 0; i < map.layerCount; i++) if (map.layerAt(i).name === name) return map.layerAt(i);
    return null;
  }

  /** Moves the player spawn to the "spawn" layer; returns where it is. */
  function moveSpawn(map, arena) {
    const ground = map.layerAt(0);
    const spawn = layerNamed(map, "spawn");
    let found = null;
    for (let y = 0; y < ground.height && !found; y++) {
      for (let x = 0; x < ground.width; x++) {
        const tile = ground.tileAt(x, y);
        if (tile && tile.tileset === arena && tile.id === ARENA.PLAYER_SPAWN_FROM_BSP) {
          found = { x: x, y: y };
          break;
        }
      }
    }
    if (!found) fail("no player spawn painted by bsp-floor.js");
    const ge = ground.edit();
    ge.setTile(found.x, found.y, arena.tile(ARENA.FLOOR), 0);
    ge.apply();
    const se = spawn.edit();
    se.setTile(found.x, found.y, arena.tile(ARENA.SPAWN_MARKER), 0);
    se.apply();
    return found;
  }

  /**
   * Puts the 4 x 4 stele where bsp-floor.js puts its waypoint: centred near the bottom of the boss
   * room, the largest room, shifted half a tile when the room's width is odd.
   */
  function placeStele(map, rooms, stele) {
    let boss = rooms[0];
    for (const r of rooms) if (r.w * r.h > boss.w * boss.h) boss = r;
    const left = boss.x + (boss.w >> 1) - 2;
    const top = boss.y + boss.h - 1 - STELE_SIZE * 2 + 1; // the rows the 128 px waypoint covered
    const layer = layerNamed(map, "spawner_boss");
    const be = layer.edit();
    for (let k = 0; k < STELE_SIZE * STELE_SIZE; k++) {
      be.setTile(left + (k % STELE_SIZE), top + Math.floor(k / STELE_SIZE), stele.tile(k), 0);
    }
    be.apply();
    if (boss.w % 2 === 1) layer.offset = Qt.point(16, 0);
  }

  /** Counts the tiles of a map whose property is true, on every tile layer. */
  function countProperty(map, property) {
    let n = 0;
    for (let i = 0; i < map.layerCount; i++) {
      const layer = map.layerAt(i);
      if (!layer.isTileLayer) continue;
      for (let y = 0; y < layer.height; y++)
        for (let x = 0; x < layer.width; x++) {
          const tile = layer.tileAt(x, y);
          if (tile && tile.property(property) === true) n++;
        }
    }
    return n;
  }

  if (typeof DepthDiabloBsp === "undefined") fail("bsp-floor.js is not in Tiled's extensions folder");
  if (!(first >= 1) || !(count >= 1)) fail("usage: generate-floors.js <first floor> <count> [replace]");

  // The hand-made Floor1 gives the tilesets, so the floors point to tiles/*.tsx like it does.
  const source = tiled.mapFormat("tmx").read(assets + "/Floor1.tmx");
  const arena = findTileset(source, "arena");
  const stele = findTileset(source, "waypoint1");
  if (!arena || !stele) fail("Floor1.tmx must use tiles/arena.tsx and tiles/waypoint1.tsx");

  const format = tiled.mapFormat("tmx");
  for (let floor = first; floor < first + count; floor++) {
    const path = assets + "/Floor" + floor + ".tmx";
    if (File.exists(path) && !replace) fail("Floor" + floor + ".tmx exists; add \"replace\" to overwrite");

    const result = DepthDiabloBsp.generate({}, arena, null); // null: no waypoints.tsx picture
    result.map.addTileset(stele);
    const spawn = moveSpawn(result.map, arena);
    placeStele(result.map, result.rooms, stele);

    const spawns = countProperty(result.map, "spawn");
    const tp = countProperty(result.map, "tp");
    if (spawns !== 1 || tp !== 4) fail("Floor" + floor + ": " + spawns + " spawn(s), " + tp + " tp tile(s)");

    const error = format.write(result.map, path);
    if (error) fail("could not write " + path + ": " + error);
    tiled.log("[floors] Floor" + floor + ".tmx: " + result.report + ", spawn " + spawn.x + "," + spawn.y);
  }
  tiled.log("[floors] done: Floor" + first + " to Floor" + (first + count - 1));
})();
