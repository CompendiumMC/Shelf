package io.github.compendiummc.shelf.process;

import java.nio.file.Path;

public sealed interface Execution<E extends ExecutionResult> permits PatchExecution {

    static Execution<PatchResult> patch(Path paperClipJar) {
        return new PatchExecution(paperClipJar);
    }

    E awaitResult() throws ExecutionFailedException;
}
