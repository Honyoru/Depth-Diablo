# DECISIONS

Everything the team has decided for the game, taken from the Game Design Document and its Questions tab (state of 5 Nov 2026). Question numbers (Q..) and GDD sections point back to the shared doc.

Status note (Ange, 4 Nov): the team reviewed the whole draft orally, and every "proposal" left in the GDD is accepted as the **working default**. Items listed under "Working defaults" below are proposals in that state.

## 1. Delivery and scope

- The 12 Nov POC is cancelled. The only delivery is the full GDD and the full playable game on **22 Nov 2026 at 23:50** (Q82, GDD 1.1.1).
- Internal milestone: GDD finished by 5 Nov (Q82).
- The former POC scope is the first slice to build and the "Arena" game mode: 1 arena, 1 character, 1 weapon, 1 consumable, 4 enemy types spawning in an endless loop.
- Section owners (Q81) and the review presentation split (Q85) are **not decided yet**.
- draw.io diagrams (Q84) are drawn once the GDD is about 95 % done.

## 2. Game concept

- Underground dungeon crawler, hack-and-slash combat, 2D top-down view (The Binding of Isaac as genre reference), libGDX.
- Hook (Q9): a roguelite in a continuous, Doom-style looping labyrinth instead of Isaac's room grid, with an engine you can see working (rewind, X-ray view, event laser, hot reload).
- Continuous labyrinth with a camera that follows the player (Q76).
- Endless run, score = depth reached, no win screen, run ends at death (Q2, Q48). Lose condition: HP at 0 (Q3).
- Run length target: 15 to 30 minutes (Q6).
- No meta-progression: nothing persists between runs, no XP and no levels, progression comes from items only (Q4, Q5, Q46).
- Difficulty scales with depth: more enemies and better stats (Q47).
- Story (5.1): a beginner adventurer enters an unexplored dungeon, the entrance seals behind them, the only way out is deeper. Story details Q72 to Q75 stay open. No cinematics, no 3D.

## 3. Game modes and flow

- Title screen: Play (dungeon run), Arena, Options, Quit; Play and Arena then go to character select (Q10).
- Screens: title, character select, pause (options, quit), inventory overlay (pauses the game), shop overlay, game over (rerun or quit). No victory screen.
- Dungeon run: 2 to 3 hand-made floors, then procedurally generated floors forever (Q2, Q77). Each floor ends with a boss; the boss's death opens a teleport zone to the next floor and the shopkeeper appears (Q11, Q14). No way back up (Q12).
- Some areas lock until every enemy inside is dead; some doors need a key (Q13, Q80).
- Arena mode: waves in cycles of 5. Waves 1 to 4 are normal waves (Rat, Zombie, Skeleton Archer, Rat Nest), wave 5 is a boss. The shopkeeper appears after each boss wave. 15 s break between normal waves; after the shop, the next cycle starts when the player activates it. Score = waves defeated.
- Bosses and biomes come in no fixed order: each floor draws one of the three kings (working default: random from the seed, so a seed gives the same run).

## 4. Characters and enemies

