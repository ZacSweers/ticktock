package dev.zacsweers.ticktock.lazyrules.runtime;

import java.util.function.Supplier;

public final class LazyZoneRulesPlugins {
  private LazyZoneRulesPlugins() {
  }

  static volatile Supplier<ZoneIdsProvider> zoneIdsProvider;

  /** Prevents changing the plugins. */
  static volatile boolean lockdown;

  /**
   * Prevents changing the plugins from then on.
   *
   * <p>This allows container-like environments to prevent client messing with plugins.
   */
  public static void lockdown() {
    lockdown = true;
  }

  /**
   * Returns true if the plugins were locked down.
   *
   * @return true if the plugins were locked down
   */
  public static boolean isLockdown() {
    return lockdown;
  }

  /** @return the value for handling {@link ZoneIdsProvider}. */
  public static Supplier<ZoneIdsProvider> getZoneIdsProvider() {
    return zoneIdsProvider;
  }

  /**
   * @param provider the supplier for creating a {@link ZoneIdsProvider} to use, null
   * allowed
   */
  public static void setZoneIdsProvider(Supplier<ZoneIdsProvider> provider) {
    if (lockdown) {
      throw new IllegalStateException("Plugins can't be changed anymore");
    }
    zoneIdsProvider = provider;
  }
}
