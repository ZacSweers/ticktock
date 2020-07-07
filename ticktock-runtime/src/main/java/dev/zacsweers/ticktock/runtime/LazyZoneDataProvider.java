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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.StreamCorruptedException;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;

/**
 * A lazy-loading {@link ZoneDataProvider}. This loads individual zone rules by zone ID from a
 * {@link ZoneDataLoader} via interpolated "tzdb/{zoneId}.dat" path.
 */
public final class LazyZoneDataProvider implements ZoneDataProvider {

  /** Creates a new lazy {@link ZoneDataProvider} backed by a {@link ResourcesZoneDataLoader}. */
  public static ZoneDataProvider create() {
    return create(ResourcesZoneDataLoader.create());
  }

  /** Creates a new lazy {@link ZoneDataProvider} backed by {@code zoneDataLoader}. */
  public static ZoneDataProvider create(ZoneDataLoader zoneDataLoader) {
    return new LazyZoneDataProvider(zoneDataLoader);
  }

  private final ZoneDataLoader zoneDataLoader;

  private LazyZoneDataProvider(ZoneDataLoader zoneDataLoader) {
    this.zoneDataLoader = zoneDataLoader;
  }

  @Override
  public ZoneRules getZoneRules(String zoneId) {
    String fileName = "tzdb/" + zoneId + ".dat";

    try (DataInputStream is =
        new DataInputStream(new BufferedInputStream(zoneDataLoader.openData(fileName)))) {
      return loadData(is);
    } catch (Exception ex) {
      throw new ZoneRulesException("Invalid binary time-zone data: " + fileName, ex);
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
}