- 3 playable classes that differ by stats only: **Knight** (melee), **Mage** (magic), **Archer** (bow, fires arrows) (Q15, Q25). Stat values are tuned during balancing.
- Stat model shared by player and enemies: HP, Attack, Defense, Speed, Attack Speed, Luck (Q44, Q45).
- Enemies: Rat, Zombie, Orc (melee); Skeleton Archer, Dark Mage (burn), Alchemist (lingering poison zone) (ranged); Necromancer (summons Skeleton Archers), Rat Nest (static, spawns Rats) (summoners). Summoned enemies drop nothing (Q16). The Succubus is cut.
- Bosses, one per floor: King of Rats, King of Orcs, King of Skeletons (Q17).
- The only NPC is the Shopkeeper: does not fight, cannot be hit (Q18).
- Player actions: move in 8 directions, attack, dash (fast, linear, can be hit), dodge roll (invincible), use consumable (3 hotbar slots), pick up (key press), interact, open inventory (Q40).
- Dash and dodge roll (20 Nov; replaces the Q40 placeholders): the dash (Shift) lasts 12 ticks (0.2 s) at 3x SPEED and can be hit; the dodge roll (Space) lasts 24 ticks (0.4 s) at 1.5x SPEED and is invincible for the whole roll. Both start once per press, go in the walking direction (nothing when standing still) and keep it to the end, with no walking or attacking meanwhile. Each has its own cooldown, which starts once it is over: 30 ticks (dash), 48 ticks (roll). The roll wins when both keys are pressed on the same tick. The base values live in the `Evade` enum; items will change them through the build (`Movement`, GDD 1.4), never in the enum.
- Character data (19 Nov): players and enemies share `data/characters.json`, told apart by their `"class"`. `CharacterKind` is a sealed interface over one enum per family (`PlayerKind`, `EnemyKind`); each kind holds the constructor of its class (`Knight::new`), so a new class with the same constructor is one enum line. `CharacterFactory.create(id, position, type)` reads the values every character shares, centres the body on the position and hands the entry to the factory of its family: `PlayerFactory` reads the starting `"weapon"`, `EnemyFactory` the `"reach"`. A family whose constructor differs (ranged enemies, bosses) gets its own kind enum. Items stay one spec record per class for now; they get kind enums and family factories once two item classes share a constructor.
- Spawns (19 Nov): the map's tiles with `spawn` (the player, the first one) and `spawn_enemy` are read in one pass; each character is centred on its tile.
- Aim: mouse or arrow keys, switched in Options (Q39). Keyboard and mouse plus gamepad, keys remappable (Q38, Q43).

## 5. Combat and stats

