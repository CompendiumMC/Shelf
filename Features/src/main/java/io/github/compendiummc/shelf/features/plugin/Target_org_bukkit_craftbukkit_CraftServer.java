package io.github.compendiummc.shelf.features.plugin;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import io.papermc.paper.plugin.entrypoint.Entrypoint;
import io.papermc.paper.plugin.entrypoint.LaunchEntryPointHandler;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;

import java.io.IOException;

@TargetClass(value = CraftServer.class)
public final class Target_org_bukkit_craftbukkit_CraftServer {

  @Substitute
  public void loadPlugins() throws IOException {
    System.out.println("--------------------");
    System.out.println("Loading plugins now!");
    System.out.println("--------------------");
    PluginContainer.registerPlugins();
    System.out.println(LaunchEntryPointHandler.INSTANCE.get(Entrypoint.PLUGIN).getRegisteredProviders());
    LaunchEntryPointHandler.INSTANCE.enter(Entrypoint.PLUGIN);
    /*List<String> lines = Files.readAllLines(Path.of("plugins.txt"));
    for (var plugin : PluginContainer.PLUGINS) {
      EntrypointUtil.registerProvidersFromSource(new ProviderSource<Object>() {
        @Override
        public void registerProviders(EntrypointHandler entrypointHandler, Object context) throws Throwable {

        }
      });
      LaunchEntryPointHandler.INSTANCE.get(Entrypoint.PLUGIN).register(plugin);
    }
    System.out.println(LaunchEntryPointHandler.INSTANCE.get(Entrypoint.PLUGIN).getRegisteredProviders());
    LaunchEntryPointHandler.INSTANCE.enter(Entrypoint.PLUGIN);*/
  }
}
