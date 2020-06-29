/*
 * Copyright (c) 2020 Zac Sweers
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
package dev.zacsweers.ticktock.lazyrules.runtime;

import java.util.List;

/**
 * An interface for indicating available zone ids. This is used by {@link LazyZoneRulesProvider} via
 * {@link LazyZoneRulesPlugins}.
 */
public interface ZoneIdsProvider {
  /** The timezone data version (e.g. "2020a"). */
  String getVersionId();
  /** A list of zone IDs included in this data set. */
  List<String> getZoneIds();
}
