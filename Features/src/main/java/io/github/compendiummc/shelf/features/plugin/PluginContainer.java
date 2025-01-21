package io.github.compendiummc.shelf.features.plugin;

import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.entrypoint.Entrypoint;
import io.papermc.paper.plugin.entrypoint.LaunchEntryPointHandler;
import io.papermc.paper.plugin.provider.PluginProvider;
import io.papermc.paper.plugin.provider.configuration.LoadOrderConfiguration;
import io.papermc.paper.plugin.provider.configuration.PaperPluginMeta;
import io.papermc.paper.plugin.provider.entrypoint.DependencyContext;
import io.papermc.paper.plugin.provider.type.paper.PaperLoadOrderConfiguration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.jar.JarFile;

@SuppressWarnings("UnstableApiUsage")
public class PluginContainer {
  static final List<Supplier<Entry<?>>> PLUGINS = new ArrayList<>();

  static void registerPlugins() {
    for (var entry : PLUGINS) {
      registerPlugin(entry.get());
    }
    LaunchEntryPointHandler.INSTANCE.enter(Entrypoint.PLUGIN);
  }

  private static <T> void registerPlugin(Entry<T> entry) {
    LaunchEntryPointHandler.INSTANCE.register(entry.entrypoint(), entry.provider());
  }

  @SuppressWarnings("UnstableApiUsage")
  record Entry<T>(Entrypoint<T> entrypoint, PluginProvider<T> provider) {

  }

  record SimplePluginProvider(
      Path source,
      PluginMeta meta,
      Class<? extends JavaPlugin> providedClass
  ) implements PluginProvider<JavaPlugin> {


    @Override
    public @NotNull Path getSource() {
      return source();
    }

    @Override
    public JarFile file() {
      return null;
    }

    @Override
    public JavaPlugin createInstance() {
      try {
        System.out.println(providedClass().getClassLoader());
        System.out.println(providedClass().getClassLoader().getParent());
        return providedClass().newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public PluginMeta getMeta() {
      return meta();
    }

    @Override
    public ComponentLogger getLogger() {
      return ComponentLogger.logger(providedClass()); // TODO cache?
    }

    @Override
    public LoadOrderConfiguration createConfiguration(@NotNull Map<String, PluginProvider<?>> toLoad) {
      return new PaperLoadOrderConfiguration((PaperPluginMeta) meta());
    }

    @Override
    public List<String> validateDependencies(@NotNull DependencyContext context) {
      return List.of(); // TODO
    }
  }
}
