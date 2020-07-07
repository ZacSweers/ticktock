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
package dev.zacsweers.ticktock.android.tzdb;

import android.annotation.SuppressLint;
import android.content.Context;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import dev.zacsweers.ticktock.runtime.TzdbZoneDataProvider;
import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import dev.zacsweers.ticktock.runtime.android.AssetsZoneDataLoader;
import dev.zacsweers.ticktock.runtime.internal.Suppliers;
import java.util.function.Supplier;

/** Entry point for tzdb zone rules on Android. */
public final class AndroidTzdbZoneRules {

  /** Initializes the TickTockZoneRulesProvider using a {@link TzdbZoneDataProvider}. */
  @SuppressLint("NewApi")
  public static void init(Context context) {
    System.setProperty(
        "java.time.zone.DefaultZoneRulesProvider",
        "dev.zacsweers.ticktock.runtime.TickTockZoneRulesProvider");

    Context applicationContext = context.getApplicationContext();
    Supplier<TzdbZoneDataProvider> supplier =
        Suppliers.memoize(
            () -> {
              ZoneDataLoader zoneDataLoader = AssetsZoneDataLoader.create(applicationContext);
              return new TzdbZoneDataProvider(zoneDataLoader);
            });
    TickTockPlugins.setZoneIdsProvider(supplier::get);
    TickTockPlugins.setZoneDataProvider(supplier::get);
  }
}
