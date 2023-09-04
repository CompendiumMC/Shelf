package io.github.compendiummc.shelf;

import io.github.compendiummc.shelf.cli.CommandRunner;
import io.github.compendiummc.shelf.process.ExecutionFailedException;

public class App {

    public static void main(String[] args) {
        try {
            new CommandRunner().run(args);
        } catch (ExecutionFailedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
