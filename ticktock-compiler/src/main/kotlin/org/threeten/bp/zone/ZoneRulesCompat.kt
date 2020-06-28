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
package org.threeten.bp.zone

import java.io.DataOutputStream
import java.io.File
import java.util.SortedMap

class ZoneRulesCompat(version: String, sourceFiles: List<File>, leapSecondsFile: File?, verbose: Boolean) {
  private val compiler: TzdbZoneRulesCompiler = TzdbZoneRulesCompiler(version, sourceFiles, leapSecondsFile, verbose)

  init {
    compiler.setDeduplicateMap(mutableMapOf())
  }

  fun compile(): SortedMap<String, ZoneRules> {
    compiler.compile()
    return compiler.zones
  }

  companion object {
    fun writeZoneRules(rules: ZoneRules, stream: DataOutputStream?) {
      (rules as StandardZoneRules).writeExternal(stream)
    }
  }
}
