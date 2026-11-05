package com.epitech.depthdiablo.rules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.epitech.depthdiablo.combat.Damageable;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * GDD 1.4.1, "Combat and stats": a non-Damageable entity never loses HP (1.3.1). Only Damageable
 * types may declare {@code takeDamage}, so a chest or the shopkeeper cannot be hit by mistake.
 */
@AnalyzeClasses(
    packages = "com.epitech.depthdiablo",
    importOptions = ImportOption.DoNotIncludeTests.class)
class CombatArchitectureRulesTest {
  @ArchTest
  static final ArchRule ONLY_DAMAGEABLE_TYPES_TAKE_DAMAGE =
      methods()
          .that()
          .haveName("takeDamage")
          .should()
          .beDeclaredInClassesThat()
          .areAssignableTo(Damageable.class);
}