- Damage formula (Q26; defense side added 10 Nov): `damage = max(1, ((attack + attack bonus) x attack buff) / ((resistance + resistance bonus) x resistance buff))`, rounded down, then `hp -= damage`. Resistance is never 0: base resistance >= 1, resistance bonus >= 0, resistance buff > 0. Enemies use the same formula with bonus = 0 and buff = 1.
- The floor is not in the formula: the EnemyFactory multiplies an enemy's base stats by `floorMultiplier(n)` at spawn.
- No crits, no chance-based dodge, no block (Q27).
- Invincibility frames after a hit, 0.3 to 0.8 s placeholder (Q28). Knockback on every hit, for feedback (Q29).
- Hurtbox and hitbox are separate: damage only happens when an attacker's hitbox overlaps a target's hurtbox (1.5).
- Collision (Q41; hitbox shape changed 12 Nov): body boxes and hurtboxes are free AABBs. Every attack hitbox is an oriented box (`Obb`), checked against hurtboxes with the separating axis theorem; traps and contact attacks use an `Obb` at angle 0 around their box. Touching edges never overlap. Fixed time step of 60 logic updates per second, separate from rendering (Q42).
- Walls (20 Nov): a step blocked on one axis moves up to contact (the blocked part is halved 10 times), so walking and dashing stop against a wall, not short of it.
- Melee swing (12 Nov): the hitbox is a rectangle (weapon reach x width) that turns towards the aim and sweeps from `aim - arc / 2` to `aim + arc / 2` during the swing. One swing hits each target at most once; a new swing can hit it again.
- Factions (12 Nov): every Damageable fights for a side (`PLAYER`, `ENEMY` or `NEUTRAL`). An attack never hurts its own side, so enemies never hit each other; a `NEUTRAL` attack (trap) hurts everyone. An attack keeps the faction it was launched with; a charm can change a character's faction for its next attacks.
- Hit result (13 Nov): a hit returns what it did, its damage and whether it killed the target (`HitResult`, named in the item UML). A hit lands only if it deals damage; a dead or invincible target takes none, so it is not touched: the same attack can still hit it once its invincibility ends.
- Attack cooldown (16 Nov): an attack runs its swing (the weapon's `swingTicks`), then waits `60 / ATTACK_SPEED` ticks, rounded, at least 1 (18 Nov: ATTACK_SPEED comes from the character's build, which already multiplies in the weapon's speed). The cooldown only counts down once the swing is over, so two swings never overlap: the rhythm is swing + cooldown. An attack refused during the swing or the cooldown changes nothing. Players and enemies share this rule.
- Hits (18 Nov): `Character.hit(target)` goes through the attack of the character's build, so passive decorators (`FireTouch`) apply to every hit; the damage formula lives only in the `default hit` of `Attack`. Melee enemies attack through their `ContactAttack`, aimed at their target's centre; the rat and the zombie use a `FrontContactAttack` and hit every hostile character in its area. The player attacks while the left mouse button is held, towards the mouse.
- Swing direction and visuals (13 Nov): a swing always turns counter-clockwise (the angle grows). Planned visuals: fading afterimages of the blade, rebuilt from `MeleeWeapon.swingShape`, so they fit any arc.
- Traps: spikes with an activation delay (Q36).

## 6. Items, inventory and shop

- Item types: weapon, consumable, armor, passive item (modifier), plus a roulette ticket (modeled as a consumable). No crafting (Q30, Q33).
- Passive items spawn only in chests, which open without a key. In Arena mode there are no chests: passive items drop from enemies or bosses (which ones: TBD). Keys only open locked doors (Q80).
- Loot (Q35): enemies drop consumables, coins and roulette tickets; bosses drop a weapon or armor, consumables and coins.
- Inventory (changed by Romain, 16 Nov): 21 slots, 3 of them the hotbar (consumables only), 18 take only passive items. The weapon and armor are only in their 2 portrait slots, and the player always has both: picking one up swaps it and the old one drops on the ground. Items never stack, one item per slot. When no free slot accepts an item, it stays on the ground. Two copies of a passive item combine by that item's own rule (additive or multiplicative) (Q31, Q32).
- Passive item order (changed by Romain, 19 Nov): passive items apply in the order of their slots (0 to 17), not in pickup order, so the inventory shows the real order of the build. The player can swap two passive slots in the inventory (Switch), which changes that order; a passive item picked up goes into the first free slot.
- Armor (19 Nov): `Armor` is abstract and adds its defense bonus to the build; `LeatherArmor` is its first class, built only by `ItemFactory`. The player starts with a weapon and an armor (`"weapon"` and `"armor"` in `characters.json`), held by its inventory; the knight's armor is `leather_armor` (+1 DEFENSE).
- Items on the ground (20 Nov): an item on the ground is an `ItemDrop` entity, walked over. Only `ItemDropper` puts items on the ground: `drop(item, at)` for an existing item, `spawn(id, at)` for monster loot or room items; it also listens to `ItemDropped`, so a character only announces what falls. The player picks up the nearest item within 32 px of its centre with E, once per press; a swapped weapon or armor, or an item no slot takes, falls at its feet. The R rewind saves the inventory with the player, so a picked-up item is never duplicated.
- Hotbar and inventory page (20 Nov): 1/2/3 select a consumable slot and A (Q on a QWERTY keyboard; libGDX reads key positions) uses it, once per press. I opens the inventory page and pauses the game: the world does not tick, no time piles up, the animations freeze and the mouse does not attack. The page shows the equipment, the stats, the consumables and the 18 passive slots; a click on an item shows what it gives (`Item.getEffects()`) with Drop (consumables and passive items; the weapon and the armor are only replaced) and Switch (passive items), and a free slot selects nothing. Closing the page cancels a selection or a pending Switch.
- Shop: coins from enemies. Shopkeeper has 2 vending slots (weapon or armor; consumable) plus a roulette. A spin costs coins; a roulette ticket gives a free spin (Q14, Q34).
- Coin drop rates, prices and roulette odds are TBD during balancing.
- Weapons (16 Nov): a weapon's values come only from `data/items.json`; `Sword` has a package-private constructor that only `ItemFactory` calls. `ItemFactory` maps each `"class"` to a builder instead of a switch, so a new item type is one more line. Placeholder sword: COMMON, 30 coins, +2 attack, attack speed 1, reach 24, width 8, arc 90°, 12 ticks. The player gets its sword through the inventory's weapon slot.
- Ranged weapons (21 Nov): `RangedWeapon` fires one projectile per attack, which flies the weapon's `range` at its `projectileSpeed` (lifetime = range ÷ speed, at least 1 tick). Families: `Bow` (always an `Arrow`, 16 x 4 px, from the owner's centre) and `Staff` (each real staff picks its spell, e.g. a `Fireball` with its burn); the real weapons extend a family. New events in the sealed `GameEvent`: `AttackStarted` (once per attack, after the attack started) and `ItemPickedUp` (only when the item is kept). F2 shows pickups in orange and draws no attack.
- Ranged weapons in game (22 Nov): `ShortBow` (extends `Bow`, shoots an `Arrow`) and `FireStaff` (extends `Staff`, casts a `Fireball` with the burn of its entry) are built from `items.json` (`short_bow`, `fire_staff`). A bow of the same behaviour is only a JSON entry; a new spell (a new behaviour) is a new staff class. Each ranged weapon's entry gives `"projectileSpriteId"`, passed to its projectiles: the picture is data, not code. A projectile hits walls with the same turned `Obb` it hurts with (`World.isFree(Obb, Entity)`); its body `Aabb` only holds its position and length.

