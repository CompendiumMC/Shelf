package io.github.compendiummc.shelf.features;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * Required workaround because showing the GUI crashes the server
 */
@TargetClass(className = "net.minecraft.server.dedicated.DedicatedServer")
public final class Target_net_minecraft_server_dedicated_DedicatedServer {

  @Substitute
  public void showGui() {

  }
}
