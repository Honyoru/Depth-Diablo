# Questions

The team answers these to fill the GDD; each table maps to a GDD section. Already decided: underground dungeon crawler, hack-and-slash, 2D top-down (Binding of Isaac reference), libGDX. Write the answer in the table, then move it into the GDD section. Agent proposals are marked as such and are not decisions.

## 1.1 Core gameplay and POC scope

| # | Question | Answer |
| --- | --- | --- |
| Q1 | In one sentence, what does the player do, and why is it fun? | Romain : Player kills monster, go through a random generated dungeon, \
Upgrade between runs and level scalling between floors. Ange (4 Nov): no upgrades between runs, nothing persists (Q4)&#32; |
| Q2 | What is the win condition: reach a depth, kill a final boss, survive a time? | Ange: endless, score only. Score = depth reached; there is no win screen, and the run ends at death. Update 4 Nov: after the 2–3 hand-made floors, the run continues on procedurally generated floors. |
| Q3 | What is the lose condition: HP at 0 only, or also time or light running out? | HP at 0 |
| Q4 | Permadeath (roguelike) or saved progress between runs? | Saved progress and progression. Ange (4 Nov): nothing persists between runs; every run starts from zero. |
| Q5 | Is there meta-progression between runs (unlocks, persistent currency)? | Ange: yes, rogue-lite. XP is tracked in the player's stats (not shown on the HUD) and feeds the rogue-lite progression. Update 4 Nov (Ange): no XP. Progression comes from items only (Q46). Nothing persists between runs. |
| Q6 | How long is one run: 5, 15 or 30 minutes? | 15-30 min |
| Q7 | What exactly is in the review demo: how many floors, enemies, items, bosses? | Update 4 Nov (Ange): the 12 Nov POC is cancelled; the only delivery is the full game on 22 Nov. Its scope stays as the first slice and the Arena game mode: 1 arena, 1 character, 1 weapon, 1 consumable, 4 enemy types spawning in an endless loop. The 2–3 floors, 5–6 enemy types and bosses stay in the full game design. Note (4 Nov): Q16 and the GDD 1.3 roster now list 8 enemy types (+ Succubus to confirm), not 5–6. |
| Q8 | What is explicitly out of the POC? | Obsolete since 4 Nov (POC cancelled); this was the first-slice scope: no save, no menus, no title screen, no floors or level generation, no bosses, no inventory beyond 1 weapon and 1 consumable. See GDD 1.1.1. |
| Q9 | What makes our game different from its references (the hook)? | Ange (4 Nov): both. A roguelite in a continuous, Doom-style looping labyrinth instead of Isaac's room grid, with an engine you can see working (rewind, X-ray view, event laser, hot reload). |
| Q86 | Time rewind (GDD 1.1.2): compact ring buffer of primitive arrays (\~48 B per entity, \~1.4 MB for 100 entities over 5 s, no GC churn) or one Java object per entity per tick (\~130 B, \~3.9 MB, GC stutter risk)? Rewind length 5 s or 10 s? Snapshot every tick or every 2nd tick? Estimates assume 41 B of state per entity, to measure once the entity class exists. | Ange (4 Nov): compact ring buffer of primitive arrays, 5 s rewind, snapshot every tick (300 frames at 60 ticks/s). |

## 1.2 Game flow

| # | Question | Answer |
| --- | --- | --- |
| Q10 | Which screens exist: title, character select, game, pause, inventory, game over, victory? | Title, class selected, game pause with option and quit, inventory, game over with rerun or quit. Ange (4 Nov): the title screen offers Play (dungeon), Arena, Options and Quit, then character select. |
| Q11 | How does the player go down a floor: stairs, hole, elevator, after the boss? | tp zone at after boss fight is done |
| Q12 | Can the player go back up to a previous floor? | nop |
| Q13 | Are rooms locked until all enemies are dead? | some with key and arena battle logic |
| Q14 | Is there a safe room or shop between floors? | Shop after boss. Team: 3 slots: 1 weapon, 1 consumable, 1 gamble (roulette style). Ange (4 Nov): in Arena mode the shop appears every N waves (N to tune, e.g. 5). Ange (4 Nov): 2 vending slots (1 weapon or armor, 1 consumable) + the roulette; a spin costs coins, a roulette ticket gives a free spin. |

## 1.3 Characters, units and OOP mapping

