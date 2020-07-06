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
import java.net.URL;
import java.time.zone.ZoneRulesException;

/** A resources-based {@link ZoneDataLoader}. */
public final class ResourcesZoneDataLoader implements ZoneDataLoader {
  @Override
  public DataInputStream openData(String path) {
    try {
      URL datUrl = ResourcesZoneDataLoader.class.getClassLoader().getResource(path);
      if (datUrl == null) {
        throw new ZoneRulesException("Missing time-zone data: " + path);
      }
      return new DataInputStream(new BufferedInputStream(datUrl.openStream()));
    } catch (Exception ex) {
      throw new ZoneRulesException("Invalid binary time-zone data: " + path, ex);
    }
  }
}
