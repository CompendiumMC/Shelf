package io.github.compendiummc.shelf.features.plugin;

import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.entrypoint.Entrypoint;
import io.papermc.paper.plugin.entrypoint.EntrypointHandler;
import io.papermc.paper.plugin.entrypoint.LaunchEntryPointHandler;
import io.papermc.paper.plugin.provider.PluginProvider;
import io.papermc.paper.plugin.provider.configuration.LoadOrderConfiguration;
import io.papermc.paper.plugin.provider.configuration.PaperPluginMeta;
import io.papermc.paper.plugin.provider.entrypoint.DependencyContext;
import io.papermc.paper.plugin.provider.source.ProviderSource;
import io.papermc.paper.plugin.util.EntrypointUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

public class PluginContainer {

  static void registerPlugins() {
    List<Class<? extends JavaPlugin>> plugins = loadPluginsFromFile();
    EntrypointUtil.registerProvidersFromSource(new MySource(), plugins);
    LaunchEntryPointHandler.INSTANCE.enter(Entrypoint.PLUGIN);
  }

  @SuppressWarnings("unchecked")
  private static List<Class<? extends JavaPlugin>> loadPluginsFromFile() {
    Path path = Path.of(".plugins");
    System.out.println(path.toAbsolutePath());
    try (InputStream inputStream = Files.newInputStream(path);
         ObjectInputStream ois = new ObjectInputStream(inputStream)
    ) {
      return (List<Class<? extends JavaPlugin>>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  static class MySource implements ProviderSource<List<Class<? extends JavaPlugin>>> {

    @Override
    public void registerProviders(EntrypointHandler entrypointHandler, List<Class<? extends JavaPlugin>> context) throws Throwable {
      for (Class<? extends JavaPlugin> javaPluginClass : context) {
        entrypointHandler.register(Entrypoint.PLUGIN, new MyPluginProvider(Path.of("placeholder"), javaPluginClass));
      }
    }
  }

  static class MyPluginProvider implements PluginProvider<JavaPlugin> {
    private final Path source;
    private final Class<? extends JavaPlugin> javaPluginClass;

    MyPluginProvider(Path source, Class<? extends JavaPlugin> javaPluginClass) {
      this.source = source;
      this.javaPluginClass = javaPluginClass;
    }

    @Override
    public @NotNull Path getSource() {
      return this.source;
    }

    @Override
    public JarFile file() {
      return null;
    }

    @Override
    public JavaPlugin createInstance() {
      try {
        return this.javaPluginClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public PluginMeta getMeta() {
      try {
        return PaperPluginMeta.create(new BufferedReader(new InputStreamReader(this.javaPluginClass.getResourceAsStream("paper-plugin.yml"))));
      } catch (ConfigurateException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public ComponentLogger getLogger() {
      return ComponentLogger.logger(this.javaPluginClass);
    }

    @Override
    public LoadOrderConfiguration createConfiguration(@NotNull Map<String, PluginProvider<?>> toLoad) {
      return new LoadOrderConfiguration() {
        @Override
        public @NotNull List<String> getLoadBefore() {
          return List.of();
        }

        @Override
        public @NotNull List<String> getLoadAfter() {
          return List.of();
        }

        @Override
        public @NotNull PluginMeta getMeta() {
          return MyPluginProvider.this.getMeta();
        }
      };
    }

    @Override
    public List<String> validateDependencies(@NotNull DependencyContext context) {
      return List.of();
    }
  }
}