| # | Question | Answer |  |
| --- | --- | --- | --- |
| Q15 | One playable character or several classes? If several, what changes between them (stats, weapons, abilities)? | different perso stat, |  |
| Q16 | Which enemy families exist (melee, ranged, swarm, tank, summoner)? How many for the POC? | Romain : melee, range, summoner 2 - 3 each&#32; Melee : Zombie (Power 2 Speed 2) , Rat (Power 1 Speed 3), Orc(Power 3 Speed 1) Range: Skeleton Archer (Arrow no effect), Mage(Fireball burn), Alchimist(Lingering poison effect)&#32; Summoner: Necromancer (Summon Skeleton Archer), Rat Nest (Summon Rat)\
No drop from invocations Ange (4 Nov): Arena mode uses Rat, Zombie, Skeleton Archer and Rat Nest. |  |
| Q17 | Are there bosses? One per floor, or one final boss? | One per floor King of Rats, King of Orc, King of Skeleton |  |
| Q18 | Are there NPCs (merchant, prisoner to free, quest giver)? | Merchant (shopkeeper, appears after each boss; see Q14) |  |
| Q19 | What do player, enemies and NPCs share (position, HP, hitbox, sprite)? This becomes the base class or interfaces. | Romain :&#32; Share : HP \| Position \| Hitbox \| Sprite&#32; Interface |  |
| Q20 | Which behaviours vary per entity and should be swappable (movement, attack, AI)? These are Strategy candidates. | From GDD 1.3.1: the weapon (Strategy attack), the renderer (F1), the level generator, and the Attack / Movement / Hitbox / Stat components that passive items decorate. |  |
| Q21 | Which entities change state (idle, chase, attack, stunned, dead)? These are State pattern candidates. | From GDD 1.3 roster: every enemy (idle, chase or keep distance, attack, hit, dead) through gdx-ai state machines; bosses add phases. Player states: TBD. |  |
| Q22 | Do entities transform or get buffs and debuffs that change behaviour (Decorator candidates)? | From GDD 1.3.1: yes. Passive items decorate any stat or behaviour (fire on hit, hitbox, attack speed, movement); status effects: burn, poison, charm. |  |
| Q23 | How are entities created: from data files (JSON) through a Factory, or hard-coded? | From GDD 1.3.1 and Q93: from JSON (one file per category, Q129) through an EnemyFactory; hot reload (F5) rebuilds them. |  |
| Q24 | Which events need listeners (damage taken, enemy killed, item picked, floor cleared)? These are Observer candidates. | From GDD 1.1.2: everything goes through the event bus (collision, damage, button), and the event laser (F2) shows it. Exact event list: TBD. |  |

## 1.4 Gameplay elements and testable rules

| # | Question | Answer |
| --- | --- | --- |
| Q25 | Combat: melee only, or melee plus ranged or magic? | melee or magic |
| Q26 | What is the damage formula? Write it as an equation, e.g. damage = attack × multiplier − defense, minimum 1. | ((damage+bonus stat)\*buff / (monster resistance\* multiplicator  etage)) - hp. Ange (4 Nov): written as damage = max(1, (attack + bonus) × buff / (resistance × floorMultiplier)), then hp −= damage; resistance is never 0.  |
| Q27 | Are there critical hits, dodge or block? With what chance or input? | NO |
| Q28 | Are there invincibility frames after a hit? How long, in ms? | ofc 0.3s \~ 0.8s |
| Q29 | Is there knockback? Does it depend on weight? | knockback for feedback |
| Q30 | Item types: weapons, armor, consumables, passive relics, keys? | Team (updated): 4 item types: weapon, consumable, armor, passive item (modifier) and Lotterie ticket. Ange (4 Nov): a ticket gives a free roulette spin Team: passive items spawn only in chests, which open without a key. |
| Q31 | Inventory: grid with limited slots, unlimited stack (like Risk of Rain 2), or equipment slots only? | 20 slots for passive + 1 weapon + 1 armor + 3 consommable |
| Q32 | Do item effects stack? Additively or multiplicatively? | relic item stack \| depend of which |
| Q33 | Is there crafting or item combining? | no |
| Q34 | Currency: what is it called, what drops it, what does it buy? | Team: coins. Enemies drop them; they buy from the shopkeeper after each boss (weapon or armor, consumable, roulette). Name and drop rates to set. |
| Q35 | Loot: random drops with rarity tiers, chests, or both? | Yes random loot from mobs ( Consommable, Gold, Lotterie Ticket)\
Boss (Weapon/Armor , Consommable, Gold) Team: chests (open without a key) hold passive items, and passive items spawn only in chests. |
| Q36 | Are there traps or hazards (spikes, pits, poison gas, falling rocks)? | Spike( with delay before activation) |
| Q37 | Which 10 rules must have a unit test first (combat, item effects, win and lose)? | Range \| Use of Potion \| |

