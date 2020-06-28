package dev.zacsweers.ticktock.compiler

import com.google.common.truth.Truth
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
      vararg zoneIds: String): SortedMap<String, ZoneRules> {
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
  @Throws(Exception::class)
  fun writeZones() {
    val zones: Map<String, ZoneRules> = generateZones(
        "Europe/Berlin", "US/Pacific", "Zulu")
    for ((key, value) in zones) {
      val file = zoneFile(key)
      Truth.assertThat(Files.exists(file)).isTrue()
      DataInputStream(FileInputStream(file.toFile())).use { input ->
        Truth.assertThat(input.readByte()).isEqualTo(1)
        Truth.assertThat(input.readUTF()).isEqualTo("TZDB-ZONE")
        val readZoneRules = ZoneRulesReaderCompat.readZoneRules(
            input)
        Truth.assertThat(readZoneRules).isEqualTo(value)
      }
    }
  }

  @Test
  @Throws(Exception::class)
  fun clearDirectory() {
    val zones1: Map<String, ZoneRules> = generateZones(
        "Europe/Berlin", "US/Pacific", "Zulu")
    for ((key) in zones1) {
      Truth.assertThat(Files.exists(zoneFile(key))).isTrue()
    }
    val zones2 = generateZones(
        "Europe/Berlin", "US/Pacific")
    for ((key) in zones2) {
      Truth.assertThat(Files.exists(zoneFile(key))).isTrue()
    }
    Truth.assertThat(Files.exists(zoneFile("Zulu"))).isFalse()
  }
}