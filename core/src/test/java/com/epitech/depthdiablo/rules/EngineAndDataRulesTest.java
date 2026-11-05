package com.epitech.depthdiablo.rules;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.badlogic.gdx.utils.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** GDD 1.4.1, "Engine and data": every JSON file loads, both language files share keys. */
class EngineAndDataRulesTest {
  private static Path assetsDir() {
    return Path.of(System.getProperty("assets.dir"));
  }

  @Test
  void should_load_every_json_data_file_when_the_data_folder_is_read() throws IOException {
    List<Path> files;
    try (Stream<Path> paths = Files.walk(assetsDir().resolve("data"))) {
      files = paths.filter(path -> path.toString().endsWith(".json")).sorted().toList();
    }
    JsonReader reader = new JsonReader();

    assertFalse(files.isEmpty(), "assets/data must hold at least one JSON file");
    for (Path file : files) {
      String content = Files.readString(file, UTF_8);
      assertDoesNotThrow(
          () -> {
            reader.parse(content);
          },
          () -> "Invalid JSON in " + file);
    }
  }

  @Test
  void should_hold_the_same_keys_when_both_language_files_are_compared() throws IOException {
    Set<String> english = keysOf("strings.properties");
    Set<String> french = keysOf("strings_fr.properties");

    assertEquals(english, french);
  }

  private static Set<String> keysOf(String fileName) throws IOException {
    Properties properties = new Properties();
    Path file = assetsDir().resolve("i18n").resolve(fileName);
    try (Reader reader = Files.newBufferedReader(file, UTF_8)) {
      properties.load(reader);
    }
    return properties.stringPropertyNames();
  }
}
