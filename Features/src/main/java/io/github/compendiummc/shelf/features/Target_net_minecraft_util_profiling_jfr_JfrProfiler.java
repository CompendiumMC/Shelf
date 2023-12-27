package io.github.compendiummc.shelf.features;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import java.net.SocketAddress;

/**
 * Required workaround as registering custom events crashes on runtime on Windows
 */
@TargetClass(className = "net.minecraft.util.profiling.jfr.JfrProfiler")
public final class Target_net_minecraft_util_profiling_jfr_JfrProfiler {

  @Substitute
  private Target_net_minecraft_util_profiling_jfr_JfrProfiler() {

  }

  @Substitute
  public boolean isAvailable() {
    return false;
  }

  @Substitute
  public Target_net_minecraft_util_profiling_jfr_callback_ProfileDuration onWorldLoadedStarted() {
    return null;
  }

  @Substitute
  public Target_net_minecraft_util_profiling_jfr_callback_ProfileDuration onChunkGenerate(
      Target_net_minecraft_world_level_ChunkPos chunkPos,
      Target_net_minecraft_resources_ResourceKey<?> world,
      String targetStatus) {
    return null;
  }

  @Substitute
  public void onPacketSent(Target_net_minecraft_network_ConnectionProtocol state, int packetId, SocketAddress remoteAddress, int bytes) { }

  @Substitute
  public void onPacketReceived(Target_net_minecraft_network_ConnectionProtocol state, int packetId, SocketAddress remoteAddress, int bytes) { }

  @Substitute
  public void onServerTick(float tickTime) { }


  @TargetClass(className = "net.minecraft.util.profiling.jfr.callback.ProfiledDuration")
  public static final class Target_net_minecraft_util_profiling_jfr_callback_ProfileDuration {
  }

  @TargetClass(className = "net.minecraft.world.level.ChunkPos")
  public static final class Target_net_minecraft_world_level_ChunkPos {
  }

  @TargetClass(className = "net.minecraft.resources.ResourceKey")
  public static final class Target_net_minecraft_resources_ResourceKey<T> {
  }
  @TargetClass(className = "net.minecraft.network.ConnectionProtocol")
  public static final class Target_net_minecraft_network_ConnectionProtocol {
  }
}
