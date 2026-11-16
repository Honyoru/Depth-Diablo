package com.epitech.depthdiablo.entity;

/**
 * Something that changes a character's build while it is held: equipment (weapon, armor) and
 * passive items (GDD 1.3.1). After every inventory change the character rebuilds from its base
 * build, then applies the weapon, the armor and the passive items in pickup order, so removing an
 * item restores the build exactly.
 */
public interface BuildModifier {

  /**
   * Returns the build with this modifier applied.
   *
   * @param build the build so far; it is not changed
   * @return a new build, or {@code build} itself if this modifier changes nothing
   * @throws NullPointerException if {@code build} is {@code null}
   */
  CharacterBuild modify(CharacterBuild build);
}
