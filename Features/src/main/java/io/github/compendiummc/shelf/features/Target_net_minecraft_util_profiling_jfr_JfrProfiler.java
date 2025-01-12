package io.github.compendiummc.shelf.features;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;

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
      ChunkPos chunkPos,
      ResourceKey<?> world,
      String targetStatus) {
    return null;
  }

  @Substitute
  public void onPacketSent(ConnectionProtocol state, PacketType<?> packetType, SocketAddress remoteAddress, int bytes) { }

  @Substitute
  public void onPacketReceived(ConnectionProtocol state, PacketType<?> packetType, SocketAddress remoteAddress, int bytes) { }

  @Substitute
  public void onServerTick(float tickTime) { }


  @TargetClass(className = "net.minecraft.util.profiling.jfr.callback.ProfiledDuration")
  public static final class Target_net_minecraft_util_profiling_jfr_callback_ProfileDuration {
  }

}
