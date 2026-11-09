# 2D Game — Game Design Document

Nov 3, 2026 · @fsjdiw

This GDD is the design-phase deliverable: no game code is written until it is complete. It follows the traditional outline from Stone Librande's [GDC 2010 talk](https://gdcvault.com/play/1012356/One-Page), minus Multiplayer, plus four OOP-focused subsections (1.1.1, 1.3.1, 1.4.1, 3.1.1).

- One owner per section writes it; the other two review through comments.
- Discuss a sentence with a comment on it; discuss anything broader in the Agent channel.
- Every rule in 1.4.1 must be phrased so it can become a unit test.
- A section left empty for the delivery gets one line saying why (for example, "No cinematics").

Review status (Ange, 4 Nov): the team reviewed the whole draft orally; every "proposal" left in the text is accepted as the working default. Section owners (Q81) and the presentation split (Q85) are not decided yet. The draw.io diagrams (Q84) are drawn once the GDD is about 95 % done.

Agents and teammates discuss open topics in Agent channel. Read its rules before editing this tab.

## 1. Game Mechanics

### 1.1 Core Gameplay

**Decided:** a dungeon crawler with hack-and-slash combat, seen from a 2D top-down view. Genre reference: The Binding of Isaac (top-down combat and items). Unlike Isaac, the camera follows the player through one continuous labyrinth, not a grid of fixed-screen rooms (Q76). The dungeon is underground.

**Hook (decided, 4 Nov; Q9):** a roguelite set in a continuous, Doom-style looping labyrinth instead of Isaac's grid of rooms, with an engine you can see working: rewind, X-ray view, event laser and hot reload (1.1.2).

**No meta-progression (decided, Ange, 4 Nov):** nothing persists between runs. Every run starts from zero; progression inside a run comes from items only (Q4, Q5, Q46).

#### 1.1.1 Scope: first slice (Arena mode) and full game

**Decided (Ange, 4 Nov):** the 12 Nov POC is cancelled. The only delivery is the full GDD and the full playable game on 22 Nov at 23:50. The former POC scope is kept as the first slice to build and as the "Arena" game mode of the full game: one arena where enemies spawn in an endless loop until the player dies, next to the main dungeon run. The rest of this GDD describes the full game.

| First slice (Arena mode) | Added after, for 22 Nov |
| --- | --- |
| 1 arena | Floors, labyrinth, Tiled levels, level generation |
| 1 playable character | 3 classes (Knight, Mage, Archer), character select |
| 1 weapon | Inventory, other items |
| 1 consumable (potion) | Loot tables, chests, shop (no crafting, Q33) |
| 4 enemy types, spawning in an endless loop | Bosses, scaling by depth |
| HUD | Menus, title screen, settings (no run progress is saved: nothing persists between runs) |

Decided (Ange, 4 Nov): the Arena mode's 4 enemy types are Rat (fast melee), Zombie (slow melee), Skeleton Archer (ranged, no status effect) and Rat Nest (summons Rats). That covers all 3 families from Q16 with no burn or poison system needed.

#### 1.1.2 Engine Showcase Features (demo)

**Decided (Ange, 4 Nov):** all four debug features ship in the full game, each one showing an OOP principle live during the review. Each is built independently, so an unfinished feature never breaks another one or the engine.

| Key | Feature | What the jury sees | Pattern | Watch out |
| --- | --- | --- | --- | --- |
| R (hold) | **Time rewind**: the game plays backwards like a video | Strict encapsulation: each object saves and restores its own state | Memento (+ Command) | Saving only (x, y) breaks on HP, AI state, projectiles, spawns and deaths. Decided (Q86): full state snapshot every fixed tick in a compact ring buffer of primitive arrays, 5 s × 60 ticks = 300 frames (\~1.4 MB for 100 entities, no GC churn). |
| F1 | **View switcher**: sprites → X-ray (hurtboxes and hitboxes in two colours + velocity vectors) → high contrast | Logic fully separate from rendering | Strategy (`Renderer` interface) | Only works if logic classes never touch libGDX textures (Q67). High contrast also counts as accessibility. |
| F2 | **Event laser**: a visible line from emitter to receiver for each event (collision, damage, button) | Objects are decoupled and talk through the engine | Observer / Event Bus | Every event must carry its source and target. The laser is just one more subscriber. |
| F5 / TAB | **Hot reload + in-game editor**: edit JSON stats or drag walls in pause, F5 applies without restart | Data-driven engine: entities come from data, not code | Factory + serialization (Prototype) | Largest scope of the four; the editor UI can eat the schedule. |

Shared requirements: a fixed time step, game state kept apart from rendering, and entities built from data. Together they make the core logic module testable without a window.

Testable rules (feed 1.4.1):

- Rewind N ticks, then the state equals the snapshot taken N ticks earlier.
- Switching renderer leaves the game state unchanged.
- A published event reaches every subscriber, and the emitter holds no reference to the receiver.
- JSON → entity → JSON round-trip gives the same data; a reload changes stats on live entities.

Isolation rule: each feature is a separate module behind one small interface (e.g. DebugFeature with enable/disable and a hook into the game loop). The engine never depends on a feature, and a feature can be switched off or left out of the build. Testable rule (1.4.1): the game runs and its tests pass with any feature disabled. Suggested build order: F1 and F2 first (cheap, they shape the architecture), then R, then F5 / TAB.

### 1.2 Game Flow

**Decided (Ange, 4 Nov):** the title screen offers Play (dungeon run), Arena, Options and Quit; Play and Arena then go to character select (Q10).

**Dungeon run (from the answers):**

