package io.github.compendiummc.shelf;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class PaperclipJar implements AutoCloseable {
  private static final String SEPARATOR = "\t";
  private final FileSystem fileSystem;

  private PaperclipJar(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public static PaperclipJar forPath(Path path) throws IOException {
    return new PaperclipJar(FileSystems.newFileSystem(path));
  }

  @Override
  public void close() throws IOException {
    this.fileSystem.close();
  }

  public Path paperJar() throws IOException {
    String versionLine = Files.readString(this.fileSystem.getPath("META-INF", "versions.list"));
    String[] parts = versionLine.split(SEPARATOR);
    String part = parts[2].strip();
    return Path.of(part);
  }

  public Collection<Path> libraries() throws IOException {
    try (Stream<String> lines = Files.lines(this.fileSystem.getPath("META-INF", "libraries.list"))) {
      return lines
          .map(l -> l.split(SEPARATOR)[2])
          .map(String::strip)
          .map(Path::of)
          .toList();
    }
  }

}
