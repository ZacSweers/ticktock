package dev.zacsweers.ticktock.runtime;

import java.util.function.Supplier;

/** Plugins for controlling loading of timezone data in TickTock. */
public final class TickTockPlugins {
  private TickTockPlugins() {
  }

  static volatile Supplier<ZoneIdsProvider> zoneIdsProvider;
  static volatile Supplier<ZoneDataProvider> zoneDataProvider;

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
  static Supplier<ZoneIdsProvider> getZoneIdsProvider() {
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

  /** @return the value for handling {@link ZoneDataProvider}. */
  static Supplier<ZoneDataProvider> getZoneDataProvider() {
    return zoneDataProvider;
  }

  /**
   * @param provider the supplier for creating a {@link ZoneDataProvider} to use, null
   * allowed
   */
  public static void setZoneDataProvider(Supplier<ZoneDataProvider> provider) {
    if (lockdown) {
      throw new IllegalStateException("Plugins can't be changed anymore");
    }
    zoneDataProvider = provider;
  }
}
