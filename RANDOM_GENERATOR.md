# Random floor generator

Summary of the research on procedural floor generation (12 Nov). **Status: proposal.**
`DECISIONS.md` §13 still says "BSP tree, then 2 or 3 extra loops" (`BspLoopGenerator`); switching
to the approach below needs team agreement, then an update of `DECISIONS.md` §12–13 and GDD 6.1.

## 1. References studied

Both are listed in `CREDITS.md`. Neither has a license, so they are **references only: no code
is copied**, the algorithm is rewritten in Java.

| Repo | What it does | Verdict |
|---|---|---|
| [BugDeveloper/bsp-generator](https://github.com/BugDeveloper/bsp-generator) (Java, ~250 lines) | Splits the map recursively (BSP), one room per leaf, corridors between the centres of sibling regions, returns an `int[][]` | Simple, but grid-like look, connectivity not guaranteed, no loops, no room list |
| [AtlantiaKing/Procedural-2D-Dungeon-Generator](https://github.com/AtlantiaKing/Procedural-2D-Dungeon-Generator) (C++ / Win32, TinyKeep method) | Rooms scattered in a circle → separation → discard → Delaunay → minimum spanning tree → corridors → keys/doors checked by a solver | **Chosen as the model.** Organic layout, fits the "continuous looping labyrinth" hook |

### Why the C++ code is not ported as-is
- Tied to its school engine (`GAME_ENGINE`, `CAMERA`, Win32 drawing) and to global `rand()`;
  our ArchUnit rules forbid unseeded randomness and gdx statics in `core`.
- Works in pixels, not tiles (we use 32 px tiles).
- Bugs: `std::set<Edge>` ordered by length only drops edges of equal length (can disconnect the
  dungeon); missing `return` after the retry in `DungeonGenerator.cpp:85`; `room1` used instead of
  `room0` in `CreateCorridors` (line 622).
- Builds a pure tree (no loops), while §13 asks for 2–3 loops.

## 2. Pipeline

```
scatter → separate → discard → minimum spanning tree
→ choose boss (dead end), keep it away from loops → add k loops
→ assign room roles (start, key, items, arenas…) → draw corridors → draw onto the tile grid
```

| Step | What | Notes |
|---|---|---|
| Scatter | Rooms at a random angle, distance `radius × √random` from the centre | Injected `RandomSource`, tile units |
| Separate | Overlapping rooms push each other apart | Add an iteration cap: integer steps can bounce back and forth |
| Discard | Remove rooms too small, or too close to leave a corridor | |
| Spanning tree | Kruskal with union-find over **all pairs** of rooms | Sort by squared length, tie-break on room indices (same seed → same floor) |
| Loops | Add exactly `k` extra edges | See §4 |
| Roles | Start, boss, key, items, arenas | See §5–6 |
| Corridors | L-shaped, no crossing | See §4 |
| Tiles | Draw rooms and corridors onto a `Tile[][]` | |

Estimated size: ~300 lines plus tests, close to BSP plus loops. Plug it in as a second strategy
behind the `LevelGenerator` interface; the hand-made Tiled floors stay the fallback.

## 3. Why no Delaunay triangulation

In the original, Delaunay only cuts down the number of edges Kruskal has to sort. It never changes
the result:

- **The minimum spanning tree only uses Delaunay edges.** For a tree edge `A–B`, the circle with
  `A–B` as its diameter holds no other room `C`: otherwise `C` would be closer to both `A` and `B`,
  and replacing `A–B` by `C–A` or `C–B` would give a shorter tree. An edge with an empty diameter
  circle is always a Delaunay edge. So Kruskal over all pairs returns the same tree.
- **The speed gain doesn't matter here:** ~15–30 rooms are left after the discard step, so ~435
  pairs instead of ~90 edges, well under a millisecond.
- **Delaunay is the riskiest code:** a hard-coded "super triangle" that must contain every room,
  edge cases with rooms in a line or on one circle (the source of the buggy retry path), precision
  of the circumcircle test, and ~150 lines to test.

**What we would lose:** Delaunay edges link only neighbouring rooms, which matters for loop edges.
Same result with the **Gabriel test**: keep `A–B` as a loop candidate only if no other room centre
`C` satisfies

```
(C − A) · (C − B) < 0      // C inside the circle with diameter A–B; integers, no sqrt
```

For 30 rooms that is ~27 000 dot products, instant.

## 4. Loops: added after the tree, never inside it

Allowing Kruskal to sometimes close a cycle makes the count depend on luck. Building the tree,
then adding exactly `k` edges, makes the count a setting: **each added edge creates exactly one
loop**, so `corridors − rooms + 1 == k` holds by construction (the §13 test rule).

Choosing the `k` edges:
1. Candidates = Gabriel edges not already in the tree.
2. Reject tiny loops: the two ends must be at least ~4 rooms apart along the tree.
3. Reject an edge whose L-shaped corridor overlaps a room or **crosses another corridor**. A
   crossing on the tile grid is a junction, so an unplanned extra loop that breaks the count test.
   Try both L orientations (horizontal-then-vertical, vertical-then-horizontal) and keep the one that
   doesn't cross. Tree corridors need the same check.
4. Never touch the boss room.
5. Take the shortest remaining candidates. If fewer than `k` exist: retry with the next seed or
   accept fewer (to decide; the test must say which).

## 5. Boss key: built solvable, no solver

The original places several keys/doors at random, then runs a solver and retries. With **one key
for the boss door only**, the floor is solvable by construction:

1. **Start and boss** = the two dead ends furthest apart along the corridors (search from any
   room to its furthest room, then a second search from that one).
2. **Boss room stays a dead end** (no loop touches it), so its single entrance holds the locked door.
3. **Key room = any other room.** Removing a dead end never disconnects the rest, so the key is
   always reachable. Prefer a dead end that is not on the start→boss path.

Test: a search from the start with the boss room blocked reaches the key room.

Side effect: loops can shorten the walk to the boss. If that matters, choose the boss after the
loops (the dead end furthest from the start in the final graph), still kept away from loops.

## 6. Room roles (key, items, arenas…)

One general step instead of special code per room type. Each role is a **filter** (which rooms
qualify) plus a **score** (which one is best), applied in priority order:

| Priority | Role | Qualifies | Preference |
|---|---|---|---|
| 1 | Start | dead end | one end of the longest route |
| 2 | Boss | dead end, kept away from loops | the other end |
| 3 | Key | any room except the boss room | dead end not on the start→boss path |
| 4 | Item / chest ×N | any free room | dead ends first, spread out |
| 5 | Arena (doors lock until the enemies die) | free room, not a dead end | on or near the main route |
| 6 | Normal | the rest | |

Item rooms (chests open without a key, so no reachability rule):
- **Spread out:** pick one at a time, the free room furthest along the corridors from every room
  already chosen (search started from several rooms at once).
- **Dead ends are a preference, not a requirement:** a floor may have only 3–4, and start, boss
  and key already use up to 3. Fall back to normal rooms far from the main path.
- **Assign roles after the loops**, on the final graph (a loop can turn a dead end into a through
  room). Only the boss room is protected from loops.
- Key and item in different rooms by default.

Counts per type (e.g. `itemRooms = 2`) are settings, which answers the open point "rooms per
floor and per type" in `DECISIONS.md`.

## 7. To do before coding

- [ ] Team agrees to replace BSP with this generator.
- [ ] Update `DECISIONS.md` §12 (one key, boss door only; currently "key rooms") and §13, and GDD 6.1.
- [ ] Decide what happens when fewer than `k` loop candidates exist.
- [ ] Decide the boss timing: chosen on the tree (simpler) or after the loops (keeps the route long).
- [ ] Design the classes one method at a time, starting with the room record and the scatter step.