## 1.5 Physics, movement and input

| # | Question | Answer |
| --- | --- | --- |
| Q38 | Movement: 8 directions with keyboard, or free with a gamepad stick? | Keyboard and/or gamepad |
| Q39 | Aim: same direction as movement, separate keys (twin-stick), or the mouse? | arrows directions |
| Q40 | Is there a dash or dodge roll? Cooldown? | Ange (4 Nov): both kept, with different effects and distances; values are placeholders, tuned during balancing. Dash {effect = "fast movement speed, linear déplacement but can be hit by the ennemies";\
effect duration = 0.8;\
cd = 0.5s;\
} \
dodge roll { effect ="normal movement speed with invincible time duration ";\
effect duration = 0.8;\
cd = 0.5s;\
}\
 |
| Q41 | Collision: tile-based grid or free AABB hitboxes? Box2D or our own? | &#32;collission AABB hitbox based |
| Q42 | Fixed time step for logic (e.g. 60 updates per second), separate from rendering? | From GDD 1.1.2 and Q86: yes, fixed 60 updates per second, separate from rendering (needed by rewind and determinism, Q114). |
| Q43 | Keyboard and mouse only, or gamepad too? Can keys be remapped? | Keyboard and mouse, gamepad too. Ange: keys can be remapped. |

## 1.6 Statistics and progression

| # | Question | Answer |
| --- | --- | --- |
| Q44 | Which stats does the player have (HP, attack, defense, speed, attack speed, crit, luck, light radius)? | HP \| Attack \| Defense \| Speed \| Attack Speed \| Luck |
| Q45 | Do enemies use the same stat model as the player? | Yes |
| Q46 | XP and levels, or progression through items only? | No only item |
| Q47 | How does difficulty scale with depth (more enemies, stronger stats, new types)? | More ennemies \| better stats |
| Q48 | Is depth (feet) the score and the main progress metric? | Yes |

## 1.7 AI

| # | Question | Answer |
| --- | --- | --- |
| Q49 | Our own enemy AI or gdx-ai? | Ange (4 Nov): gdx-ai for everything: state machines, behavior trees and steering. |
| Q50 | Pathfinding: do enemies need A\* around obstacles, or is direct chase enough for the POC? | Ange (4 Nov): gdx-ai A\* pathfinding, needed in the labyrinth. |
| Q51 | How do enemies detect the player: line of sight, radius, noise, light? | line of sight or when a room is entered by signaling to the enemy who spawn there |
| Q52 | Do enemies cooperate (surround, flee at low HP, call for help)? | possible depending on complexity |
| Q53 | What does each boss do differently (phases, patterns)? |  |

## 2 User interface and HUD

| # | Question | Answer |
| --- | --- | --- |
| Q54 | Which HUD elements: HP bar, currency, depth, minimap, active item, skill cooldowns, boss HP? | HP bar. Ange: plus consumables (potion count) and current floor number. XP is not on the HUD. Team (updated): coin counter is back, since enemies now drop coins for the shop. |
| Q55 | Minimal corner HUD (like the pixel dungeon reference) or richer (like Risk of Rain 2)? | Ange (4 Nov): minimal corner HUD, like the pixel dungeon reference: HP bar, potions, coins, floor. |
| Q56 | Is there a minimap, or a full map on a key? | no |
| Q57 | Damage numbers floating over enemies? | no |
| Q58 | Inventory as a pause overlay or a separate screen? | Ange (4 Nov): pause overlay (the game pauses while it is open). |
| Q59 | Which language(s) for in-game text: English, French, both? | Ange: French and English, switchable (not complicated). |

## 3 Art and tech

