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
package dev.zacsweers.ticktock.jvm.tzdb;

import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import dev.zacsweers.ticktock.runtime.TzdbZoneDataProvider;

/** Entry point for lazy zone rules on JVM. */
public final class JvmTzdbZoneRules {

  /** Initializes the TickTockZoneRulesProvider using a {@link TzdbZoneDataProvider}. */
  public static void init() {
    System.setProperty(
        "java.time.zone.DefaultZoneRulesProvider",
        "dev.zacsweers.ticktock.runtime.TickTockZoneRulesProvider");
    TzdbZoneDataProvider provider = TzdbZoneDataProvider.create();
    TickTockPlugins.setZoneDataProvider(() -> provider);
    TickTockPlugins.setZoneIdsProvider(() -> provider);
  }
}
