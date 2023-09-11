package io.github.compendiummc.shelf.process;

public class ExecutionFailedException extends Exception {

  public ExecutionFailedException(String message) {
    super(message);
  }

  public ExecutionFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
