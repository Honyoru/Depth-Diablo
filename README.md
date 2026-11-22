# Depth Diablo

Top-down hack-and-slash in an endless underground labyrinth, built with libGDX, Java 21 and Gradle
(Epitech 2D game project). Team: Ange, Loïc, Romain.
Design decisions: `DECISIONS.md` and the GDD. Workflow and code rules: `CONTRIBUTING.md`.

## Layout

| Folder | Role |
|---|---|
| `core` | Pure game logic: no graphics, audio, input, `Gdx.*`, hidden randomness or clock (ArchUnit). 70 % JaCoCo gate. |
| `desktop` | libGDX LWJGL3 launcher, rendering, input and sound. No coverage gate. |
| `assets` | Sprites, Tiled maps, JSON data (`data/`), i18n bundles (`i18n/`). |
| `docs/uml` | draw.io class diagrams generated from the code (`00-overview` to `12-desktop-audio`), the first drafts completed with the code (`draftremake/`) and the original drafts (`drafts/`). |
| `tools` | `uml/generate_uml.py`: regenerates every class diagram from the compiled classes. |
| `config` | PMD and SpotBugs rules. |

## Commands

```bash
./gradlew spotlessApply   # format the code (google-java-format, 100 columns)
./gradlew check           # format check, tests, coverage gate, SpotBugs, PMD (what CI runs)
./gradlew :desktop:run    # launch the game
./gradlew :core:test      # unit, property and architecture tests only
./gradlew :desktop:jar    # runnable jar in desktop/build/libs
python3 tools/uml/generate_uml.py   # regenerate the UML (after ./gradlew classes)
```

Requires JDK 21 (Gradle downloads one if missing). VS Code: open the folder and accept the
recommended extensions in `.vscode/`.

## Game modes

- **Arena**: waves of enemies in one arena, a break between two waves, a boss every 5th wave, then
  a shop before the next cycle. The score is the number of waves cleared.
- **Endless**: one floor to explore for now; enemies engage when they see you.

## Controls

Keys are physical positions: on a QWERTY keyboard, read W A S D for Z Q S D and Q for A.

| Key | Action |
| --- | --- |
| Z Q S D | Move |
| Left click | Attack towards the mouse |
| Shift | Dash |
| Space | Dodge roll (invincible) |
| E | Pick up the nearest item, or talk to the merchant |
| 1 2 3 | Select a consumable |
| A | Use it |
| I | Inventory (pauses the game) |
| H | Show every control |
| Esc | Back to the menu |

## Debug keys (GDD 1.1.2)

`R` (hold) rewind the last 5 seconds, `F1` view switcher (sprites, X-ray, high contrast), `F2`
event laser, `F5` reload the data files (`items.json`, `characters.json`).
