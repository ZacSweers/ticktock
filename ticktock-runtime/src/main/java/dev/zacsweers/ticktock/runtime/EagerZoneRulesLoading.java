/*
 * Copyright (C) 2020 Zac Sweers
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

import java.time.ZoneId;
import java.time.zone.ZoneRulesProvider;

/** Utilities for eager zone rule caching. */
public final class EagerZoneRulesLoading {

  /**
   * Call on background thread to eagerly load all zones. Starts with loading {@link
   * ZoneId#systemDefault()} which is the one most likely to be used.
   *
   * <p>NOTE: This is not safe to call on Android without library desugaring, even if your minimum
   * SDK version is 26+.
   */
  public static void cacheZones() {
    ZoneId.systemDefault().getRules();
    for (String zoneId : ZoneRulesProvider.getAvailableZoneIds()) {
      ZoneRulesProvider.getRules(zoneId, true);
    }
  }
}
