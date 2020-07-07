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

import java.io.InputStream;
import java.time.zone.ZoneRules;

/**
 * An interface for opening a stream to load {@link ZoneRules}. This is can be used by
 * implementations of {@link ZoneDataProvider} to keep the data source abstract.
 */
public interface ZoneDataLoader {
  /** Loads {@link ZoneRules} for a given {@code zoneId}. */
  InputStream openData(String path) throws Exception;
}
