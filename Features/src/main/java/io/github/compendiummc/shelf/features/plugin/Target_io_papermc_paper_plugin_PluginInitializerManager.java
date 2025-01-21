package io.github.compendiummc.shelf.features.plugin;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import io.papermc.paper.plugin.PluginInitializerManager;
import joptsimple.OptionSet;

@TargetClass(PluginInitializerManager.class)
public final class Target_io_papermc_paper_plugin_PluginInitializerManager {

  @Substitute
  public static void load(OptionSet set) throws Exception {
    // only init, do not attempt to load any plugins
    PluginInitializerManager.init(set);
  }
}