1. Title → Play → character select (Q10).
2. The floor starts. The player explores one continuous labyrinth with 2–3 loops; the camera follows the player (Q76). Some areas lock until every enemy inside is dead (arena fights, Q13); some doors need a key (Q80).
3. Each floor ends with its boss (Q17). When the boss dies, a teleport zone to the next floor opens (Q11) and the shopkeeper appears (Q14). There is no way back up (Q12).
4. Floors 1–3 are hand-made; after them the run goes on forever on procedurally generated floors (Q2, Q77). Each floor has more enemies with better stats (Q47).
5. HP at 0 ends the run (Q3) → game over screen: rerun or quit (Q10). Score = depth reached (Q2, Q48). Nothing carries over to the next run (1.1).

At any time: pause (options, quit) and the inventory overlay, which pauses the game (Q58). Target run length: 15–30 min (Q6).

Arena mode (decided, Ange, 4 Nov): Title → Arena → character select → one arena with waves in cycles of 5. Waves 1–4 are normal enemy waves (Rat, Zombie, Skeleton Archer, Rat Nest, 1.1.1); wave 5 is a boss. When the boss dies, the shopkeeper appears. Between two normal waves there is a 15 s break; after the shop, the next cycle starts only when the player activates it. Score = waves defeated. HP at 0 → game over.

Decided (Ange, 4 Nov): one boss per floor, and bosses and biomes come in no fixed order: each floor draws them (proposal: at random from the seed, so a seed still gives the same run). TBD during balancing: wave size and makeup in Arena mode, and which king the Arena's boss waves use (proposal: random).

### 1.3 Characters / Units

**Shopkeeper (decided by the team):** the only NPC. Appears after each boss (and after each boss wave in Arena mode, every 5th wave), has 2 vending slots and a roulette (see 1.4), does not fight.

#### Character roster

Every character in the game, as the source for the draw.io class diagram. Stats use tiers (L = low, M = mid, H = high). Melee ATK and SPD come from Romain's Power / Speed scale (Q16); every other tier is a proposal to confirm; real numbers live in JSON and are tuned during balancing (Q93). Stat model shared by everyone (Q44, Q45): HP, Attack, Defense, Speed, Attack Speed, Luck. TBD = still to decide.

**Player**

| Character | HP | ATK | DEF | SPD | ATK SPD | Luck | Actions | Notes |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Player (base) | M | from weapon | from armor | M | from weapon | M | move (8 directions), attack (aim with the mouse or the keyboard, switched in Options, Q39), dash (fast, linear, can be hit) and dodge roll (invincible), with different effects and distances (Q40), use consumable (3 hotbar slots), pick up an item (key press), interact (chest, locked door with key, shop), open inventory (pause overlay) | i-frames after a hit (Q28), knockback (Q29). First slice: 1 character; class select comes with the full game (1.2) |
| Knight | TBD | TBD | TBD | TBD | TBD | TBD | same as base | Melee weapon. Decided (Ange, 4 Nov): 3 classes (Knight, Mage, Archer) that differ by stats only (Q15); values TBD during balancing. The enemy mage is renamed Dark Mage. |
| Mage | TBD | TBD | TBD | TBD | TBD | TBD | same as base | Magic weapon (Q25). |
| Archer | TBD | TBD | TBD | TBD | TBD | TBD | same as base | Ranged weapon: a bow that fires Arrow projectiles (decided, 4 Nov). |

**Enemies (Q16)**

| Character | Family | HP | ATK | DEF | SPD | Attack and effect | Behaviour (gdx-ai states) | Drops (Q35) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Rat | Melee | L | L (1) | L | H (3) | bite, contact | idle, chase, attack, hit, dead | consumable, coins, ticket; none when summoned |
| Zombie | Melee | M | M (2) | M | M (2) | melee hit | idle, chase, attack, hit, dead | consumable, coins, ticket |
| Orc | Melee | H | H (3) | H | L (1) | heavy melee hit | idle, chase, attack, hit, dead | consumable, coins, ticket |
| Skeleton Archer | Ranged | L | M | L | M | arrow, no effect | idle, keep distance, shoot, hit, dead | consumable, coins, ticket; none when summoned |
| Dark Mage | Ranged | L | H | L | M | fireball, burn (damage over time) | idle, keep distance, cast, hit, dead | consumable, coins, ticket |
| Alchemist | Ranged | M | M | L | M | potion throw, lingering poison zone | idle, keep distance, throw, hit, dead | consumable, coins, ticket |
| Necromancer | Summoner | M | L | L | L | summons Skeleton Archers | idle, flee, summon, hit, dead | consumable, coins, ticket |
| Rat Nest | Summoner | M | none | M | 0 (static) | spawns Rats | idle, spawn, hit, dead | consumable, coins, ticket |

Arena mode uses Rat, Zombie, Skeleton Archer and Rat Nest (1.1.1).

**Bosses (Q17), one per floor**

| Character | Floor | HP | ATK | DEF | SPD | Attacks and phases (Q53) | Summons | Drops |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| King of Rats | any (no fixed order) | H | M | M | H | TBD | Rats? | weapon or armor, consumables, coins |
| King of Orcs | any (no fixed order) | H+ | H | H | L | TBD | Orcs? | weapon or armor, consumables, coins |
| King of Skeletons | any (no fixed order) | H+ | H | M | M | TBD | Skeleton Archers? | weapon or armor, consumables, coins |

After each boss: teleport zone to the next floor (Q11) and the shopkeeper appears (Q14).

**NPC**

| Character | HP | Actions | Notes |
| --- | --- | --- | --- |
| Shopkeeper | none (cannot be hit) | appear (after each boss; after each Arena boss wave), 2 vending slots (weapon or armor; consumable) + roulette | the only NPC (1.3) |

**Not characters, but entities on the same diagram:** projectiles (arrow, fireball, poison zone), chest (keyless, holds passive items), locked door and key, spike trap with activation delay (Q36), teleport zone, item pickups (weapon, armor, consumable, passive item, coin, roulette ticket).

