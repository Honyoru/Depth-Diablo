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
| `docs/uml` | draw.io class diagrams (items, entities) with PNG previews. |
| `config` | PMD and SpotBugs rules. |

## Commands

```bash
./gradlew spotlessApply   # format the code (google-java-format, 100 columns)
./gradlew check           # format check, tests, coverage gate, SpotBugs, PMD (what CI runs)
./gradlew :desktop:run    # launch the game
./gradlew :core:test      # unit, property and architecture tests only
./gradlew :desktop:jar    # runnable jar in desktop/build/libs
```

Requires JDK 21 (Gradle downloads one if missing). VS Code: open the folder and accept the
recommended extensions in `.vscode/`.

## Debug keys (GDD 1.1.2)

`R` (hold) rewind, `F1` view switcher, `F2` event laser, `F5` / `TAB` hot reload and editor.
