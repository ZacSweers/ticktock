package dev.zacsweers.ticktock.compiler;

import static com.google.common.truth.Truth.assertThat;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.zone.ZoneRules;
import org.threeten.bp.zone.ZoneRulesProvider;
import org.threeten.bp.zone.ZoneRulesReaderCompat;

@RunWith(JUnit4.class) public class ZoneWriterTest {

  private Path outputDir;
  private ZoneWriter zoneWriter;

  @Before public void setup() throws IOException {
    outputDir = Files.createTempDirectory(null);
    zoneWriter = new ZoneWriter(outputDir);
  }

  private SortedMap<String, ZoneRules> generateZones(String... zoneIds) throws IOException {
    SortedMap<String, ZoneRules> zones = new TreeMap<>();
    for (String zoneId : zoneIds) {
      zones.put(zoneId, ZoneRulesProvider.getRules(zoneId, false));
    }
    zoneWriter.writeZones(zones);
    return zones;
  }

  private Path zoneFile(String zoneId) {
    return outputDir.resolve(ZoneWriter.fileName(zoneId));
  }

  @Test public void writeZones() throws Exception {
    Map<String, ZoneRules> zones = generateZones("Europe/Berlin", "US/Pacific", "Zulu");
    for (Map.Entry<String, ZoneRules> entry : zones.entrySet()) {
      Path file = zoneFile(entry.getKey());
      assertThat(Files.exists(file)).isTrue();
      try (DataInputStream input = new DataInputStream(new FileInputStream(file.toFile()))) {
        assertThat(input.readByte()).isEqualTo(1);
        assertThat(input.readUTF()).isEqualTo("TZDB-ZONE");
        ZoneRules readZoneRules = ZoneRulesReaderCompat.readZoneRules(input);
        assertThat(readZoneRules).isEqualTo(entry.getValue());
      }
    }
  }

  @Test public void clearDirectory() throws Exception {
    Map<String, ZoneRules> zones1 = generateZones("Europe/Berlin", "US/Pacific", "Zulu");
    for (Map.Entry<String, ZoneRules> entry : zones1.entrySet()) {
      assertThat(Files.exists(zoneFile(entry.getKey()))).isTrue();
    }

    SortedMap<String, ZoneRules> zones2 = generateZones("Europe/Berlin", "US/Pacific");
    for (Map.Entry<String, ZoneRules> entry : zones2.entrySet()) {
      assertThat(Files.exists(zoneFile(entry.getKey()))).isTrue();
    }
    assertThat(Files.exists(zoneFile("Zulu"))).isFalse();
  }
}