Decided (Ange, 4 Nov): the Succubus is cut. TBD during balancing: class stat values, Attack Speed tiers for enemies, boss phases and summons (Q53), and which enemies apply status effects in the full game (burn and poison need a StatusEffect system).

#### 1.3.1 Entities → Class Hierarchy → Design Patterns

**Decided (Ange, 4 Nov), replaces Q101's "max 2 levels":** a layered hierarchy. The brief sets no depth, but asks for "many kinds of entities sharing common traits and differing in behavior" shown in UML with design patterns, so every level adds one shared trait or one behaviour. Stats still come from JSON (Q93); classes carry behaviour, data carries numbers. This is the source for the draw.io class diagram (Q84, Q108).

```text
«interface» Updatable · Collidable · Rewindable (Memento) · Damageable · Pickable · Interactable
abstract Entity              position, body box, spriteId; update(tick) (Template Method); implements Updatable, Collidable, Rewindable
  ├─ abstract Character      hp, CharacterBuild, hurtbox, hit(), status effects, i-frames; implements Damageable
  │    ├─ abstract Player     input, inventory, coins, keys, dash / dodge roll; startingWeapon() (Factory Method)
  │    │    ├─ Knight
  │    │    ├─ Mage
  │    │    └─ Archer
  │    └─ abstract Enemy     gdx-ai state machine (EnemyState), detection, loot
  │         ├─ abstract MeleeEnemy     → Rat, Zombie, Orc
  │         ├─ abstract RangedEnemy    → SkeletonArcher, DarkMage, Alchemist (createProjectile(), Factory Method)
  │         ├─ abstract SummonerEnemy  → Necromancer, RatNest (summon through the EnemyFactory)
  │         └─ abstract Boss (phases)  → KingOfRats, KingOfOrcs, KingOfSkeletons (BossPhase, State)
  ├─ abstract Projectile     → Arrow, Fireball, PoisonZone (each owns a Hitbox)
  ├─ abstract Trap           → SpikeTrap
  ├─ Wall, TeleportZone
  ├─ Chest, Door, Shopkeeper   implement Interactable; the Shopkeeper is never Damageable
  └─ ItemPickup, Coin, Key     implement Pickable
```

```text
«interface» BuildModifier    modify(build): CharacterBuild
abstract Item                id, nameKey, spriteId, rarity, price
  ├─ abstract Equipment      implements BuildModifier
  │    ├─ abstract Weapon    «Strategy» implements Attack: attack(), hit(); owns the attack hitbox
  │    │    ├─ MeleeWeapon     Knight
  │    │    ├─ MagicWeapon     Mage
  │    │    └─ RangedWeapon    Archer (bow, fires Arrow)
  │    └─ Armor              adds defense
  ├─ abstract Consumable     «Command» use(user): boolean
  │    ├─ Potion
  │    └─ RouletteTicket
  └─ abstract PassiveItem    implements BuildModifier; wraps Stats, Attack, Hurtbox, Hitbox, Movement (Decorator)
```

What each level adds, and the pattern it shows:

- **Interfaces:** capabilities that cut across the tree (a Chest is Collidable but not Damageable; the Shopkeeper is never Damageable). Rewindable is the Memento originator for the R feature.
- **Entity:** what every object in the world shares. Template Method: update(tick) calls hooks the subclasses fill in.
- **Character:** what can fight: stats, HP, damage formula (1.6), status effects (burn, poison).
- **Enemy and its families:** shared AI and drops; each family fixes how it attacks (contact, projectile, summon). State pattern through the gdx-ai state machine. Enemies are built by an EnemyFactory from JSON, which also applies the floor multiplier to their stats (Factory, 1.6).
- **Concrete enemies:** only what differs (the Rat Nest is static, the Alchemist leaves a poison zone). Numbers stay in JSON.
- **Boss:** phases on top of Enemy (State); each king adds its own patterns (Q53, TBD).
- **Items:** Strategy for weapons, Command for consumables, Decorator for passive items (1.4). A passive item can wrap any stat or behaviour, not only numbers (see below).

**Passive items as Decorators (decided, Ange, 4 Nov):** a passive item can change any stat and add any effect. Each Character exposes its parts through small interfaces, and a passive item wraps one or more of them. The wrapper calls the wrapped object, then changes the result or adds to it, so items stack by wrapping in pickup order (Q32).

| Component (interface) | What a passive item can change | Example |
| --- | --- | --- |
| `StatSource` | any stat: HP, Attack, Defense, Speed, Attack Speed, Luck | +20 % move speed; attack speed ×1.5 |
| `Attack` (wraps the weapon Strategy) | what a hit does: on-hit effects, extra projectiles, range | fire effect: every hit also applies burn |
| `HurtboxSource / HitboxSource` | the holder's hurtbox (where it is hit) or its attacks' hitbox (where they deal damage) | smaller hurtbox, harder to hit; bigger attack hitbox, wider swing |
| `Movement` | how the holder moves: speed curve, dash, knockback taken | longer dash; no knockback |

```java
// Pattern: Decorator. Adds burn to every hit of the wrapped attack.
final class FireTouch extends AttackDecorator {
  private final BurnSpec burn; // from data/items.json (Q93, Q129)
  FireTouch(Attack inner, BurnSpec burn) { super(inner); this.burn = burn; }
  @Override public HitResult hit(Character attacker, Character target) {
    HitResult result = inner.hit(attacker, target);
    target.applyStatus(new Burn(burn));
    return result;
  }
}
```

Testable rules (1.4.1): removing a passive item restores the holder's stats and behaviour exactly; two copies of the same item stack by their rule (additive or multiplicative, per item in JSON); decorator order is the pickup order, and the same order gives the same result; a fire-effect item applies burn on every hit and on nothing else.