| # | Question | Answer |
| --- | --- | --- |
| Q60 | Pixel art? Tile size (16 × 16 or 32 × 32 px)? Target resolution? | 32x32 target resolution is 1080p. Ange (4 Nov): virtual resolution 960×540 scaled ×2 to 1080p, so 30×17 tiles on screen. |
| Q61 | Free asset packs (which licence) or our own sprites? | generated by ai for the art and or licencses pack |
| Q62 | Is light radius a gameplay mechanic (torches, upgrades) or visual only? | no light radius |
| Q63 | Which animations are needed per entity (idle, walk, attack, hit, death)? | hit, idle, attack, moving, death classicle, charm if sucubus or other gameplay |
| Q64 | Build tool: Gradle (libGDX default) or Maven? Java version? | Ange (4 Nov): Gradle (generated by gdx-liftoff) and Java 21. |
| Q65 | Test stack: JUnit 5, coverage with JaCoCo? Coverage target (e.g. 70 %)? | Ange: JUnit 5 for unit tests on game logic. JaCoCo measures coverage and fails the CI build below 70 % on the core logic module only, since rendering is excluded (see Q67). Coverage proves code ran, not that it was checked, so tests must assert results. |
| Q66 | CI: GitHub Actions on every push? Running tests and coverage? | Ange (4 Nov): GitHub Actions on every push to any branch; full step list in Q125. |
| Q67 | How do we keep game logic testable without libGDX (separate core module, no rendering in logic classes)? | Ange (4 Nov): see Q98 and Q100: a core module with no rendering, audio or input (ArchUnit check), tested on the libGDX headless backend. |

## 4 Sound and music

| # | Question | Answer |
| --- | --- | --- |
| Q68 | Is sound in the game at all? | Yes |
| Q69 | Which sound effects matter most (hit, death, pickup, footsteps, UI)? | Ange (4 Nov): hit, death, pickup and UI sound effects first. |
| Q70 | Music: one ambient loop per floor, or dynamic music during combat? | Ange (4 Nov): one ambient music loop for the whole game. |
| Q71 | Free sound libraries (which licence) or our own? | Ange (4 Nov): free CC0 libraries (e.g. Kenney, OpenGameArt), each file listed in a CREDITS file. |

## 5 Story

| # | Question | Answer |
| --- | --- | --- |
| Q72 | Why is the player going underground (escape, treasure, rescue, curse)? | Ange (4 Nov): not fixed for now (Q72–Q75 stay open). |
| Q73 | Who or what lives down there, and why is it hostile? |  |
| Q74 | How is the story told: intro text, NPC dialog, notes found on floors, none? |  |
| Q75 | Tone: dark, humorous, mysterious? |  |

## 6 Levels

| # | Question | Answer |
| --- | --- | --- |
| Q76 | Room-by-room screens or a scrolling map? | Ange: continuous labyrinth with a camera that follows the player (not Isaac's fixed rooms) and Doom-style loops (instead of the room-by-room proposal from Loïc's Claude). |
| Q77 | Hand-made rooms (Tiled) or procedural generation? | Ange: hand-made first (Tiled maps, with the loops designed by hand) behind a `LevelGenerator` interface, as Loïc's Claude proposed. Update 4 Nov (Ange): the 2–3 hand-made floors are for the demo and tests; procedural generation (GDD 6.1) is required after them for the endless run. Generator: BSP + 2–3 loops. |
| Q78 | How many rooms per floor, and which room types (combat, treasure, shop, boss, secret)? |  |
| Q79 | Does each floor have a different biome (mine, caves, ruins, lava)? | yes to determine |
| Q80 | Are there keys, locked doors or secrets? | yes key, chest and lock door. Team (updated): chests need no key. Team (4 Nov): yes, keys open locked doors; chests stay keyless. |

## Team and process

| # | Question | Answer |
| --- | --- | --- |
| Q81 | Who owns which GDD sections? |  |
| Q82 | What is the delivery date, and when must the GDD be finished? | delivery of the end project is 22/11/2026 23h50. Ange (4 Nov): internal milestone, GDD finished by 5 Nov. |
| Q83 | Git workflow: branch per feature, pull requests reviewed by one teammate? | branch per feature, no pull request but automated test |
| Q84 | Which UML diagrams do we deliver (class, sequence, state, use case)? With which tool (PlantUML, Mermaid, draw.io)? | Draw.io need to provide BEFORE starting |
| Q85 | How do we split the review presentation between the three of us? |  |

## Coding and test conventions