## 7. AI

- gdx-ai for everything: state machines, behavior trees (bosses) and A* pathfinding (Q49, Q50).
- Detection by line of sight, or a signal to an area's enemies when the player enters it (Q51).

## 8. Engine showcase features (GDD 1.1.2)

All four ship in the full game. Each is a separate module behind one small interface (`DebugFeature`); the engine never depends on a feature, and the game and tests must pass with any feature disabled.

| Key | Feature | Pattern |
| --- | --- | --- |
| R (hold) | Time rewind | Memento (+ Command) |
| F1 | View switcher: sprites, X-ray (hurtboxes and hitboxes, velocity vectors), high contrast | Strategy (`Renderer`) |
| F2 | Event laser: a visible line per event from emitter to receiver | Observer / Event Bus |
| F5 / TAB | Hot reload and in-game editor | Factory + serialization (Prototype) |

- Rewind (Q86): full state snapshot every fixed tick in a compact ring buffer of primitive arrays, 5 s x 60 ticks = 300 frames.
- Suggested build order: F1 and F2, then R, then F5 / TAB.

## 9. Architecture and code conventions

- Gradle project generated by gdx-liftoff, Java 21 (Q64).
- Modules: `core` (game logic, unit-tested at 70 %) and `desktop` (rendering, input, audio, LWJGL3 launcher) (Q98). `core` may use libGDX math, collections and gdx-ai; an ArchUnit test bans graphics, audio, input and `Gdx.*` statics in core (Q100). Core tests run on the libGDX headless backend.
- Package layout by feature: combat, entity, item, level, ai, shop, debug... (Q99).
- Layered class hierarchy instead of "max 2 levels": interfaces (Updatable, Collidable, Damageable, Rewindable, Pickable, Interactable) -> abstract Entity -> Character -> Player / Enemy -> families (Melee, Ranged, Summoner, Boss) -> concrete enemies. Items: Item -> Equipment (Weapon, Armor) / Consumable / PassiveItem. Stats stay in JSON. No full ECS (Q101, GDD 1.3.1).
- Passive items are Decorators wrapping a Character's stats, attack, hurtbox/hitbox, and movement.
- All tunable gameplay values live in JSON data; code constants only for non-gameplay values. No inline literal numbers (Q93). One JSON file per category in snake_case, for example `data/characters.json`, `data/items.json`; a load test parses every file (Q129).
- Style: Google Java Style, enforced by Spotless + google-java-format (`./gradlew spotlessApply`, `spotlessCheck` in CI), 100 columns, any IDE with an `.editorconfig` (Q87 to Q89, Q92).
- Naming: no `I` prefix, no `Impl`; interfaces get role names (`Renderer`, `LevelGenerator`), implementations say what they are (`XRayRenderer`, `BspLoopGenerator`) (Q90).
- English everywhere in code, comments, Javadoc and commits; player-facing text in French and English through `I18NBundle` with key format `area.element.detail`, files `strings.properties` (EN default) and `strings_fr.properties`; a test checks both hold the same keys (Q59, Q91, Q131).
- `Optional` for may-find-nothing returns, `Objects.requireNonNull` in constructors, no annotation library (Q94). Unchecked exceptions for programming errors, custom exceptions only where the caller can recover, never catch and ignore (Q95).
- SLF4J with Logback everywhere, `System.out` banned (Q96). Java records for value objects, final fields by default (Q97).
- Constructor injection by hand, one composition root in the launcher, no singletons (the event bus is passed in) (Q103). Every Strategy, Observer and State role is an interface first (Q104).
- Patterns shown in names where natural, a Javadoc line "Pattern: ..." on every pattern class, and a `PATTERNS.md` mapping pattern to classes to UML (Q102).
- Javadoc on every public class and method in core and desktop: summary line plus `@param`, `@return`, `@throws`; generated by hand before delivery, not in CI (Q105 to Q107).
- README: build, run, controls including the debug keys (Q109).
- UML: design diagrams first in draw.io, then redone in PlantUML (text files in `docs/uml`) from the code as built (Q108).

