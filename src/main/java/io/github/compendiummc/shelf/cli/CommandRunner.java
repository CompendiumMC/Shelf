package io.github.compendiummc.shelf.cli;

import io.github.compendiummc.shelf.App;
import io.github.compendiummc.shelf.PaperclipJar;
import io.github.compendiummc.shelf.process.CompileResult;
import io.github.compendiummc.shelf.process.Execution;
import io.github.compendiummc.shelf.process.ExecutionFailedException;
import io.github.compendiummc.shelf.process.PatchResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Optional;

import static io.github.compendiummc.shelf.os.OsSupport.isWindows;

public class CommandRunner {

  private static final Path BIN = Path.of("bin");
  private static final Path JAVA_EXECUTABLE = BIN.resolve(isWindows() ? "java.exe" : "java");
  private static final Path NATIVE_IMAGE_EXECUTABLE = BIN.resolve(isWindows() ? "native-image.cmd" : "native-image");

  public void run(String[] args) throws ExecutionFailedException {
    ShelfCommand shelfCommand = new ShelfCommandParser().parseOrExit(args);
    System.out.println(shelfCommand.command() + " " + shelfCommand.rest());
    String[] array = shelfCommand.rest().toArray(new String[0]);
    switch (shelfCommand.command()) {
      case "compile" -> handleCompile(array);
      default -> throw new UnsupportedOperationException("TODO");
    }
  }

  private static void handleCompile(String[] array) throws ExecutionFailedException {
    CompileCommand command = new CompileCommandParser().parseOrExit(array);
    Path paperClipJar = command.paperclipJar();
    Path workingDir = paperClipJar.getParent();
    if (workingDir == null) {
      workingDir = Path.of("."); // current dir
    }
    Path javaBaseDir = command.graalHome()
        .or(CommandRunner::findJavaHomeInEnv)
        .orElseGet(CommandRunner::findJavaHomeInProperties);
    Path nativeImagePath = javaBaseDir.resolve(NATIVE_IMAGE_EXECUTABLE);
    Path javaPath = javaBaseDir.resolve(JAVA_EXECUTABLE);
    Execution<PatchResult> patch = Execution.patch(paperClipJar, javaPath);
    PatchResult patchResult = patch.awaitResult();
    patchResult.checkForFailure();

    Path paperJarPath;
    Collection<Path> libraries;
    try (PaperclipJar paperclipJar = PaperclipJar.forPath(paperClipJar)) {
      paperJarPath = Path.of("versions").resolve(paperclipJar.paperJar());
      Path librariesBase = Path.of("libraries");
      libraries = paperclipJar.libraries().stream().map(librariesBase::resolve).toList();
    } catch (IOException e) {
      throw new ExecutionFailedException("Failed to extract version info", e);
    }

    Path nativeImageConfiguration = command.nativeImageConfigurationDirectory().orElseThrow();

    extractFeatures(workingDir);

    Execution<CompileResult> compiled = Execution.compile(workingDir, nativeImagePath, nativeImageConfiguration, paperJarPath, libraries);
    CompileResult compileResult = compiled.awaitResult();
    compileResult.checkForFailure();
  }

  private static void extractFeatures(Path workingDir) {
    try {
      try (InputStream inputStream = CommandRunner.class.getModule().getResourceAsStream("Features.jar")) {
        if (inputStream == null) {
          System.out.println("Features.jar not found in module");
          return;
        }
        Path featuresJar = workingDir.resolve(App.FEATURES_JAR);
        Files.createDirectories(featuresJar.getParent());
        Files.copy(inputStream, featuresJar, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Optional<Path> findJavaHomeInEnv() {
    String javaHome = System.getenv("JAVA_HOME");
    return Optional.ofNullable(javaHome).map(Path::of);
  }

  private static Path findJavaHomeInProperties() {
    return Path.of(System.getProperty("java.home"));
  }

}