Agree on these before the first line of code. Each answer becomes a rule in a CONTRIBUTING file in the repo and, where possible, a check in CI.

### Code style

| # | Question | Answer |
| --- | --- | --- |
| Q87 | Which style guide: Google Java Style, Oracle conventions, or our own? | Ange (4 Nov): Google Java Style. |
| Q88 | Enforced by a tool (Checkstyle, Spotless + google-java-format) and checked in CI? | Ange (4 Nov): Spotless + google-java-format. `./gradlew spotlessApply` formats; `spotlessCheck` runs in CI and fails on unformatted code. |
| Q89 | Shared IDE settings: one IDE for all (IntelliJ?), a shared formatter file, an .editorconfig? | Ange (4 Nov): any IDE; an .editorconfig sets indent and line endings, Spotless does the real formatting. |
| Q90 | Naming: classes `PascalCase`, methods and fields `camelCase`, constants `UPPER_SNAKE`; any prefixes or suffixes (`I` for interfaces, `Impl`, `Abstract`)? | Ange (4 Nov): no I prefix, no Impl. Interfaces get role names (Renderer, LevelGenerator); implementations say what they are (XRayRenderer, BspLoopGenerator); the pattern shows in the name where natural (EnemyFactory, PlayerMemento). |
| Q91 | Language of code, comments and commit messages: English only? | Ange (4 Nov): English everywhere (code, comments, Javadoc, commits). Player-facing text is FR + EN through I18NBundle (Q131). |
| Q92 | Max line length (100? 120?), max method length, max class length? | Ange (4 Nov): 100 columns (Google Style, enforced by Spotless). Guidelines, not checked: methods about 30 lines, classes about 300 lines. |
| Q93 | Magic numbers: all game values (speed, damage, cooldowns) in constants or JSON data, never inline? | Ange (4 Nov): all tunable gameplay values in JSON data (needed for hot reload F5 and balancing). Code constants only for non-gameplay values (tick rate, tile size). No inline literal numbers. |
| Q94 | Null handling: `Optional` for return values, `Objects.requireNonNull` on inputs, or annotations (`@NonNull`)? | Ange (4 Nov): a method that may find nothing returns Optional; constructors check inputs with Objects.requireNonNull; no annotation library. |
| Q95 | Error handling: when to throw, checked vs unchecked exceptions, custom exception types? | Ange (4 Nov): unchecked exceptions for programming errors (IllegalArgumentException, IllegalStateException). Custom exceptions only where the caller can recover (e.g. InvalidGameDataException when JSON loading fails). Never catch and ignore. |
| Q96 | Logging: libGDX `Gdx.app.log`, SLF4J, or `System.out` banned? Log levels? | Ange (4 Nov): SLF4J everywhere (core and desktop) with a Logback backend; System.out banned. Levels: error, warn, info, debug. |
| Q97 | Immutability: final fields and records for value objects (stats, positions, events)? | Ange (4 Nov): Java 21 records for value objects (Stats, Position, events, snapshot data); final fields by default; mutable only where state really changes (entity HP, position). |

### Architecture and packages

| # | Question | Answer |
| --- | --- | --- |
| Q98 | Gradle modules: `core` (pure logic, no libGDX) + `desktop` (rendering, input)? Others (`data`, `tools`)? | Ange (4 Nov): core (game logic, unit-tested at 70 %) + desktop (rendering, input, audio, LWJGL3 launcher). |
| Q99 | Package layout: by feature (`combat`, `entity`, `level`, `ai`) or by layer (`model`, `view`, `controller`)? | Ange (4 Nov): by feature: combat, entity, item, level, ai, shop, debug, ... |
| Q100 | Rule: no libGDX import allowed in `core`? Checked automatically (ArchUnit test)? | Ange (4 Nov): core may use libGDX math, collections and gdx-ai (needed for AI, Q49). An ArchUnit test bans graphics, audio, input and Gdx.\* statics in core. Core tests run on the libGDX headless backend. |
| Q101 | Composition over inheritance: max inheritance depth? Entity components or class hierarchy? | Ange (4 Nov, updated): layered hierarchy instead of max 2 levels: interfaces (Updatable, Collidable, Damageable, Rewindable) → abstract Entity → abstract Character → Player / abstract Enemy → abstract families (Melee, Ranged, Summoner, Boss) → concrete enemies. Items: Item → Equipment (Weapon, Armor) / Consumable / PassiveItem. Stats stay in JSON. Full tree in GDD 1.3.1. No full ECS. |
| Q102 | How do we mark design patterns in the code so the jury finds them (class names, Javadoc tag, UML link)? | Ange (4 Nov): pattern in the name where natural (EnemyFactory); a Javadoc line on every pattern class (e.g. "Pattern: Memento (originator)"); a PATTERNS.md table mapping pattern → classes → UML diagram. |
| Q103 | Dependency injection: constructor injection by hand, or a small DI library? No singletons except the event bus? | Ange (4 Nov): constructor injection by hand, one composition root in the launcher. No singletons at all; the event bus is passed in like any other dependency. |
| Q104 | Interfaces first: is every Strategy / Observer / State defined as an interface before its implementations? | Ange (4 Nov): yes. Every Strategy, Observer and State role is an interface (Renderer, Weapon, LevelGenerator, DebugFeature) before any implementation. |

