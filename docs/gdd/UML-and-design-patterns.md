# UML and Design Patterns

5 Nov 2026 · proposal from Loïc's Claude, for the team to review

The two UML diagrams already show seven GoF patterns. The main gap is Observer: the F2 event laser needs it, and neither diagram has it.

## Diagrams

The diagrams live in [`docs/uml`](../uml). Since 22 Nov they are generated from the compiled classes by [`tools/uml/generate_uml.py`](../../tools/uml/generate_uml.py) (`./gradlew classes`, then `python3 tools/uml/generate_uml.py`), so they show every class with its real fields and methods; re-run it after each pull request. The PNG previews are exported by hand from draw.io. The first drafts of 5–9 Nov, which also hold classes still planned (Dark Mage, chests, doors…), are kept in [`docs/uml/drafts`](../uml/drafts).

| # | Diagram | Editable source |
| --- | --- | --- |
| 0 | Overview: packages and their dependencies | [00-overview.drawio](../uml/00-overview.drawio) |
| 1 | Engine: world, fixed step, events, rewind photo | [01-engine.drawio](../uml/01-engine.drawio) |
| 2 | Entities: what lives in the world | [02-entities.drawio](../uml/02-entities.drawio) |
| 3 | Characters: players, enemies, stats and builds | [03-characters.drawio](../uml/03-characters.drawio) |
| 4 | Character data: characters.json to characters | [04-character-data.drawio](../uml/04-character-data.drawio) |
| 5 | Combat: attacks, hitboxes, damage, status effects | [05-combat.drawio](../uml/05-combat.drawio) |
| 6 | Items: weapons, armor, consumables, passive items | [06-items.drawio](../uml/06-items.drawio) |
| 7 | Item data: inventory, items.json, loot | [07-item-data.drawio](../uml/07-item-data.drawio) |
| 8 | Arena, shop, input, randomness | [08-arena-shop-input.drawio](../uml/08-arena-shop-input.drawio) |
| 9 | Debug features: F1, F2, F5, R | [09-debug.drawio](../uml/09-debug.drawio) |
| 10 | Desktop: application, screens, drawing | [10-desktop-screens.drawio](../uml/10-desktop-screens.drawio) |
| 11 | Desktop: HUD, inventory page, shop page | [11-desktop-hud.drawio](../uml/11-desktop-hud.drawio) |
| 12 | Desktop: sound effects and music | [12-desktop-audio.drawio](../uml/12-desktop-audio.drawio) |

## 1. Pattern correspondence

Each row maps a pattern to the classes that play its roles in the two diagrams.

