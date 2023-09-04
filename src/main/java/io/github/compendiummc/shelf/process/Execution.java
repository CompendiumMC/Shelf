package io.github.compendiummc.shelf.process;

import java.nio.file.Path;
import java.util.Collection;

public sealed interface Execution<E extends ExecutionResult> permits CompileExecution, PatchExecution {

  static Execution<PatchResult> patch(Path paperClipJar) {
    return new PatchExecution(paperClipJar);
  }

  static Execution<CompileResult> compile(
      Path workingDir,
      Path nativeImagePath,
      Path nativeImageConfiguration,
      Path paperJar,
      Collection<Path> libraries
  ) {
    return new CompileExecution(workingDir, nativeImagePath, nativeImageConfiguration, paperJar, libraries);
  }

  E awaitResult() throws ExecutionFailedException;
}
