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
package dev.zacsweers.ticktock.jvm.lazyzonerules;

import dev.zacsweers.ticktock.runtime.LazyZoneDataProvider;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;

/** Entry point for lazy zone rules on JVM. */
public final class JvmLazyZoneRules {

  /** Initializes the TickTockZoneRulesProvider using a {@link LazyZoneDataProvider}. */
  public static void init() {
    System.setProperty(
        "java.time.zone.DefaultZoneRulesProvider",
        "dev.zacsweers.ticktock.runtime.TickTockZoneRulesProvider");
    TickTockPlugins.setZoneIdsProvider(GeneratedZoneIdsProvider::new);
    TickTockPlugins.setZoneDataProvider(LazyZoneDataProvider::create);
  }
}
