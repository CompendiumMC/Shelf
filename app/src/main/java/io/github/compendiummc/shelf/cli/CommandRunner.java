package io.github.compendiummc.shelf.cli;

import io.github.compendiummc.shelf.process.Execution;
import io.github.compendiummc.shelf.process.ExecutionFailedException;
import io.github.compendiummc.shelf.process.PatchResult;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

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
        Execution<PatchResult> patch = Execution.patch(command.paperclipJar());
        PatchResult patchResult = patch.awaitResult();
        patchResult.checkForFailure();

        Path paperJarPath;
        try (FileSystem fileSystem = FileSystems.newFileSystem(command.paperclipJar())) {
            paperJarPath = extractPaperJarPath(fileSystem);
        } catch (IOException e) {
            throw new ExecutionFailedException("Failed to extract version info", e);
        }


    }

    private static Path extractPaperJarPath(FileSystem fileSystem) throws IOException {
        String versionLine = Files.readString(fileSystem.getPath("META-INF", "versions.list"));
        String[] parts = versionLine.split("\t");
        String part = parts[2];
        return Path.of(part);
    }
}
