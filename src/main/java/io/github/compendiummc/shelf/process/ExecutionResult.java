package io.github.compendiummc.shelf.process;

public sealed interface ExecutionResult permits CompileResult, PatchResult {

  int exitCode();

  default void checkForFailure() throws ExecutionFailedException {
    if (exitCode() != 0) {
      throw new ExecutionFailedException("Execution failed with exit code " + exitCode());
    }
  }
}
