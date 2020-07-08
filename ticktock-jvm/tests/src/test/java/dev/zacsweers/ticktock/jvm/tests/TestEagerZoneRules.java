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
package dev.zacsweers.ticktock.jvm.tests;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.synchronizedSet;

import dev.zacsweers.ticktock.runtime.EagerZoneRulesLoading;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import dev.zacsweers.ticktock.runtime.ZoneDataProvider;
import java.time.zone.ZoneRulesProvider;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

final class TestEagerZoneRules {
  private TestEagerZoneRules() {}

  /** Calls {@link EagerZoneRulesLoading#cacheZones()} and returns the loaded zone IDs. */
  static void cacheZonesAndAssertLoaded() {
    Supplier<ZoneDataProvider> currentSupplier = TickTockPlugins.getZoneDataProvider();
    ZoneDataProvider currentProvider = currentSupplier.get();
    Set<String> loadedZones = synchronizedSet(new LinkedHashSet<>());
    ZoneDataProvider newProvider =
        zoneId -> {
          loadedZones.add(zoneId);
          return currentProvider.getZoneRules(zoneId);
        };
    Supplier<ZoneDataProvider> newSupplier = () -> newProvider;
    TickTockPlugins.setZoneDataProvider(newSupplier);
    EagerZoneRulesLoading.cacheZones();
    assertThat(loadedZones).containsExactlyElementsIn(ZoneRulesProvider.getAvailableZoneIds());
  }
}
