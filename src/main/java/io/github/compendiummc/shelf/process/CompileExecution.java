package io.github.compendiummc.shelf.process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    Stream<Path> librariesStream = libraries().stream();
    String classpathJoined = Stream.concat(Stream.of(paperJar()), librariesStream)
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
          "-o",
          "server",
          "-H:+BuildReport",
          "-Ob", // fast build
          "-H:IncludeResources=log4j2.+",
          "-H:ConfigurationFileDirectories=" + nativeImageConfiguration(),
          "-H:+ReportExceptionStackTraces",
          "-H:IncludeResources=data/.*", // accessed via FileSystem
          "--no-fallback", // we don't want that
          "--enable-monitoring=jfr", // Minecraft uses JFR
          "--enable-url-protocols=https", // allow accessing URLs with https protocol
          "--trace-object-instantiation=com.sun.jmx.mbeanserver.JmxMBeanServer",
          "--initialize-at-build-time=org.apache.logging.log4j",
          "--initialize-at-build-time=org.apache.logging.slf4j",
          "--initialize-at-build-time=com.mojang.logging",
          "--initialize-at-build-time=org.slf4j",
          "--initialize-at-build-time=org.fusesource.jansi",
          "--initialize-at-run-time=io.netty", // TODO can we cut this down?
          "--initialize-at-run-time=net.minecraft.core.registries.BuiltInRegistries",
          "@" + CLASSPATH_FILE,
          "org.bukkit.craftbukkit.Main" // entry point via main class
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
