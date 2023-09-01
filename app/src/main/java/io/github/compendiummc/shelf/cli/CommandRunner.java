package io.github.compendiummc.shelf.cli;

import io.github.compendiummc.shelf.PaperclipJar;
import io.github.compendiummc.shelf.process.CompileResult;
import io.github.compendiummc.shelf.process.Execution;
import io.github.compendiummc.shelf.process.ExecutionFailedException;
import io.github.compendiummc.shelf.process.PatchResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public class CommandRunner {

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
    Execution<PatchResult> patch = Execution.patch(paperClipJar);
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

    Path nativeImagePath = command.nativeImageExecutable().orElse(Path.of("native-image"));
    Path nativeImageConfiguration = command.nativeImageConfigurationDirectory().orElseThrow();

    Execution<CompileResult> compiled = Execution.compile(workingDir, nativeImagePath, nativeImageConfiguration, paperJarPath, libraries);
    CompileResult compileResult = compiled.awaitResult();
    compileResult.checkForFailure();
  }

}
