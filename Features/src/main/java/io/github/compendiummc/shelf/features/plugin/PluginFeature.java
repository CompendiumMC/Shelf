package io.github.compendiummc.shelf.features.plugin;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.entrypoint.Entrypoint;
import io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader;
import io.papermc.paper.plugin.provider.PluginProvider;
import io.papermc.paper.plugin.provider.configuration.PaperPluginMeta;
import io.papermc.paper.plugin.provider.configuration.type.PermissionConfiguration;
import io.papermc.paper.plugin.provider.configuration.type.PluginDependencyLifeCycle;
import io.papermc.paper.plugin.provider.type.PluginFileType;
import io.papermc.paper.plugin.provider.type.paper.PaperPluginParent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static org.graalvm.nativeimage.hosted.RuntimeClassInitialization.initializeAtBuildTime;

@SuppressWarnings("UnstableApiUsage")
public class PluginFeature implements Feature {

  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    Path pluginsPath = Path.of("plugins");

    class Accessor {
      static final MethodHandle GET_CLASS_LOADER;

      static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
          Field cl = PaperPluginParent.class.getDeclaredField("classLoader");
          cl.setAccessible(true);
          MethodHandle classLoader = lookup.unreflectGetter(cl);
          Field field = findEnclosingInstanceAccessor();
          MethodHandle getEnclosing = lookup.unreflectGetter(field);
          GET_CLASS_LOADER = MethodHandles.filterReturnValue(getEnclosing, classLoader);
        } catch (NoSuchFieldException | IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }

      static ClassLoader getClassLoader(PluginProvider<?> pluginProvider) {
        try {
          return (ClassLoader) GET_CLASS_LOADER.invoke(pluginProvider);
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      }

      private static Field findEnclosingInstanceAccessor() {
        for (Field declaredField : PaperPluginParent.PaperServerPluginProvider.class.getDeclaredFields()) {
          if (declaredField.getType() == PaperPluginParent.class) {
            declaredField.setAccessible(true);
            return declaredField;
          }
        }
        throw new AssertionError("Field not found");
      }
    }

    access.registerAsUsed(JavaPlugin.class);
    RuntimeReflection.register(JavaPlugin.class);
    initializeAtBuildTime(JavaPlugin.class);
    initializeAtBuildTime(PluginBase.class);
    initializeAtBuildTime(Plugin.class);
    initializeAtBuildTime(PluginContainer.class);
    initializeAtBuildTime(PluginContainer.Entry.class);
    initializeAtBuildTime(PluginContainer.SimplePluginProvider.class);
    initializeAtBuildTime(Entrypoint.class);
    initializeAtBuildTime(PaperPluginLogger.class);
    initializeAtBuildTime(PaperPluginParent.class);
    initializeAtBuildTime(PaperPluginParent.PaperBootstrapProvider.class);
    initializeAtBuildTime(PaperPluginParent.PaperServerPluginProvider.class);
    initializeAtBuildTime(PaperPluginMeta.class);
    initializeAtBuildTime(PluginDependencyLifeCycle.class);

    initializeAtBuildTime(PluginLoadOrder.class);

    initializeAtBuildTime(PermissionConfiguration.class);
    initializeAtBuildTime(Permission.class);
    initializeAtBuildTime(PermissionDefault.class);

    initializeAtBuildTime(forName("io.papermc.paper.plugin.provider.type.paper.PaperPluginProviderFactory"));
    System.out.println("JavaPlugin: " + JavaPlugin.class.getClassLoader());
    try (Stream<Path> stream = Files.walk(pluginsPath, 1, FileVisitOption.FOLLOW_LINKS)) {
      Path[] array = stream.filter(p -> Files.isRegularFile(p))
          .toArray(Path[]::new);
      System.out.println(Arrays.toString(array));
      for (Path p : array) {
        JarFile file = new JarFile(p.toFile(), true, JarFile.OPEN_READ, JarFile.runtimeVersion());
        PluginFileType<?, ?> pluginFileType = PluginFileType.guessType(file);
        if (pluginFileType == null) {
          continue;
        }
        PluginMeta config = pluginFileType.getConfig(file);
        PluginContainer.PLUGINS.add(
            () -> {
              PaperPluginClassLoader loader;
              Class<?> main;
              try {
                loader = new PaperPluginClassLoader(PaperPluginLogger.getLogger(config), p, file, (PaperPluginMeta) config, this.getClass().getClassLoader(), new URLClassLoader(new URL[0]));
                main = loader.loadClass(config.getMainClass());
              } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
              }
              return new PluginContainer.Entry<>(
                  Entrypoint.PLUGIN,
                  new PluginContainer.SimplePluginProvider(p, config, main.asSubclass(JavaPlugin.class))
              );
            }
        );
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Class<?> forName(String binaryName) {
    try {
      return Class.forName(binaryName);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

}
