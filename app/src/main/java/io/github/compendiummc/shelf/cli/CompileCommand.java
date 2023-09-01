package io.github.compendiummc.shelf.cli;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import net.jbock.Command;
import net.jbock.Option;
import net.jbock.Parameter;

@Command(name = "shelf-compile")
interface CompileCommand {

  @Parameter(index = 0)
  Path paperclipJar();

  @Option(names = {"--native-image", "-n"})
  Optional<Path> nativeImageExecutable();

  @Option(names = {"--configuration", "-c"})
  Optional<Path> nativeImageConfigurationDirectory();

}
