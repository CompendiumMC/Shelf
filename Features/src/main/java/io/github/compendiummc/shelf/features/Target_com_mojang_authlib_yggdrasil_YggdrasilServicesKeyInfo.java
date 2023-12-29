/*
package io.github.compendiummc.shelf.features;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@TargetClass(className = "com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo")
public final class Target_com_mojang_authlib_yggdrasil_YggdrasilServicesKeyInfo {

  @Alias
  private static ScheduledExecutorService FETCHER_EXECUTOR;

  @Substitute
  public static Target_com_mojang_authlib_yggdrasil_ServicesKeySet get(URL url, Target_com_mojang_authlib_minecraft_client_MinecraftClient client) {
    System.out.println("Running replaced get method");
    CompletableFuture<?> ready = new CompletableFuture<>();
    AtomicReference<Target_com_mojang_authlib_yggdrasil_ServicesKeySet> keySet = new AtomicReference<>();
    //noinspection Convert2Lambda: required by native-image
    FETCHER_EXECUTOR.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        System.out.println("Running scheduled task");
        Optional<Target_com_mojang_authlib_yggdrasil_ServicesKeySet> var10000 = fetch(url, client);
        System.out.println("Fetched: " + var10000);
        Objects.requireNonNull(keySet);
        var10000.ifPresent(keySet::set);
        ready.complete(null);
      }
    }, 0L, 24L, TimeUnit.HOURS);
    //noinspection Convert2Lambda: required by native-image
    return Target_com_mojang_authlib_yggdrasil_ServicesKeySet.lazy(new Supplier<>() {
      @Override
      public Target_com_mojang_authlib_yggdrasil_ServicesKeySet get() {
        ready.join();
        return keySet.get();
      }
    });
  }

  @Alias
  public static Optional<Target_com_mojang_authlib_yggdrasil_ServicesKeySet> fetch(URL url, Target_com_mojang_authlib_minecraft_client_MinecraftClient client) {
    throw new AssertionError("aliased method");
  }

  @TargetClass(className = "com.mojang.authlib.yggdrasil.ServicesKeySet")
  public static final class Target_com_mojang_authlib_yggdrasil_ServicesKeySet {

    @Alias
    public static Target_com_mojang_authlib_yggdrasil_ServicesKeySet lazy(Supplier<Target_com_mojang_authlib_yggdrasil_ServicesKeySet> supplier) {
      throw new AssertionError("aliased method");
    }

  }

  @TargetClass(className = "com.mojang.authlib.minecraft.client.MinecraftClient")
  public static final class Target_com_mojang_authlib_minecraft_client_MinecraftClient {
  }
}
*/
