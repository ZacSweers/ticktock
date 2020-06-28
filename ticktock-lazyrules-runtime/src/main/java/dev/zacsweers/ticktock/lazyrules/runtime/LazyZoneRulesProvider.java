/*
 * Copyright (c) 2020 Zac Sweers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.zacsweers.ticktock.lazyrules.runtime;

import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesProvider;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A lazy {@link ZoneRulesProvider}. This should not be used directly and is only public for service
 * loading!
 */
public final class LazyZoneRulesProvider extends ZoneRulesProvider {

  private final NavigableMap<String, ZoneRules> map = new ConcurrentSkipListMap<>();

  private final Supplier<ZoneIdsProvider> zoneIdsProvider = Suppliers.memoize(() -> {
    Supplier<ZoneIdsProvider> callable =
        requireNonNull(LazyZoneRulesPlugins.getZoneIdsProvider(), "No ZoneIdsProvider registered!");
    return callable.get();
  });

  private final Supplier<ZoneRulesLoader> zoneRulesLoader = Suppliers.memoize(() -> {
    Supplier<ZoneRulesLoader> callable = LazyZoneRulesPlugins.getZoneRulesLoader();
    if (callable == null) {
      // Default to using resources.
      return new ResourcesZoneRulesLoader();
    } else {
      return callable.get();
    }
  });

  public LazyZoneRulesProvider() {
  }

  @Override protected Set<String> provideZoneIds() {
    return new LinkedHashSet<>(zoneIdsProvider.get()
        .getZoneIds());
  }

  @Override protected ZoneRules provideRules(String zoneId, boolean forCaching) {
    requireNonNull(zoneId, "zoneId");
    ZoneRules rules = map.get(zoneId);
    if (rules == null) {
      rules = zoneRulesLoader.get()
          .loadData(zoneId);
      map.put(zoneId, rules);
    }
    return rules;
  }

  @Override protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
    String versionId = zoneIdsProvider.get()
        .getVersionId();
    ZoneRules rules = provideRules(zoneId, false);
    return new TreeMap<>(Collections.singletonMap(versionId, rules));
  }
}