**Item UML (proposal, Ange's Claude, 5 Nov; local file uml/items.drawio):**

- Every Character holds a CharacterBuild record (attack, stats, hurtbox, hitbox, movement). Equipment and PassiveItem implement BuildModifier: modify(build) returns a new build. After every inventory change the character rebuilds: base build (class stats from JSON, Unarmed attack) → weapon → armor → passive items in pickup order. "Removing an item restores stats exactly" and "same order, same result" then hold by construction.
- Attack speed is a stat (ATTACK\_SPEED, set by the weapon), so passive items can change it; the Player tracks the cooldown and weapons stay stateless.
- equip() / unequip() live in Inventory, which owns the 2 portrait slots. Slots 0–2 are the hotbar (consumables only), 3–19 take any item. Proposal: drop(slot), so a full inventory can free a slot.
- Hitboxes and projectiles call owner.hit(target); the Player sends it through its Attack chain, so a fire item also burns through arrows and fireballs.
- A LootTable (weighted entries, a no-drop weight, the seeded RandomSource) serves enemies, bosses, chests and the roulette. The roulette screen spends the RouletteTicket; using it from the hotbar does nothing.
- Coins and keys are pickups, not Items: they are counters on the Player (keys: proposal).

Testable rules (1.4.1): every Character dies at HP ≤ 0; a non-Damageable entity never loses HP; a summoned enemy never drops loot (Q16).

**Entity UML (proposal, Ange's Claude, 5 Nov; local file uml/entities.drawio, corrected from the team's Entities\_UML.drawio):**

- Capabilities go only where they apply: Entity implements Updatable, Collidable and Rewindable; Character implements Damageable, so chests and the shopkeeper can never be hit; ItemPickup, Coin and Key implement Pickable (team); Chest, Door and Shopkeeper implement Interactable (new: what the interact key opens).
- Combat: a Hitbox (shape, owner, ticks left, targets already hit) hits a Damageable hurtbox, then calls owner.hit(target). The Player goes through its Attack chain; enemies use the damage formula directly (1.6). Projectiles own a Hitbox.
- Enemy families fix how they attack: MeleeEnemy uses a contact hitbox; RangedEnemy.createProjectile() is a Factory Method (Arrow, Fireball + burn, PoisonZone); SummonerEnemy summons through the EnemyFactory with summoned = true; Boss runs BossPhase objects (State, Q53 TBD). The roster's states live in one EnemyState enum (gdx-ai State).
- Rat, Zombie, Orc, RatNest and Necromancer have no code of their own (data/enemies.json); the Rat Nest is static because its speed is 0.
- Status effects: StatusEffect.tick() (Template Method), with Burn (BurnSpec) and Poison (PoisonSpec).
- Door states: OPEN, CLOSED, KEY\_LOCKED (opens with a key, Q80), SEALED (arena room, Q13). Coins and keys go to counters on the Player when touched (proposal).
- Rewind: every tick, each entity writes its own state into a primitive-array StateBuffer (Memento, Q86).
- Open: Knight, Mage and Archer only differ by stats and starting weapon (one Player class + a class id in JSON would also work); walls as entities or as tiles of the collision layer.

### 1.4 Gameplay Elements

**Item types (decided by the team):**

| Type | What it does | OOP fit (1.3.1) |
| --- | --- | --- |
| Weapon | Defines the attack and its hitbox (melee, magic or ranged; Q25, plus the Archer, 4 Nov) | Strategy: the weapon is the player's attack behaviour |
| Consumable | One-time effect, e.g. potion | Command: `use()` applies an effect, then the item is gone |
| Armor | Raises defense or resistances | Changes stats, like a passive item |
| Passive item (modifier) | Permanent stat or behaviour change while held | Decorator: wraps any of the holder's stats or behaviours (attack effects such as fire, hurtbox or hitbox size, attack speed, movement); see 1.3.1 |

All four share an `Item` base type (name, sprite, rarity, price). The roulette ticket (Q30, Q35) gives one free roulette spin (decided, 4 Nov); it is modelled as a Consumable (proposal). Sources (Q35): enemies drop consumables, coins and roulette tickets; bosses drop a weapon or armor, consumables and coins. In the dungeon run, chests open without a key and passive items spawn only in chests. Arena mode has no chests: passive items drop from enemies or bosses there (which ones: TBD, decided 4 Nov). Keys exist only to open locked doors (Q80). Testable rule (1.4.1): in the dungeon run, a passive item never comes from an enemy, a boss or the shop.

**Shop and coins (decided by the team):** enemies drop coins. After each boss, a shopkeeper appears with 2 vending slots and a roulette:

| Slot | Sells |
| --- | --- |
| 1 | A weapon or an armor |
| 2 | A consumable |
| 3 | Roulette (not a vending slot): a spin costs coins; a roulette ticket gives a free spin |

Open: coin drop rates, prices, the roulette's outcomes and odds, all TBD during balancing. Decided (Ange, 4 Nov): in Arena mode the shopkeeper appears after each boss wave (every 5th wave). Testable rules for 1.4.1: buying with too few coins fails and changes nothing; the roulette with a fixed seed always gives the same result; its outcome odds sum to 100 %; a spin paid with a ticket costs no coins and uses exactly one ticket.

**Combat (from the answers):** weapons are melee (Knight), magic (Mage) or ranged (Archer's bow) (Q25), aimed with the mouse or the keyboard, chosen in Options (Q39). No crits, no chance-based dodge, no block (Q27). After a hit, the target gets invincibility frames (0.3–0.8 s, placeholder, Q28) and knockback for feedback (Q29). Status effects: burn (Dark Mage, fire passive items), lingering poison zone (Alchemist).

**Inventory (decided, Ange, 4 Nov; replaces the Q31 layout):** 20 slots. 3 of them form the hotbar and are reserved for consumables; the other 17 take any item, consumables included. 2 more slots on the character portrait are reserved for the equipped weapon and armor (2.3); an unequipped weapon or armor goes in the 20 like any other item. Items never stack: each item takes one slot. Picking up an item needs a key press (2.4). When all slots are full, the player cannot pick anything up and the item stays on the ground (like Minecraft). Two copies of the same passive item still combine their effects by each item's rule (Q32). No crafting (Q33).

**World elements:** keyless chests that hold passive items; keys that open locked doors (Q80); spike traps with a delay before they activate (Q36); arena-locked areas (Q13); the teleport zone after each boss (Q11).

#### 1.4.1 Rules as Testable Statements

Every rule below becomes a unit test, written first (Q121), in the rules/ test package (Q113). Collected from the whole GDD; the section in brackets holds the context.

**Combat and characters**

- Damage is never below 1 (1.6).
- Resistance is never 0 (1.6).
- The EnemyFactory gives an enemy spawned on floor n its base stats × floorMultiplier(n); the same floor always gives the same stats (1.6).
- Damage happens only when an attack's hitbox overlaps the target's hurtbox (1.5).
- A Character dies at HP ≤ 0; the player at HP 0 ends the run (Q3).
- During invincibility frames or a dodge roll, the player takes no damage (Q28, Q40).
- A non-Damageable entity never loses HP (1.3.1).
- An attack only hits targets inside the weapon's range (Q37).
- A status effect deals its damage every tick for its duration, then ends.
- A summoned enemy never drops loot (Q16).

**Items**

- Using a consumable from the hotbar applies its effect and empties that slot; using an empty slot does nothing (Q37).
- In the dungeon run, a passive item never comes from an enemy, a boss or the shop; Arena mode never spawns a chest (1.4).
- Removing a passive item restores the holder's stats and behaviour exactly (1.3.1).
- Two copies of the same item stack by their own rule; the same pickup order always gives the same result (1.3.1).
- A fire-effect item applies burn on every hit and on nothing else (1.3.1).
- Two items never share a slot (1.4).
- With all 20 slots full, a pickup fails and the item stays on the ground (1.4).
- An item on the ground is picked up only when the player presses the pickup key in range; walking over it does nothing (1.4).
- The 3 hotbar slots accept only consumables; the 17 other slots accept any item (1.4).

**Shop**

- Buying with too few coins fails and changes nothing (1.4).
- With a fixed seed, the roulette always gives the same result; its odds sum to 100 % (1.4).
- A spin paid with a ticket costs no coins and uses exactly one ticket (1.4).

**Levels**

- Every room is reachable from the start room (6.1).
- Loop count = corridors − rooms + 1, equal to the configured 2 or 3 (6.1).
- The same seed always gives the same floor (6.1).
- Every locked door's key is reachable from the start without crossing that door (proposal, from the cyclic research).
- When a boss dies, the teleport zone opens (Q11).
- In Arena mode, every 5th wave is a boss wave, and the shop appears only after a boss wave is cleared (1.2).
- In Arena mode, the next normal wave starts 15 s after the last one is cleared; after the shop, it starts only when the player activates it (1.2).
- Arena score = number of waves defeated (1.2).

**Engine and data**

- Rewind N ticks, then the state equals the snapshot taken N ticks earlier (1.1.2).
- Switching renderer leaves the game state unchanged (1.1.2).
- A published event reaches every subscriber; the emitter holds no reference to the receiver (1.1.2).
- JSON → entity → JSON gives the same data; a reload changes stats on live entities (1.1.2).
- The game runs and its tests pass with any debug feature disabled (1.1.2).
- Every JSON data file loads (Q129); both language files hold the same keys (5.3).
- core never uses graphics, audio, input, Gdx statics, Math.random or the system clock (ArchUnit, Q100, Q114).

### 1.5 Game Physics

- **Time step:** fixed 60 logic updates per second, separate from rendering (Q42); needed by rewind and determinism.
- **Movement:** 8 directions on the keyboard, or the gamepad stick (Q38, Q43). Keys can be remapped (Q43).
- **Aim:** mouse or arrow keys, switched in Options (decided, 4 Nov; Q39); right stick on the gamepad.
- **Dash:** fast, straight-line move; the player can still be hit. **Dodge roll:** normal speed, invincible. Different effects and distances; durations and cooldowns are placeholders (Q40).
- **Collision:** axis-aligned bounding boxes (AABB) (Q41). Decided (Ange, 4 Nov): hurtbox and hitbox are separate. The **hurtbox** is where a Character can be hit; the **hitbox** is the area of an attack (melee swing, projectile, contact attack) that deals damage. Damage happens only when an attacker's hitbox overlaps a target's hurtbox. Walls block a separate body box at the feet (proposal). Proposal: our own AABB code in core rather than Box2D, so it stays simple and testable.
- **Knockback:** a push on every hit, for feedback (Q29). Open: does it depend on weight?
- **Projectiles:** move every tick; their hitbox hits the first Damageable hurtbox it touches (proposal).

### 1.6 Statistics

**Damage formula (decided, 4 Nov; Q26):** `damage = max(1, (attack + bonus) × buff / resistance)`, then `hp -= damage`. No crits, no chance-based dodge and no block (Q27); the dodge roll (Q40) is an input action, not a stat. Player stats: HP, Attack, Defense, Speed, Attack Speed, Luck (Q44); enemies use the same stat model (Q45). Testable rules (1.4.1): damage is never below 1; resistance is never 0. Enemy hits: see below.

Decided (Ange, 4 Nov): enemies hit the player with the same formula; enemies have no buffs, so buff = 1 on their side. The floor is not in the formula: the EnemyFactory multiplies an enemy's base stats by floorMultiplier when it spawns the enemy (Factory, 1.3.1), so deeper enemies hit harder and take longer to kill.

| Stat | What it does |
| --- | --- |
| HP | health; 0 = death |
| Attack | base damage, plus the weapon |
| Defense | the "resistance" in the damage formula (proposal) |
| Speed | movement speed |
| Attack Speed | attacks per second, from the weapon |
| Luck | TBD: proposal, drop chances and roulette odds |

Scaling (Q47): every floor adds enemies and raises their stats: the EnemyFactory applies floorMultiplier to their base stats at spawn. Progression inside a run comes from items only (Q46); nothing carries over between runs (1.1). Base values: TBD, in JSON.

### 1.7 AI

- **Library:** gdx-ai for everything (Q49): one state machine per enemy (states in the 1.3 roster), behavior trees for bosses (proposal), A\* pathfinding over the labyrinth (Q50).
- **Detection:** line of sight, or a signal sent to the enemies of an area when the player enters it (Q51).
- **Families:** melee enemies chase; ranged enemies keep their distance and shoot; summoners keep away and summon; the Rat Nest stays put and spawns Rats.
- **Cooperation:** surround, flee or call for help only if time allows (Q52).
- **Bosses:** phases per king (Q53, TBD).

Open: a cap on summoned enemies; detection range; what enemies do after losing sight of the player.

## 2. User Interface

**HUD (decided, 4 Nov):** minimal, in the corners, like the pixel dungeon reference: HP bar, the 3 hotbar slots, coin counter, current floor (Q54, Q55). No minimap (Q56), no floating damage numbers (Q57). The inventory (20 slots: 3 hotbar + 17 storage, 1.4) opens as an overlay that pauses the game (Q58).

### 2.1 Flow Chart

```text
Title ── Play ──► Character select ──► Dungeon run ── HP 0 ──► Game over ── Rerun ──► Character select
  │                                     ▲  │                        └─ Quit ──► Title
  ├─ Arena ──► Character select ──► Arena ── HP 0 ──► Game over
  ├─ Options (keys, aim mode, language)  │  │
  └─ Quit                     Resume │  │ pause key
                                     │  ▼
                         Pause (Options, Quit to title)
            inventory key ⇄ Inventory overlay (game paused)
            after a boss  ⇄ Shop overlay (proposal: game paused)
```

There is no victory screen (Q2).

### 2.2 Functional Requirements

| Screen | Must let the player | Source |
| --- | --- | --- |
| Title | Play, Arena, Options, Quit | Q10 |
| Character select | pick Knight, Mage or Archer (stats differ) | Q10, Q15 |
| Options | remap keys, aim mode (mouse / keyboard), switch FR / EN | Q43, Q59 |
| HUD | see HP, the hotbar, coins, current floor | Q54, Q55 |
| Inventory overlay | see and manage the 20 slots (3-slot hotbar + 17 storage) and the weapon and armor slots on the portrait; the game is paused | Q31, Q58 |
| Shop | buy from 2 vending slots, spin the roulette with coins or a ticket | 1.4 |
| Pause | resume, options, quit | Q10 |
| Game over | see depth reached; rerun or quit | Q2, Q10 |

All text comes from the language files (5.3). Menus use Scene2D (3.1.1). Open: whether the score / depth reached shows on the HUD during the run, not only at game over.

### 2.3 Mock-up

HUD at the 960 × 540 virtual resolution, after the pixel dungeon reference (HP top-left, coins top-right, depth bottom-right). Hotbar placement is a proposal.

```
┌──────────────────────────────────────────────────────┐
│ [████████░░] 80%                              ● 125  │
│                                                      │
│                                                      │
│                   (camera follows                    │
│                     the player)                      │
│                                                      │
│                                                      │
│ [1][2][3]                                   Floor 2  │
└──────────────────────────────────────────────────────┘
  HP bar, hotbar (3 consumables)       coins, floor
```

Inventory overlay (decided, 4 Nov; 1.4): the character portrait with its weapon and armor slots on the left, the 17 storage slots and the 3-slot hotbar on the right. The shop and menu mock-ups go in draw.io (Q84).

```
┌──────────── Inventory (game paused) ─────────────┐
│  ┌───────┐   [ ][ ][ ][ ][ ][ ]                  │
│  │  (o)  │   [ ][ ][ ][ ][ ][ ]   17 storage     │
│  │  /|\  │   [ ][ ][ ][ ][ ]                     │
│  │  / \  │                                       │
│  └───────┘   [1][2][3]            3 hotbar       │
│  [Weapon] [Armor]                                │
└──────────────────────────────────────────────────┘
```

### 2.4 Buttons, Icons, Pointers

Default controls. Decided (Ange, 4 Nov): standard keyboard + mouse, and the same actions on a gamepad. The keys below are the usual defaults (each a proposal); the debug keys are decided. All keys can be remapped (Q43).

| Action | Keyboard | Gamepad | Status |
| --- | --- | --- | --- |
| Move (8 directions) | WASD | left stick | proposal (Q38) |
| Aim and attack | mouse + left click, or arrow keys (Options) | right stick | decided: aim mode switched in Options (4 Nov) |
| Dash | Shift | RB | proposal |
| Dodge roll | Space | A | proposal |
| Use consumable 1 / 2 / 3 | 1 / 2 / 3 | d-pad | proposal |
| Interact (chest, door, shop) | E | X | proposal |
| Pick up item | F | Y | pickup on a key decided (4 Nov); the key is a proposal |
| Inventory | I | Select | proposal; TAB is taken by the editor |
| Pause | Esc | Start | proposal |
| Rewind (hold) | R | none | decided (1.1.2) |
| View switcher / event laser | F1 / F2 | none | decided (1.1.2) |
| Hot reload / editor | F5 / TAB | none | decided (1.1.2) |

Mouse: aims in game when mouse aim is on (decided, 4 Nov: Options switches between mouse aim and arrow-key aim) and clicks in menus. Icons and pointer style: TBD with the art.

## 3. Art and Video

### 3.1 Goals, Style, Mood

**Decided:** the underground setting is a deliberate constraint on the visuals. It narrows the palette, tile sets and lighting, which keeps the art scope small and puts the effort on gameplay.

#### 3.1.1 Tech Choices (GUI framework, build, tests, CI)

**Decided:** [libGDX](https://libgdx.com/) is the graphics stack (Java, desktop through the LWJGL3 backend). The brief leaves the GUI tool free, so this is allowed.

libGDX extensions:

- [gdx-ai](https://github.com/libgdx/gdx-ai): A\* pathfinding, state machines and behavior trees for enemy AI
- Tiled map loading (built in) for hand-made rooms
- Scene2D (built in) for the HUD, inventory and menus

Decided (Ange, 4 Nov): gdx-ai for all enemy AI: state machines, behavior trees and A\* pathfinding (Q49, Q50). Our own OOP design shows in entities, items, level generation and the engine features instead. Rendering: 32 × 32 px tiles on a 960 × 540 virtual resolution, scaled ×2 to 1920 × 1080 (30 × 17 tiles on screen, Q60).

**Assets in Git (Ange):** plain Git for now. Git by default stores every version of every file, which bloats the repo with binary assets. Git LFS keeps a small pointer in the repo and the real file on a separate server. Switch to LFS only if music or large spritesheets arrive; LFS, if needed, uses the GitHub Student plan (Q132).

**Build and CI (decided, 4 Nov):** Gradle project generated by gdx-liftoff, Java 21 (Q64). GitHub Actions on every push: spotlessCheck, build, tests (JUnit 5, jqwik, ArchUnit), JaCoCo 70 % on core, SpotBugs + PMD (Q65, Q66, Q125). The code lives in the private school (Epitech) repo (Q128); which allows GitHub Actions and branch protection (checked 4 Nov). Main is protected and only takes commits whose CI is green; with no pull requests, a commit goes to its feature branch first, then to main once green (Q83, Q124).

### 3.2 2D Art and Animation

- **Style:** pixel art, 32 × 32 px tiles, 960 × 540 virtual resolution scaled ×2 to 1080p (Q60). Dark underground palette (3.1); no light radius (Q62).
- **Sources:** AI-generated art and/or licensed asset packs (Q61); every asset and its licence goes in CREDITS (Q130). Risk: AI images and packs mixed together can clash in style, so one palette and one tile size apply to all.
- **Animations per character:** idle, move, attack, hit, death.
- **References:** local visual-ref folder (hud/ for now).

#### 3.2.1 GUI

Minimal corner HUD (2, 2.3), inventory and shop overlays, title, options, pause and game over screens, all built with Scene2D. Open: pixel font choice.

#### 3.2.2 Special Effects

- **Engine views (1.1.2):** X-ray view (hurtboxes and hitboxes in two colours, velocity vectors), high-contrast view, event laser lines.
- **Gameplay effects:** burn on hit, poison zone, summon appearance, hit feedback during invincibility frames (proposal: sprite flash), teleport zone.
- **Rewind:** proposal: a screen tint while R is held.

### 3.3 3D Art and Animation

No 3D: the game is 2D only.

### 3.4 Cinematics

No cinematics; the story is not fixed yet (Q72–Q75).

## 4. Sound and Music

### 4.1 Goals, Style, Format

**Decided (Ange, 4 Nov):** sound effects for hit, death, pickup and UI, plus one ambient music loop for the whole game. All sounds come from free CC0 libraries (e.g. Kenney, OpenGameArt) and are listed in a CREDITS file (Q68–Q71).

### 4.2 Sound Effects

#### 4.2.1 GUI

UI sounds (Q69): menu move and confirm; proposal: shop purchase, roulette spin, item equip.

#### 4.2.2 Special Effects

Hit, death and pickup first (Q69). Proposal if time allows: dash, dodge roll, burn, summon, door, chest.

#### 4.2.3 Environment

None beyond the ambient loop (4.3); footsteps are not a priority (Q69).

### 4.3 Music

One ambient music loop for the whole game (Q70), CC0, listed in CREDITS (Q71). The subsections below all follow from that single loop.

#### 4.3.1 Events

No event music (boss, shop, death); the loop keeps playing.

#### 4.3.2 System Screens

Same loop on menus (proposal: lower volume).

#### 4.3.3 Level Theme

Same loop on every floor; biomes only change the tileset (6.1.1), so one loop fits all; a theme per biome is optional.

#### 4.3.4 Situations

No dynamic combat music (Q70).

#### 4.3.5 Cinematic Soundtrack

No cinematics (3.4).

## 5. Story

### 5.1 Backstory and World

Decided (Ange, 4 Nov): the player is a beginner adventurer who finds a dungeon nobody has explored. As soon as they step inside, the entrance closes behind them. The only way out is to go deeper: each floor's boss guards the way down, and the escape always seems one floor further. This is why the run is endless (Q2): there is no last floor, only how deep you got.

World: underground, floor after floor. Each floor has its own biome (a skin, 6.1.1) and is ruled by one of three kings: the King of Rats, the King of Orcs and the King of Skeletons (Q17). Who built the dungeon and why it seals itself is left untold.

### 5.2 Character Descriptions

Stats, actions and behaviour are in the 1.3 roster. Story roles:

- **The adventurer (player):** a beginner trapped by the sealed entrance; plays as a Knight (melee), a Mage (magic) or an Archer (ranged).
- **The three kings:** each rules one floor and blocks the way deeper.
- **The dungeon's creatures:** rats, zombies, orcs, skeletons, dark mages, alchemists and necromancers, serving the kings.
- **The shopkeeper (proposal):** an adventurer trapped long ago who gave up on escaping and now trades with newcomers after each boss.

### 5.3 Game Text, Dialog Requirements

**Localization (Ange):** the game ships in French and English (Q59). Code never contains player-visible text, only keys; one file per language maps keys to text, loaded with libGDX `I18NBundle` (Q131).

```
label.setText(bundle.format("hud.coins", coins));
```

```
# strings.properties (EN, default)
hud.coins=Coins: {0}
hud.floor=Floor {0}

# strings_fr.properties (FR)
hud.coins=Pièces : {0}
hud.floor=Étage {0}
```

- Key format: `area.element.detail` (`hud.floor`, `menu.options.title`, `enemy.zombie.name`).
- Testable rule (1.4.1): both files contain exactly the same keys, so a missing translation fails CI.

### 5.4 Sample Scripts

No dialog trees. Player-facing text is limited to the intro line, HUD labels, menus, shop, inventory and game over, all through I18N keys (5.3). Sample lines (proposal):

| Key | EN | FR |
| --- | --- | --- |
| story.intro | The entrance seals behind you. The only way out is down. | L'entrée se referme derrière vous. La seule sortie est plus bas. |
| hud.floor | Floor {0} | Étage {0} |
| shop.greeting | Still alive? Let's trade. | Encore en vie ? Faisons affaire. |
| shop.no\_coins | Not enough coins. | Pas assez de pièces. |
| gameover.depth | You reached floor {0}. | Vous avez atteint l'étage {0}. |
| arena.score | Waves defeated: {0} | Vagues vaincues : {0} |

## 6. Level Requirements

### 6.1 Level Diagrams

**Decided (Ange, 4 Nov): 2–3 floors are hand-made (Tiled) for the demo and for tests; after them, the endless run continues on procedurally generated floors (Q2, Q77). Generator (decided):** generate floors with a BSP tree, then add 2 or 3 loops between rooms. This gives the open, interconnected labyrinth feel of classic Doom 1 and 2 maps instead of a dead-end tree.

Why the tweak is needed: plain BSP connects rooms as a tree, so there is exactly one path between any two rooms and no loops ([discussion](https://backiee.wasmer.app/http_gamedev_stackexchange_com/a/82066)). Romero's maps rely on loops, a big central area and views into other areas ([eev.ee](https://eev.ee/blog/2015/12/30/you-should-make-a-doom-level-part-2/)).

Algorithm sketch:

1. Split the floor rectangle recursively until leaves reach a minimum size; place one room per leaf.
2. Connect sibling leaves bottom-up with corridors. The result is a spanning tree: every room reachable, no loops.
3. Loop pass: list room pairs that are close in space but not connected. Score each pair by its path length in the tree (BFS). Carve corridors for the 2 or 3 best pairs above a minimum score (e.g. at least 4 rooms apart), so each loop is a real shortcut, not a tiny square.
4. Optional Doom touches: make the biggest room a hub, and put a key-locked door on one loop.

Testable rules (feed 1.4.1):

- Every room is reachable from the start room.
- Loop count = corridors − rooms + 1, and it equals the configured 2 or 3.
- The same seed always produces the same floor.

OOP fit: a `LevelGenerator` interface (Strategy) with `BspLoopGenerator` and `TiledMapLoader` lets procedural and hand-made floors coexist.

Settled: continuous labyrinth with a scrolling camera (Q76). Decided (Ange, 4 Nov): BSP + 2–3 loops for the procedural floors. The cyclic graph grammar stays as background reading only.

Further reading: cyclic generation in Unexplored, which builds levels around loops on purpose ([Boris the Brave](https://www.boristhebrave.com/2021/04/10/dungeon-generation-in-unexplored/)); Romero's level rules on [DoomWiki](https://doomwiki.org/wiki/John_Romero).

#### 6.1.1 Flow Diagrams

Flow of one floor (hand-made or generated):

```text
Start ──► labyrinth with 2–3 loops (6.1) ──► boss area ──► boss fight
             │   ├─ arena-locked areas (Q13)                       │
             │   ├─ chests: passive items                          ▼
             │   ├─ spike traps (Q36)              teleport zone + shopkeeper
             │   └─ key ──► locked door (Q80)                       │
             │                                                     ▼
             └─────────────────────────────────────── next floor (no way back, Q12)
```

The hand-made floors are drawn in Tiled. Decided (Ange, 4 Nov; Q78, Q79): every floor uses the same room types; a biome only changes the skin (tileset), and biomes come in no fixed order. Room types:

| Room | Holds |
| --- | --- |
| Arena room | open fight area, enemies roam |
| Arena with closed doors | doors lock until every enemy inside is dead (Q13) |
| Corridor | links rooms; the 2–3 loops run through them (6.1) |
| Chest room | a keyless chest with a passive item (1.4) |
| Key room | the key for a locked door (Q80) |
| Boss room | the floor's boss, then the teleport zone and the shopkeeper (Q11, Q14) |

TBD during balancing: how many rooms per floor, and how many of each type.

### 6.2 Asset Revelation Schedule

When each enemy first appears (proposal). Bosses have no fixed floor (decided, Ange, 4 Nov): each floor draws one of the three kings.

| Where | New enemies | Boss |
| --- | --- | --- |
| Arena mode | Rat, Zombie, Skeleton Archer, Rat Nest (decided, 1.1.1) | a king every 5th wave |
| Floor 1 | Rat, Rat Nest, Zombie | one king, drawn |
| Floor 2 | + Orc, Skeleton Archer | one king, drawn |
| Floor 3 | + Dark Mage, Alchemist, Necromancer | one king, drawn |
| Floor 4+ (procedural) | all, scaled by floor (Q47) | one king, drawn |

Items: the class's starting weapon (sword, staff or bow, proposal) and a potion at the start; armor from the first boss; passive items from the first chest on floor 1, or from drops in Arena mode (proposal).
