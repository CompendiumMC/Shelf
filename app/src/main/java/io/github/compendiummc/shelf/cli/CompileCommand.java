package io.github.compendiummc.shelf.cli;

import java.nio.file.Path;

import net.jbock.Command;
import net.jbock.Parameter;

@Command(name = "shelf-compile")
interface CompileCommand {

    @Parameter(index = 0)
    Path paperclipJar();

}