| Pattern | Family | Diagram | Classes and roles | What it solves | Marked on the UML |
| --- | --- | --- | --- | --- | --- |
| [Decorator](https://refactoring.guru/design-patterns/decorator) | GoF structural | Items | Components: `Attack`, `StatSource`, `HurtboxSource`, `HitboxSource`, `Movement`. Decorators: `AttackDecorator`, `StatDecorator`, `HurtboxDecorator`, `HitboxDecorator`, `MovementDecorator`. Concrete: `FireTouch`, `StatBonus`, `ScaledHurtbox`, `ScaledHitbox`, `DashBoost`. | Stacks passive effects without a subclass per combination. | Yes |
| [Strategy](https://refactoring.guru/design-patterns/strategy) | GoF behavioral | Items | Strategy: `Attack`, `Weapon`. Concrete: `MeleeWeapon`, `RangedWeapon`, `MagicWeapon`. Context: `Character` through `CharacterBuild`. | Changes the attack with the equipped weapon. | Yes |
| [Command](https://refactoring.guru/design-patterns/command) | GoF behavioral | Items | Command: `Consumable.use(user)`. Concrete: `Potion`, `RouletteTicket`. Invoker: `Inventory.use()`. | Lets the hotbar treat every consumable the same way. | Yes |
| [State](https://refactoring.guru/design-patterns/state) | GoF behavioral | Entities | `Enemy` with `EnemyState` (gdx-ai state machine). `Boss` with `BossPhase`. | Separates AI behaviours and boss phases. | Yes |
| [Template Method](https://refactoring.guru/design-patterns/template-method) | GoF behavioral | Entities | `Entity.update()` calls `onUpdate()`. `Character.onUpdate()` calls `think()`. `StatusEffect.tick()` calls `applyTick()`. Also `Trap.strike()`, `Projectile.onHit()`, `Enemy.attackTarget()`. | Fixes the order of an update and leaves the steps to subclasses. | `Entity` only |
| [Factory Method](https://refactoring.guru/design-patterns/factory-method) | GoF creational | Entities | `RangedEnemy.createProjectile()`, `Player.startingWeapon()`, `Boss.createPhases()`. | Lets each subclass choose what it creates. | Not `createPhases()` |
| [Memento](https://refactoring.guru/design-patterns/memento) | GoF behavioral | Entities | Originator: `Rewindable`, `Entity`. Stored state: `StateBuffer`. Caretaker: not drawn. | Rewinds without exposing internal state. | Yes, caretaker missing |
| Simple Factory (not GoF) | Creational idiom | Both | `EnemyFactory`, `ItemFactory`: `definitions`, `create()`, `reload()`. | Builds objects from JSON and supports hot reload. | Named, not stereotyped |
| [Type Object](https://gameprogrammingpatterns.com/type-object.html) | Game pattern | Both | `Rat`, `Zombie`, `Orc`, `RatNest`, `Necromancer` (data only, per the UML note). `StatPassive`. | Adds a type through JSON, without a new class. | No |
| [Update Method](https://gameprogrammingpatterns.com/update-method.html) and [Game Loop](https://gameprogrammingpatterns.com/game-loop.html) | Game pattern | Entities, code | `Updatable`, `Entity.update(tick)`. `FixedTimestep` in `core`. | Advances every object at a fixed tick. | No |
| [Value Object](https://martinfowler.com/bliki/ValueObject.html) | Modeling | Both | Records: `Aabb`, `BurnSpec`, `PoisonSpec`, `LootEntry`, `CharacterBuild`. | Makes shared data immutable and compared by value. | `«record»` only |
| Modifier pipeline (no catalog name) | Project design | Items | `BuildModifier.modify()`, `Inventory.modifiers()`, `CharacterBuild`. | Rebuilds in order: base, weapon, armor, passive items. | Note only |
| Role interfaces (a principle, not a pattern) | Interface segregation | Entities | `Updatable`, `Collidable`, `Rewindable`, `Damageable`, `Interactable`, `Pickable`. | Gives a capability without forcing it on the whole tree. | Not needed |

Do not present these as patterns at the review:

- `Door` with `DoorState` is an enum with conditions, not the State pattern. That is enough for four simple states.
- The `Attack` chain is a Decorator, not a Chain of Responsibility: every link acts, then forwards.
- `CharacterBuild.withX()` is an immutable copy, not a Builder: there is no builder object and no `build()`.
- `FireRing` creating `FireTouch` is not a Factory Method: no subclass overrides the creation.

## 2. Suggestions

These are proposals, highest priority first. Nothing here is decided, and the GDD tab stays unchanged.

| Priority | Suggestion | Where | Why | Verified sources |
| --- | --- | --- | --- | --- |
| 1 | Add Observer: `EventBus`, `GameEvent`, `EventListener`. Deliver events at the end of the tick through a queue. | Entities, new `engine` part | F2 needs it, and it is decided (Q103, Q104). `Door` can unseal on "room cleared". A queue can hide feedback loops instead of crashing, so handlers should not send events. Use an injected `MessageDispatcher` or our own bus, not the `MessageManager` singleton (Q103). | [Observer, Refactoring Guru](https://refactoring.guru/design-patterns/observer) · [Observer, GPP](https://gameprogrammingpatterns.com/observer.html) · [Event Queue, GPP](https://gameprogrammingpatterns.com/event-queue.html) · [Message Handling, gdx-ai](https://github.com/libgdx/gdx-ai/wiki/Message-Handling) |
| 2 | Make `GameEvent` a sealed interface whose events are records carrying source and target. | Same part | The laser needs source and target on every event. The compiler then checks each `switch`. The event list is still open (Q24). | [JEP 409](https://openjdk.org/jeps/409) · [JEP 395](https://openjdk.org/jeps/395) · [JEP 441](https://openjdk.org/jeps/441) |
| 3 | Draw the Memento caretaker, for example `RewindFeature` implementing `DebugFeature` and owning the ring buffer. Restore item ids, then call `rebuild()`. | Entities | The pattern is incomplete without it. The decorator chain cannot go into a primitive buffer, but it can be rebuilt. | [Memento, Refactoring Guru](https://refactoring.guru/design-patterns/memento) |
| 4 | Decide where Command lives. `PATTERNS.md` plans it for player input, the UML has it only on `Consumable`. Proposal: `PlayerInput` produces commands. | Entities, `PATTERNS.md` | `core` stays free of `Input` (Q100), and tests can replay a scripted list of commands. | [Command, GPP](https://gameprogrammingpatterns.com/command.html) · [Command, Refactoring Guru](https://refactoring.guru/design-patterns/command) |
| 5 | Decide on the data-only classes. Remove `Rat`, `Zombie`, `Orc`, `RatNest` and `Necromancer` from the class tree, or mark them as data. Apply the same check to `Knight`, `Mage`, `Archer`. | Entities | The note says data only, but the diagram draws classes. Type Object is the honest name. | [Type Object, GPP](https://gameprogrammingpatterns.com/type-object.html) |
| 6 | Make the Prototype claim true at low cost: a JSON entry can name a `prototype` entry and inherit its values. `reload()` resolves them. | `EnemyFactory`, `ItemFactory` | The showcase table in 1.1.2 says Prototype, but the UML has no `clone()`. The data files are still empty, so the format is cheap to change now. | [Prototype, GPP (Prototypes for Data Modeling)](https://gameprogrammingpatterns.com/prototype.html) · [Prototype, Refactoring Guru](https://refactoring.guru/design-patterns/prototype) |
| 7 | Align `PATTERNS.md` with the UML. It says Strategy for enemy attack styles, the UML uses families (Template Method and Factory Method). Draw `Renderer` and `LevelGenerator` in a further diagram. | `PATTERNS.md`, new diagram | Reviewers will compare the two. Both strategies are decided but appear in neither diagram. | [Strategy, Refactoring Guru](https://refactoring.guru/design-patterns/strategy) · [Design Patterns (GoF), publisher page](https://www.informit.com/store/design-patterns-elements-of-reusable-object-oriented-9780201633610) |
| 8 | Stereotype the unmarked patterns: Template Method outside `Entity`, `createPhases()`, Type Object. | Both | Q102 asks for patterns to be visible. It costs a few labels. | [Template Method, Refactoring Guru](https://refactoring.guru/design-patterns/template-method) · [Factory Method, Refactoring Guru](https://refactoring.guru/design-patterns/factory-method) |
| 9 | Keep Object Pool for later: use it on `Hitbox` and `Projectile` only if GC pauses are measured. | Entities | It adds reset bugs, and rewind must restore pooled objects correctly. | [Object Pool, GPP](https://gameprogrammingpatterns.com/object-pool.html) · [Memory management, libGDX](https://libgdx.com/wiki/articles/memory-management) |

GPP = *Game Programming Patterns*, Robert Nystrom.

## 3. Test patterns (JUnit 5.14 and ArchUnit 1.5.1)

The project already applies five of these. The rest are proposals that fit the rules in `CONTRIBUTING.md`: no Mockito, no AssertJ.

### JUnit

| Test pattern | How it applies here | Status | Verified sources |
| --- | --- | --- | --- |
| Four-Phase Test, Given When Then | Three blocks, one behaviour per test (Q112), as in `FixedTimestepTest`. | In use | [xUnit Patterns](http://xunitpatterns.com/Four%20Phase%20Test.html) · [Fowler](https://martinfowler.com/bliki/GivenWhenThen.html) |
| Testcase Class per Class | One `ClassNameTest` per class (Q113). | In use | [xUnit Patterns](http://xunitpatterns.com/Testcase%20Class%20per%20Class.html) |
| Deterministic seams | Inject `RandomSource` and `FixedTimestep` (Q114). | In use | [Fowler, non-determinism](https://martinfowler.com/articles/nonDeterminism.html) |
| Test Data Builder | `anEnemy().withHp(10).build()` (Q116). | Decided, to write | [Nat Pryce](http://natpryce.com/articles/000714.html) |
| Hand-written test doubles | Stub `RandomSource` with fixed rolls for `LootTable`. Fake `PlayerInput`. Spy listener recording what `EventBus` sends (Q117). | Decided, to write | [Fake Object](http://xunitpatterns.com/Fake%20Object.html) · [Test Stub](http://xunitpatterns.com/Test%20Stub.html) · [Test Spy](http://xunitpatterns.com/Test%20Spy.html) · [Fowler](https://martinfowler.com/bliki/TestDouble.html) |
| Humble Object | Keep `desktop` thin and the logic in `core`. One test per renderer: the state is unchanged (Q118). | Decided, to write | [xUnit Patterns](http://xunitpatterns.com/Humble%20Object.html) · [Fowler](https://martinfowler.com/bliki/HumbleObject.html) |
| Parameterized Test | Damage formula, floor multiplier, door states, one case per JSON id, with `@CsvSource` and `@MethodSource`. | Proposal | [JUnit guide](https://docs.junit.org/5.14.4/writing-tests/parameterized-classes-and-tests.html) · [xUnit Patterns](http://xunitpatterns.com/Parameterized%20Test.html) |
| Interface contract test | `AttackContract`, `RewindableContract`, `RendererContract` with default test methods. Each implementation's test inherits them. | Proposal | [JUnit guide](https://docs.junit.org/5.14.4/writing-tests/test-interfaces-and-default-methods.html) |
| Custom Assertion | `assertSameStats(base, rebuilt)` for "removing an item restores stats exactly" (1.4.1). | Proposal | [xUnit Patterns](http://xunitpatterns.com/Custom%20Assertion.html) |
| Temporary file fixture | Copy a small JSON into `@TempDir`, edit it, call `reload()` (Q116). | Proposal | [JUnit guide](https://docs.junit.org/5.14.4/writing-tests/built-in-extensions.html) |
| Nested contexts | `@Nested` per enemy state or per boss phase. | Optional | [JUnit guide](https://docs.junit.org/5.14.4/writing-tests/nested-tests.html) |
| Round-trip property | jqwik on the JUnit Platform: `writeState`, change the entity, `readState`, then the state must match. | Proposal | [jqwik guide](https://jqwik.net/docs/current/user-guide.html) |

### ArchUnit

The main calls in these snippets were checked in the ArchUnit 1.5.1 source. The snippets were not compiled, because most target classes do not exist yet.

| Test pattern | How it applies here | Status | Verified sources |
| --- | --- | --- | --- |
| Boundary rule | `core` never touches graphics, audio, input or `Gdx` statics (Q100). | In use | [ArchUnit guide](https://www.archunit.org/userguide/html/000_Index.html) |
| Determinism rule | No `Math.random()`, `new Random()` or system clock in `core` (Q114). | In use | [ArchUnit guide](https://www.archunit.org/userguide/html/000_Index.html) |
| Dependency cycle rule | `slices().matching("com.epitech.depthdiablo.(*)..").should().beFreeOfCycles()`. **The UML would fail it today:** `Player` uses `Inventory`, and `Consumable.use()` takes a `Player`. Fix the cycle, or accept it through `ignoreDependency` or a frozen rule. | Proposal, needs a decision | [Slices](https://www.archunit.org/userguide/html/000_Index.html#_slices) · [Freezing](https://www.archunit.org/userguide/html/000_Index.html#_freezing_arch_rules) |
| Feature isolation | `engine` never depends on a debug feature implementation, only on `DebugFeature` (1.1.2). | Proposal | [ArchUnit guide](https://www.archunit.org/userguide/html/000_Index.html) |
| Template Method guard | `methods().that().haveName("update").and().areDeclaredIn(Entity.class).should().beFinal()`. | Proposal | [MethodsShould, v1.5.1](https://github.com/TNG/ArchUnit/blob/v1.5.1/archunit/src/main/java/com/tngtech/archunit/lang/syntax/elements/MethodsShould.java) |
| Factory gate | Only `EnemyFactory` and `ItemFactory` construct these types, with `callConstructorWhere(...)`. Allow the `super()` calls of subclasses. | Proposal | [ClassesShould, v1.5.1](https://github.com/TNG/ArchUnit/blob/v1.5.1/archunit/src/main/java/com/tngtech/archunit/lang/syntax/elements/ClassesShould.java) |
| No singleton event bus | `fields().that().haveRawType(EventBus.class).should().notBeStatic()` (Q103). | Proposal | [FieldsShould, v1.5.1](https://github.com/TNG/ArchUnit/blob/v1.5.1/archunit/src/main/java/com/tngtech/archunit/lang/syntax/elements/FieldsShould.java) |
| Value Object convention | `classes().that().haveSimpleNameEndingWith("Spec").should().beRecords()` (Q97). | Proposal | [ClassesShould, v1.5.1](https://github.com/TNG/ArchUnit/blob/v1.5.1/archunit/src/main/java/com/tngtech/archunit/lang/syntax/elements/ClassesShould.java) |
| Naming convention | `noClasses().should().haveSimpleNameEndingWith("Impl")` (Q90). | Proposal | [ClassesShould, v1.5.1](https://github.com/TNG/ArchUnit/blob/v1.5.1/archunit/src/main/java/com/tngtech/archunit/lang/syntax/elements/ClassesShould.java) |
| Test location | `GeneralCodingRules.testClassesShouldResideInTheSamePackageAsImplementation()` (Q113). It needs tests imported, so a class without `DoNotIncludeTests`. | Proposal | [General coding rules](https://www.archunit.org/userguide/html/000_Index.html#_general_coding_rules) |
| Diagram conformance | After the PlantUML redo (Q108), check code against a **component** diagram with `adhereToPlantUmlDiagram`. Class diagrams are not covered. | Later | [PlantUML rules](https://www.archunit.org/userguide/html/000_Index.html#_plantuml_component_diagrams_as_rules) |

Two ArchUnit limits to plan for:

- A rule that matches no class fails by default, so add each rule when its classes land, or use `allowEmptyShould(true)` ([guide](https://www.archunit.org/userguide/html/000_Index.html#_fail_rules_on_empty_should)).
- ArchUnit reads bytecode, so it cannot check the `Pattern: ...` Javadoc line from Q102 ([guide](https://www.archunit.org/userguide/html/000_Index.html)).

## How the sources were checked

Checked on 5 Nov 2026: each linked page was fetched, and its title or key terms matched the row. The pages were not all read in full. JUnit links point to the guide for 5.14.4, the version in `gradle.properties`. The ArchUnit guide is not versioned, so API calls were also checked in the v1.5.1 source. The GoF publisher page confirms the book only, not page numbers.
