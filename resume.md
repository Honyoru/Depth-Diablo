# Resume: where we left off (6 Nov 2026, evening)

> Personal handoff note. **Not tracked by git, don't commit it.**

## Done today (all merged on `main`)
| PR | What |
|---|---|
| #6 | UML: DamageFormula + Arena page (ArenaCycle) |
| #7 | `Aabb` record |
| #8 | `DamageFormula` |
| #9 | `Damageable` + `Character` (Template Method `update()` → `think()`) |
| #10 | UML: Hitbox (`owner: Entity`, lifetime in ticks, `update`, `moveTo`) + Trap gets a hitbox |
| #11 | UML: `TriggerBox` + `TriggerAction` (Command: SealDoors, GoToNextFloor, ArmTrap), TeleportZone removed, `Trap.arm()` |
| #12 | UML previews regenerated (Entities) + new `arena-preview.png` |
| #15 | `ArenaCycle` (WAVE / BREAK / SHOP, score, boss every 5th wave) + UML names synced |

## Waiting on someone else
- **#17 "Entity & EntityTest implementation"** (Loic, branch `feature/entity-base`): `Entity.java` + `EntityTest.java`. **Review it first thing.** Check that it matches the UML (Entities page) and that `Character` can extend it.
  - #14 and #16 were his earlier attempts: one opened from `main`, the other closed.

## Next for me
1. **Review #17.** Once it's merged: `git switch feature/hitbox && git fetch && git rebase origin/main`.
2. **Hitbox**, on `feature/hitbox`, following the UML decided in #10:
   - `Hitbox(shape: Aabb, owner: Entity, lifetimeTicks: int)`
   - `update(tick)` counts `ticksLeft` down; `isExpired()` is true at 0.
   - `moveTo(x, y)` replaces `shape` with a new Aabb and keeps `alreadyHit` and `ticksLeft`.
   - `canHit(target)`:
     - Overlaps the hurtbox: yes.
     - Is the owner: no.
     - Already hit: no.
     - Several different targets per hitbox: yes.
   - The owner applies the damage. The arrow calls `remove()` on its first hit.
   - Open question: can an expired hitbox hit? Can a dead target be hit?
3. **`Character` must extend `Entity`** once Entity is merged (owner type for Hitbox).
4. Then: `TriggerBox` + `TriggerAction`, or Item/Consumable/Inventory (needs a `Player`: `use(Player)` is the decision).

## Small leftovers (optional, my own PRs)
- `Aabb`: constructor Javadoc with `@throws`, the touching-edges sentence in `overlaps`, `" : "` → `": "` in the message.
- `Character` / `Damageable`: put the bad values in error messages, add `@param tick`, add the `Pattern: Template Method` Javadoc line.
- `README.md`: the `docs/uml` line says "items, entities". The Arena page and its preview could be added.
- Old local branches can be deleted, since they're all merged: `docs/uml-*`, `feature/aabb`, `feature/character`, `feature/damage-formula`, `feature/arena-cycle`.

## Rules (reminder)
- I write the code **and** the tests. Claude gives skeletons with TODOs, reviews, points out problems, and makes commits/PRs **only when asked**. Never any Claude attribution.
- Branch `type/short-name` from `origin/main`. Commit `test(...)` (red) before `feat(...)` (green). PR, then 1 approval, then Rebase-and-merge.
- Never `git pull`/merge `main` into a feature branch: use `git rebase origin/main`.

## Handy commands
```
# run one test class (not with check, or JaCoCo fails for nothing)
./gradlew :core:test --tests 'com.epitech.depthdiablo.arena.ArenaCycleTest'

# full check before a PR (spotless + tests + JaCoCo 70% + SpotBugs + PMD)
./gradlew spotlessApply && ./gradlew check

# export one UML page to PNG (pages numbered from 1: 1 Entities, 2 Events, 3 Arena)
docker run --rm --shm-size=1g -u $(id -u):$(id -g) -e HOME=/tmp \
  -v $PWD/docs/uml:/in:ro -v $PWD/out:/out rlespinasse/drawio-desktop-headless \
  -x -f png -p 1 -b 20 -o /out/page1.png /in/entities.drawio
```
