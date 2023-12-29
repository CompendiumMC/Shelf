package io.github.compendiummc.shelf.features.plugin;

import io.papermc.paper.plugin.entrypoint.Entrypoint;
import io.papermc.paper.plugin.entrypoint.EntrypointHandler;
import io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader;
import io.papermc.paper.plugin.provider.PluginProvider;
import io.papermc.paper.plugin.provider.type.PluginFileType;
import io.papermc.paper.plugin.provider.type.paper.PaperPluginParent;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.nativeimage.hosted.Feature;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class PluginFeature implements Feature {

  public PluginFeature() {
    System.out.println("Found plugin PluginFeature!");
  }

  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    List<Class<? extends JavaPlugin>> plugins = new ArrayList<>();
/*    OptionSet optionSet = getOptionSet();
    try {
      PluginInitializerManager.load(optionSet);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }*/
    /*LaunchEntryPointHandler launchEntryPointHandler = LaunchEntryPointHandler.INSTANCE;
    List<Class<? extends JavaPlugin>> plugins = new ArrayList<>();
    for (PluginProvider<JavaPlugin> provider : launchEntryPointHandler.get(Entrypoint.PLUGIN).getRegisteredProviders()) {
      Class<? extends JavaPlugin> clazz = provider.createInstance().getClass();
      access.registerAsUsed(clazz);
      plugins.add(clazz);
      // Target_org_bukkit_craftbukkit_CraftServer.PLUGINS.add(provider);
      System.out.println("registering " + provider);
    }*/
    Path path = Path.of(".plugins");
    Path pluginsPath = Path.of("plugins");

    class Accessor {
      static final MethodHandle GET_CLASS_LOADER;

      static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
          MethodHandle classLoader = lookup.findGetter(PaperPluginParent.class, "classLoader", PaperPluginClassLoader.class);
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
            return declaredField;
          }
        }
        throw new AssertionError("Field not found");
      }
    }
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
        pluginFileType.register(new EntrypointHandler() {
          @Override
          public <T> void register(Entrypoint<T> entrypoint, PluginProvider<T> provider) {
            String mainClass = provider.getMeta().getMainClass();
            ClassLoader classLoader = Accessor.getClassLoader(provider);
            try {
              var main = Class.forName(mainClass, false, classLoader).asSubclass(JavaPlugin.class);
              access.registerAsUsed(main);
              plugins.add(main);
            } catch (ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }

          @Override
          public void enter(Entrypoint<?> entrypoint) {

          }
        }, file, p);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    System.out.println(path.toAbsolutePath());
    try (OutputStream outputStream = Files.newOutputStream(path);
         ObjectOutputStream oos = new ObjectOutputStream(outputStream)
    ) {
      oos.writeObject(plugins);
      System.out.println("Wrote plugins " + plugins);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static OptionSet getOptionSet() {
    OptionParser parser = new OptionParser() {
      {
        this.acceptsAll(List.of("P", "plugins"), "Plugin directory to use")
            .withRequiredArg()
            .ofType(File.class)
            .defaultsTo(new File("plugins"))
            .describedAs("Plugin directory");

        this.acceptsAll(List.of("b", "bukkit-settings"), "File for bukkit settings")
            .withRequiredArg()
            .ofType(File.class)
            .defaultsTo(new File("bukkit.yml"))
            .describedAs("Yml file");
      }
    };
    return parser.parse();
  }
}
