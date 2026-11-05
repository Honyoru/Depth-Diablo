package com.epitech.depthdiablo.rules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * GDD 1.4.1, "Engine and data": core never touches graphics, audio, input, {@code Gdx} statics,
 * unseeded randomness or the system clock (Q100, Q114). ArchUnit needs a class of its own.
 */
@AnalyzeClasses(
    packages = "com.epitech.depthdiablo",
    importOptions = ImportOption.DoNotIncludeTests.class)
class CoreArchitectureRulesTest {
  @ArchTest
  static final ArchRule CORE_HAS_NO_RENDERING_AUDIO_OR_INPUT_PACKAGES =
      noClasses()
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(
              "com.badlogic.gdx.graphics..",
              "com.badlogic.gdx.audio..",
              "com.badlogic.gdx.scenes..",
              "com.badlogic.gdx.backends..",
              "com.badlogic.gdx.controllers..");

  @ArchTest
  static final ArchRule CORE_HAS_NO_GDX_STATICS_OR_INPUT_TYPES =
      noClasses()
          .should()
          .dependOnClassesThat(
              JavaClass.Predicates.belongToAnyOf(
                  Gdx.class, Input.class, InputProcessor.class, Audio.class, Graphics.class));

  @ArchTest
  static final ArchRule CORE_HAS_NO_UNSEEDED_RANDOMNESS =
      noClasses()
          .should()
          .callMethod(Math.class, "random")
          .orShould()
          .callConstructor(Random.class)
          .orShould()
          .callConstructor(Random.class, long.class)
          .orShould()
          .callMethod(ThreadLocalRandom.class, "current");

  @ArchTest
  static final ArchRule CORE_HAS_NO_SYSTEM_CLOCK =
      noClasses()
          .should()
          .callMethod(System.class, "currentTimeMillis")
          .orShould()
          .callMethod(System.class, "nanoTime");

  @ArchTest
  static final ArchRule CORE_NEVER_WRITES_TO_STANDARD_STREAMS =
      GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
}