### Documentation

| # | Question | Answer |
| --- | --- | --- |
| Q105 | Javadoc required on: every public class and method, or public API of `core` only? | Ange (4 Nov): every public class and method in core and desktop; private code only where it is not obvious. |
| Q106 | Javadoc content: summary line, `@param`, `@return`, `@throws`, plus an example for complex methods? | Ange (4 Nov): summary line + @param, @return, @throws; an example only for complex methods (damage formula, level generator). |
| Q107 | Javadoc build checked in CI (fail on missing docs or broken links)? | Ange (4 Nov): not in CI; Javadoc is generated by hand before delivery. |
| Q108 | UML: where do the diagrams live (repo `docs/uml`), which tool (PlantUML, Mermaid, draw.io), who keeps them in sync with the code? | Ange (4 Nov): both, in two phases. Design diagrams first in draw.io (GDD phase); once the code is built, the diagrams are corrected and redone in PlantUML to match the code as built (text files in docs/uml). |
| Q109 | README sections: how to build, run, test, controls, architecture overview? | Ange (4 Nov): minimal: build, run, controls (including the F1, F2, R, F5 / TAB debug keys). The rest lives in the GDD. |

### Tests

| # | Question | Answer |
| --- | --- | --- |
| Q110 | Libraries: JUnit 5 only, or also Mockito (mocks), AssertJ (readable assertions), jqwik (property-based)? | Ange (4 Nov): JUnit 5 + jqwik (property-based, e.g. BSP invariants over many seeds) + ArchUnit (core rules, Q100). No Mockito and no AssertJ: plain JUnit assertions, and hand-written fakes where a test needs one. |
| Q111 | Test naming: `methodName_condition_expectedResult`, `should...When...`, or `@DisplayName` sentences? | Ange (4 Nov): should\_X\_when\_Y, e.g. should\_deal\_at\_least\_1\_damage\_when\_resistance\_is\_high. |
| Q112 | Test structure: Arrange / Act / Assert (or Given / When / Then) with blank lines between the parts? | Ange (4 Nov): Given / When / Then, three blocks separated by blank lines; one behaviour per test. |
| Q113 | Location: `src/test/java` mirroring the main package, one test class per class (`CombatTest` for `Combat`)? | Ange (4 Nov): mix. src/test/java mirrors the main packages with one \<ClassName>Test per class; cross-class GDD 1.4.1 rules go in a rules/ package, one class per section (ShopRulesTest, CombatRulesTest). |
| Q114 | Determinism: seeded RNG and a fixed time step injected in tests, never `Math.random()` or real time? | Ange (4 Nov): a seeded RandomSource and a fixed time step are injected. ArchUnit bans Math.random(), new Random() and System.currentTimeMillis() in core. Same seed, same result. |
| Q115 | What must always be tested: every rule in GDD 1.4.1, every bug fix (regression test first), every public method of `core`? | Ange (4 Nov): every rule in GDD 1.4.1, and every bug fix starts with a failing regression test. Public core methods are covered through the 70 % gate, not one by one. |
| Q116 | Test data: builders or factory methods (`anEnemy().withHp(10).build()`), or JSON fixtures? | Ange (4 Nov): builders in code (anEnemy().withHp(10).build()); a few small JSON fixtures only for data-loading and hot-reload tests. |
| Q117 | Mocks: allowed for which dependencies (event bus, renderer, input), and banned for value objects? | Ange (4 Nov): hand-written fakes implementing our interfaces (e.g. RecordingEventBus keeps the events it got), made possible by interfaces-first (Q104). Value objects are always real. No Mockito. |
| Q118 | Rendering tests: skip, use libGDX headless backend, or only check that renderers read state without changing it? | Ange (4 Nov): one test per renderer: render a frame and check the game state is unchanged (F1 rule, GDD 1.1.2). No pixel comparison. |
| Q119 | Coverage: target per module (e.g. `core` 70 %), exclusions (generated code, `desktop`), and does a drop fail the build? | Ange (4 Nov): JaCoCo fails CI below 70 % on core; desktop and the launcher are excluded. |
| Q120 | Test speed: max time for the full suite (e.g. under 30 s) so everyone runs it before pushing? | Ange (4 Nov): full suite under 30 s; jqwik limited to about 200 tries per property. |
| Q121 | Test-first (TDD) for game rules, or tests written after the code? | Ange (4 Nov): test-first for the GDD 1.4.1 rules (red, then green); other code tested after, in the same commit. |

