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

import dev.zacsweers.ticktock.runtime.ResourcesZoneDataLoader;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import dev.zacsweers.ticktock.runtime.ZoneIdsProvider;
import dev.zacsweers.ticktock.runtime.internal.StandardZoneRules;
import dev.zacsweers.ticktock.runtime.internal.Suppliers;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
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
        requireNonNull(TickTockPlugins.getZoneIdsProvider(), "No ZoneIdsProvider registered!");
    return callable.get();
  });

  private final Supplier<ZoneDataLoader> zoneDataLoader = Suppliers.memoize(() -> {
    Supplier<ZoneDataLoader> callable = TickTockPlugins.getZoneRulesLoader();
    if (callable == null) {
      // Default to using resources.
      return new ResourcesZoneDataLoader();
    } else {
      return callable.get();
    }
  });

  public LazyZoneRulesProvider() {
    System.out.println("Initializing LazyZoneRulesProvider");
  }

  @Override protected Set<String> provideZoneIds() {
    return new LinkedHashSet<>(zoneIdsProvider.get()
        .getZoneIds());
  }

  @Override protected ZoneRules provideRules(String zoneId, boolean forCaching) {
    requireNonNull(zoneId, "zoneId");
    ZoneRules rules = map.get(zoneId);
    if (rules == null) {
      rules = loadData(zoneId);
      map.put(zoneId, rules);
    }
    return rules;
  }

  private ZoneRules loadData(String zoneId) {
    String fileName = "tzdb/" + zoneId + ".dat";
    DataInputStream is = null;
    try {
      is = zoneDataLoader.get().openData(fileName);
      return loadData(is);
    } catch (Exception ex) {
      throw new ZoneRulesException("Invalid binary time-zone data: " + fileName, ex);
    } finally {
      close(is);
    }
  }

  private ZoneRules loadData(DataInputStream dis) throws Exception {
    if (dis.readByte() != 1) {
      throw new StreamCorruptedException("File format not recognised");
    }

    String groupId = dis.readUTF();
    if (!"TZDB-ZONE".equals(groupId)) {
      throw new StreamCorruptedException("File format not recognised");
    }
    return StandardZoneRules.readExternal(dis);
  }

  private void close(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
    String versionId = zoneIdsProvider.get()
        .getVersionId();
    ZoneRules rules = provideRules(zoneId, false);
    return new TreeMap<>(Collections.singletonMap(versionId, rules));
  }
}
