package io.github.compendiummc.shelf.features.plugin;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import io.papermc.paper.plugin.entrypoint.Entrypoint;
import io.papermc.paper.plugin.entrypoint.LaunchEntryPointHandler;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@TargetClass(value = CraftServer.class)
public final class Target_org_bukkit_craftbukkit_CraftServer {

  @Substitute
  public void loadPlugins() throws IOException {
    System.out.println("--------------------");
    System.out.println("Loading plugins now!");
    System.out.println("--------------------");
    System.out.println(JavaPlugin.class);
    System.out.println(JavaPlugin.class.getClassLoader());
    PluginContainer.registerPlugins();
    var registeredProviders = LaunchEntryPointHandler.INSTANCE.get(Entrypoint.PLUGIN).getRegisteredProviders();
    System.out.println(registeredProviders);
  }
}
