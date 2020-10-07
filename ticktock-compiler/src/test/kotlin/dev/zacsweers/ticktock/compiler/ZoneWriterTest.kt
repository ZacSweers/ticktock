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
package dev.zacsweers.ticktock.compiler

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.threeten.bp.zone.ZoneRules
import org.threeten.bp.zone.ZoneRulesProvider
import org.threeten.bp.zone.ZoneRulesReaderCompat
import java.io.DataInputStream
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.SortedMap
import java.util.TreeMap

@RunWith(JUnit4::class)
class ZoneWriterTest {
  private lateinit var outputDir: Path
  private lateinit var zoneWriter: ZoneWriter

  @Before
  fun setup() {
    outputDir = Files.createTempDirectory(null)
    zoneWriter = ZoneWriter(outputDir)
  }

  private fun generateZones(
    vararg zoneIds: String
  ): SortedMap<String, ZoneRules> {
    val zones: SortedMap<String, ZoneRules> = TreeMap()
    for (zoneId in zoneIds) {
      zones[zoneId] = ZoneRulesProvider.getRules(zoneId, false)
    }
    zoneWriter.writeZones(zones)
    return zones
  }

  private fun zoneFile(zoneId: String): Path {
    return outputDir.resolve(ZoneWriter.fileName(zoneId))
  }

  @Test
  fun writeZones() {
    val zones: Map<String, ZoneRules> = generateZones(
      "Europe/Berlin", "US/Pacific", "Zulu"
    )
    for ((key, value) in zones) {
      val file = zoneFile(key)
      assertThat(Files.exists(file)).isTrue()
      DataInputStream(FileInputStream(file.toFile())).use { input ->
        assertThat(input.readByte()).isEqualTo(1)
        assertThat(input.readUTF()).isEqualTo("TZDB-ZONE")
        val readZoneRules = ZoneRulesReaderCompat.readZoneRules(input)
        assertThat(readZoneRules).isEqualTo(value)
      }
    }
  }

  @Test
  fun clearDirectory() {
    val zones1: Map<String, ZoneRules> = generateZones(
      "Europe/Berlin", "US/Pacific", "Zulu"
    )
    for ((key) in zones1) {
      assertThat(Files.exists(zoneFile(key))).isTrue()
    }
    val zones2 = generateZones(
      "Europe/Berlin", "US/Pacific"
    )
    for ((key) in zones2) {
      assertThat(Files.exists(zoneFile(key))).isTrue()
    }
    assertThat(Files.exists(zoneFile("Zulu"))).isFalse()
  }
}
