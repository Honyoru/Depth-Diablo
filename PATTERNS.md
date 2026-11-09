# Design patterns

Planned patterns. Fill the "Where" column as each one lands in the code.

| Pattern        | Purpose                                          | Where |
|----------------|--------------------------------------------------|-------|
| State          | Enemy and player behavior (gdx-ai state machine) | TBD   |
| Strategy       | Level generation, enemy attack styles            | TBD   |
| Observer       | Game events (damage, death, pickup)              | `engine/EventBus`, `engine/EventListener` |
| Factory        | Entity and item creation from JSON               | TBD   |
| Command        | Player input mapped to actions; TriggerBox actions (`TriggerAction`) | TBD   |
| Template Method | Fixed per-tick steps, subclass fills `think()`  | `entity/Entity.update()`, `entity/Character.onUpdate()` |
