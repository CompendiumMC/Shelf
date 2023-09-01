package io.github.compendiummc.shelf.process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public record CompileExecution(
    Path workingDir,
    Path nativeImagePath,
    Path nativeImageConfiguration,
    Path paperJar,
    Collection<Path> libraries
) implements Execution<CompileResult> {


  private static final String CLASSPATH_FILE = "classpath";

  @Override
  public CompileResult awaitResult() throws ExecutionFailedException {
    String classpathJoined = libraries().stream()
        .map(Path::toString)
        .collect(Collectors.joining(File.pathSeparator));
    try {
      Files.writeString(workingDir().resolve(CLASSPATH_FILE), "-cp " + classpathJoined);
    } catch (IOException e) {
      throw new ExecutionFailedException("Failed to write classpath file", e);
    }
    // --initialize-at-run-time=<class-name>
    try {
      Process process = new ProcessBuilder(
          nativeImagePath.toString(),
          "-H:+BuildReport",
          "-Ob", // fast build
          "-H:IncludeResources=log4j2.+",
          "-H:ConfigurationFileDirectories=" + nativeImageConfiguration(),
          "-H:+ReportExceptionStackTraces",
          "--trace-object-instantiation=com.sun.jmx.mbeanserver.JmxMBeanServer",
          "--initialize-at-run-time=io.netty", // TODO can we cut this down?
          "@" + CLASSPATH_FILE,
          "-jar", paperJar().toString()
      )
          .inheritIO()
          .directory(workingDir().toFile())
          .start();
      int exitCode = process.waitFor();
      return new CompileResult(exitCode);
    } catch (InterruptedException e) {
      // TODO?
      throw new ExecutionFailedException("Execution was interrupted", e);
    } catch (IOException e) {
      throw new ExecutionFailedException("Execution failed", e);
    }
  }
}