### Git and CI

| # | Question | Answer |
| --- | --- | --- |
| Q122 | Branch naming: `feature/<name>`, `fix/<name>`, `docs/<name>`? | Ange (4 Nov): type/short-name: feature/shop-roulette, fix/dash-cooldown, docs/uml-entities, test/bsp-invariants. |
| Q123 | Commit messages: Conventional Commits (`feat:`, `fix:`, `test:`, `docs:`, `refactor:`) or free form? | Ange (4 Nov): Conventional Commits, e.g. feat(shop): add roulette slot, fix(combat): clamp damage to 1. |
| Q124 | Without pull requests (Q83), what stops broken code reaching main: protected main + required CI checks? | Ange (4 Nov): protected main with required CI checks. Without PRs: push to the feature branch, wait for green CI, then push the same commit to main. Ange (4 Nov): the school repo allows it. |
| Q125 | CI steps and order: format check, build, unit tests, coverage, Javadoc, static analysis (SpotBugs, PMD)? | Ange (4 Nov): spotlessCheck → build → tests (JUnit, jqwik, ArchUnit) → JaCoCo 70 % on core → SpotBugs + PMD. Javadoc is not in CI (Q107). |
| Q126 | Definition of done for a feature: code + tests + Javadoc + UML updated + CI green? | Ange (4 Nov): code + tests (1.4.1 rules test-first) + Javadoc + draw.io UML updated + CI green. PlantUML is redone from the code after the build (Q108). |
| Q127 | Merge strategy: merge commits, squash, or rebase? | Ange (4 Nov): rebase the feature branch on main, wait for green CI, then fast-forward main. Linear history; matches the protected-main flow (Q124). |
| Q128 | Repo hosting: private GitHub repo (not the Epitech one)? Who owns it? | Ange (4 Nov): the private school (Epitech) repo. Checked 4 Nov: it allows GitHub Actions and branch protection (Q66, Q124). |

### Data and assets

| # | Question | Answer |
| --- | --- | --- |
| Q129 | JSON data files: one file per entity type or one per category? Naming (`snake_case.json`)? Schema or validation test? | Ange (4 Nov): one JSON file per category in snake\_case (data/enemies.json, data/items.json, ...); a load test parses every file. |
| Q130 | Asset naming and folders: `assets/sprites/enemies/zombie_walk.png`? Licences listed in a `CREDITS` file? | Ange (4 Nov): assets/sprites/enemies/zombie\_walk.png style (snake\_case, folder per category); every asset and its licence listed in CREDITS. |
| Q131 | Localization (Q59, FR + EN): key format (`hud.potion.count`), one properties file per language (libGDX I18NBundle)? | Ange: no player-visible text in code, only keys. Format `area.element.detail` (e.g. `hud.potion.count`). libGDX `I18NBundle` with `strings.properties` (EN, default) and `strings_fr.properties` (FR). A unit test checks both files hold the same keys. See GDD 5.3. |
| Q132 | Large binary assets: plain Git or Git LFS? | Ange: plain Git for now. Pixel art and short sounds stay small. Switch to Git LFS (large files kept outside the history, `git lfs track "*.png"`) only if music or big spritesheets arrive. Ange (4 Nov): if LFS is needed, it uses the GitHub Student plan. See GDD 3.1.1. |
