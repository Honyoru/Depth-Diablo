# Design patterns

Planned patterns. Fill the "Where" column as each one lands in the code.

| Pattern        | Purpose                                          | Where |
|----------------|--------------------------------------------------|-------|
| State          | Enemy and player behavior (gdx-ai state machine) | TBD   |
| Strategy       | Weapons (the player's attack); level generation, enemy attack styles | `combat/Attack` (`item/Weapon`) |
| Observer       | Game events (damage, death, pickup)              | `engine/event/EventBus`, `engine/event/EventListener` |
| Factory        | Entity and item creation from JSON               | TBD   |
| Command        | Player input mapped to actions; TriggerBox actions (`TriggerAction`); consumables | `item/Consumable.use()` (`item/Potion`) |
| Template Method | Fixed per-tick steps, subclass fills `think()`  | `entity/Entity.update()`, `entity/character/Character.onUpdate()` |