## 10. Tests and CI

- JUnit 5 + jqwik (about 200 tries per property) + ArchUnit. No Mockito, no AssertJ; hand-written fakes implementing our interfaces (Q110, Q117, Q120).
- Test names `should_X_when_Y`; Given / When / Then in three blocks, one behaviour per test (Q111, Q112).
- `src/test/java` mirrors the main packages, one `<ClassName>Test` per class; cross-class GDD 1.4.1 rules go in a `rules/` package, one class per section (Q113).
- Seeded `RandomSource` and a fixed time step are injected; ArchUnit bans `Math.random()`, `new Random()` and `System.currentTimeMillis()` in core (Q114).
- Every rule in GDD 1.4.1 is tested, test-first (red, then green); every bug fix starts with a failing regression test (Q115, Q121).
- Test data via builders in code (`anEnemy().withHp(10).build()`), a few JSON fixtures only for data-loading and hot-reload tests (Q116).
- Rendering: one test per renderer, render a frame and check the game state is unchanged; no pixel comparison (Q118).
- JaCoCo fails CI below 70 % on core; desktop and launcher excluded. Full suite under 30 s (Q65, Q119, Q120).
- CI: GitHub Actions on every push to any branch: `spotlessCheck` -> build -> tests (JUnit, jqwik, ArchUnit) -> JaCoCo 70 % on core -> SpotBugs + PMD (Q66, Q125).
- Definition of done: code + tests + Javadoc + draw.io UML updated + CI green + one approving review (Q126, review added 5 Nov).

## 11. Git workflow

