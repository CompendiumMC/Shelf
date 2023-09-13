package io.github.compendiummc.shelf.os;

import java.util.Locale;

public class OsSupport {

  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows");
  }
}
