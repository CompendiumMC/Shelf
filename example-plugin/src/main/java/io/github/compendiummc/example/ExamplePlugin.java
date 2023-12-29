package io.github.compendiummc.example;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.nativeimage.ImageInfo;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class ExamplePlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    getComponentLogger().info(text("Hey! ").color(NamedTextColor.YELLOW)
        .append(message()));
  }

  @NotNull
  private static TextComponent message() {
    try {
      if (ImageInfo.inImageRuntimeCode()) {
        return text("We're running in a native executable!").color(NamedTextColor.GREEN);
      }
    } catch (Exception ignored) {
    }
    return text("We're not running in a native executable :(").color(NamedTextColor.RED);
  }
}