- Repo: the private school (Epitech) repo; it allows GitHub Actions and branch protection (checked 4 Nov) (Q128).
- Branch per feature, merged through a **pull request reviewed by one other teammate** (changed by the team on 5 Nov; replaces Q83's "no pull requests"). Branch names `type/short-name`: `feature/shop-roulette`, `fix/dash-cooldown`, `docs/uml-entities`, `test/bsp-invariants` (Q122).
- Commits: Conventional Commits, for example `feat(shop): add roulette slot`, `fix(combat): clamp damage to 1` (Q123).
- `main` is protected: no direct push, no force-push; a pull request needs 1 approval, green CI (`build-and-test`) and an up-to-date branch (replaces Q124, 5 Nov).
- Merge strategy: **Rebase and merge** only, linear history, every commit keeps its author (replaces Q127's local fast-forward, 5 Nov). Merged branches are deleted.
- Binary assets: plain Git for now; switch to Git LFS (GitHub Student plan) only if music or large spritesheets arrive (Q132).

## 12. Art, UI and sound

- Pixel art, 32 x 32 px tiles, 960 x 540 virtual resolution scaled x2 to 1080p (30 x 17 tiles on screen) (Q60).
- Art from AI generation and/or licensed packs; one palette and one tile size for all. Every asset and its licence goes in `CREDITS`; folders like `assets/sprites/enemies/zombie_walk.png`, snake_case (Q61, Q130).
- No light radius (Q62). Animations per character: idle, move, attack, hit, death (Q63).
- Biome per floor changes only the tileset; the room types are the same on every floor (Q79). A floor holds: arena rooms, arenas with closed doors, corridors, chest rooms, key rooms, a boss room.
- HUD: minimal corner HUD, HP bar, the 3 hotbar slots, coin counter, current floor. No minimap, no floating damage numbers (Q54 to Q57). The inventory is a pause overlay (Q58). Menus and overlays are built with Scene2D.
- Options screen: remap keys, aim mode, language (FR / EN) (Q43, Q59).
- Sound: effects for hit, death, pickup and UI; one ambient music loop for the whole game; all from free CC0 libraries listed in `CREDITS` (Q68 to Q71).
- Sound effects (21 Nov): attack (one per attack, heard from the attacker: melee, bow, staff, or the enemy sound), damage (one per character hit, heard from the one hit), death, pickup (only when the item is kept), the player's footsteps (paced by the sound's length; Q69 had them as not a priority) and menu move / confirm. One source never plays the same effect twice at once. The attack sound follows the weapon's family (`MeleeWeapon`, `Bow`, `Staff`), so a new weapon needs no audio change. Music: one loop at volume 0.5 in game and 0.25 on menus, never restarted between screens. Exceptions to CC0 (Q71), credited in `CREDITS`: the staff sound and the music, both CC-BY-SA 3.0. The 3.3 MB music stays in plain Git (Q132).
- Music (22 Nov; replaces the single loop of Q70 in game): the menus keep the ambient loop; the game plays the musics of Renaud Beaud / JB, credited in `CREDITS` (composed for the project). Main during the waves and their breaks, Boss during the boss wave, Victory from the boss's death until the next wave (the shop, or the break when there is no shop). Each is an intro played once per run, then a loop; going back to the menus starts a new run. Two musics crossfade over 1.5 s; one caught mid-fade fades out from where it was. Volumes stay 0.25 on menus and 0.5 in game. Each sound effect has its own volume: the staff 0.7, the menu sounds 0.8, the others 1. Endless stays on Main until it has bosses and floors; it will then play Victory from the boss's death to the next floor.

## 13. Level generation

- 2 to 3 floors hand-made in Tiled for the demo and tests; after them, procedural floors forever (Q77).
- Generator: BSP tree, then 2 or 3 extra loops between rooms, behind a `LevelGenerator` interface (Strategy) with `BspLoopGenerator` and `TiledMapLoader` (GDD 6.1).
- Testable rules: every room reachable from the start; loop count = corridors - rooms + 1 equals the configured 2 or 3; the same seed gives the same floor.

## 14. Working defaults (proposals accepted on 4 Nov)

- Default controls: WASD move, Shift dash, Space dodge roll, 1 / 2 / 3 consumables, E interact, F pick up, I inventory, Esc pause; gamepad equivalents (GDD 2.4). Debug keys R, F1, F2, F5 / TAB are decided.
- Own AABB code in core rather than Box2D; walls block a separate body box at the feet.
- Item UML: `CharacterBuild` record rebuilt after every inventory change (base build -> weapon -> armor -> passive items in slot order, changed 19 Nov); `LootTable` shared by enemies, bosses, chests and roulette; coins and keys are counters on the Player (Ange's Claude, 5 Nov; files `uml/items.drawio` and `uml/entities.drawio`).
- The shopkeeper as a long-trapped adventurer; sample EN / FR text lines in GDD 5.4.

## 15. Still open

- Class stat values, enemy attack speed tiers, boss phases and summons (Q53), which enemies apply status effects in the full game.
- Wave size and makeup in Arena mode, rooms per floor and per type, coin drop rates, prices, roulette odds.
- Story Q72 to Q75, pixel font, icons and pointer style, event list for the event bus (Q24).
- Section owners (Q81) and review presentation split (Q85).
