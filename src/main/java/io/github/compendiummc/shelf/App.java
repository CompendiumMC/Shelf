package io.github.compendiummc.shelf;

import io.github.compendiummc.shelf.cli.CommandRunner;
import io.github.compendiummc.shelf.process.ExecutionFailedException;

import java.nio.file.Path;

public class App {
  public static final Path FEATURES_JAR = Path.of(".build/Features.jar"); // relative path

  public static void main(String[] args) {
    try {
      new CommandRunner().run(args);
    } catch (ExecutionFailedException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
