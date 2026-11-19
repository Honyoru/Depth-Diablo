package com.epitech.depthdiablo.testdata;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.epitech.depthdiablo.data.DataRegistry;
import com.epitech.depthdiablo.item.ItemFactory;
import com.epitech.depthdiablo.item.Potion;
import com.epitech.depthdiablo.item.Sword;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** The real items of {@code assets/data/items.json}, built by the real {@link ItemFactory}. */
public final class Items {

  private Items() {}

  /**
   * Builds the sword of the data file.
   *
   * @return a new sword with the values of its entry
   */
  public static Sword sword() {
    return (Sword) factory().create("sword");
  }

  /**
   * Builds the small potion of the data file.
   *
   * @return a new potion with the values of its entry
   */
  public static Potion potion() {
    return (Potion) factory().create("potion_small");
  }

  /**
   * Builds the real item factory, reading the data file.
   *
   * @return a factory for every item of the data file
   */
  public static ItemFactory factory() {
    Path file = Path.of(System.getProperty("assets.dir"), "data", "items.json");
    return new ItemFactory(new DataRegistry("items", () -> read(file)));
  }

  private static String read(Path file) {
    try {
      return Files.readString(file, UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
