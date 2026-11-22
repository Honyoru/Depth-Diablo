package com.epitech.depthdiablo.entity.spawn;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * The class an entry of {@code data/characters.json} builds, read from its {@code "class"} field
 * (Q23): a kind of one family, enemy or player. Several ids can share one kind: they then only
 * differ by their values (Type Object). A name is never used by two families.
 */
public sealed interface CharacterKind permits EnemyKind, PlayerKind {

  /**
   * Returns the name of this kind, as written in the data file.
   *
   * @return its name, e.g. {@code "RAT"}
   */
  String name();

  /**
   * Finds the kind of the given name, among every family.
   *
   * @param name the {@code "class"} of an entry, e.g. {@code "RAT"}
   * @return the kind of that name
   * @throws NullPointerException if {@code name} is null
   * @throws IllegalArgumentException if no family has a kind of that name
   */
  static CharacterKind of(String name) {
    Objects.requireNonNull(name, "name is null");
    return Stream.<CharacterKind[]>of(EnemyKind.values(), PlayerKind.values())
        .flatMap(Stream::of)
        .filter(kind -> kind.name().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No character kind is named " + name));
  }
}
