/*
 * Copyright (C) 2020 Zac Sweers & Gabriel Ittner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.zacsweers.ticktock.runtime;

import java.util.function.Supplier;

/** Plugins for controlling loading of timezone data in TickTock. */
public final class TickTockPlugins {
  private TickTockPlugins() {}

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

  /** @param provider the supplier for creating a {@link ZoneIdsProvider} to use, null allowed */
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

  /** @param provider the supplier for creating a {@link ZoneDataProvider} to use, null allowed */
  public static void setZoneDataProvider(Supplier<ZoneDataProvider> provider) {
    if (lockdown) {
      throw new IllegalStateException("Plugins can't be changed anymore");
    }
    zoneDataProvider = provider;
  }
}
