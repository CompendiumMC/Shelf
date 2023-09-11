package io.github.compendiummc.shelf.process;

import java.io.IOException;
import java.nio.file.Path;

record PatchExecution(Path paperclipJar, Path javaPath) implements Execution<PatchResult> {

  @Override
  public PatchResult awaitResult() throws ExecutionFailedException {
    try {
      Process process = new ProcessBuilder(
          javaPath.toString(),
          "-Dpaperclip.patchonly=true",
          "-DbundlerRepoDir=" + paperclipJar().getParent(),
          "-jar", paperclipJar().toString()
      )
          .inheritIO()
          .start();
      int exitCode = process.waitFor();
      return new PatchResult(exitCode);
    } catch (InterruptedException e) {
      // TODO?
      throw new ExecutionFailedException("Execution was interrupted", e);
    } catch (IOException e) {
      throw new ExecutionFailedException("Execution failed", e);
    }
  }

}
