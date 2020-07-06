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
package dev.zacsweers.ticktock.runtime.android;

import android.annotation.TargetApi;
import android.content.Context;
import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.time.zone.ZoneRulesException;

/** An assets-based {@link ZoneDataLoader}. */
@TargetApi(26)
public final class AssetsZoneDataLoader implements ZoneDataLoader {

  public static AssetsZoneDataLoader create(Context context) {
    return new AssetsZoneDataLoader(context.getApplicationContext());
  }

  private final Context context;

  private AssetsZoneDataLoader(Context context) {
    this.context = context;
  }

  @Override
  public DataInputStream openData(String path) {
    try {
      return new DataInputStream(new BufferedInputStream(context.getAssets().open(path)));
    } catch (Exception missingFileEx) {
      throw new ZoneRulesException("Invalid binary time-zone data: " + path, missingFileEx);
    }
  }
}
