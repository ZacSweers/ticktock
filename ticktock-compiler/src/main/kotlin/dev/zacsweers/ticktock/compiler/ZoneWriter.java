package dev.zacsweers.ticktock.compiler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.SortedMap;
import org.threeten.bp.zone.ZoneRules;
import org.threeten.bp.zone.ZoneRulesCompat;

final class ZoneWriter {

  private final Path outputDir;

  ZoneWriter(Path outputDir) {
    this.outputDir = outputDir;
  }

  void writeZones(SortedMap<String, ZoneRules> zones) throws IOException {
    if (Files.exists(outputDir)) {
      Files.walkFileTree(outputDir, FILE_DELETER);
    }
    for (Map.Entry<String, ZoneRules> entry : zones.entrySet()) {
      writeZoneRulesFile(outputDir.resolve(fileName(entry.getKey())), entry.getValue());
    }
  }

  private void writeZoneRulesFile(Path path, ZoneRules rules) throws IOException {
    Files.deleteIfExists(path);
    if (!Files.exists(path.getParent())) {
      Files.createDirectories(path.getParent());
    }
    Files.createFile(path);
    try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(path))) {
      out.writeByte(1);
      out.writeUTF("TZDB-ZONE");
      ZoneRulesCompat.writeZoneRules(rules, out);
    }
  }

  static String fileName(String zoneId) {
    return "tzdb/" + zoneId + ".dat";
  }

  private final FileVisitor<Path> FILE_DELETER = new SimpleFileVisitor<Path>() {
    @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
      Files.delete(file);
      return FileVisitResult.CONTINUE;
    }

    @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc)
        throws IOException {
      Files.delete(dir);
      return FileVisitResult.CONTINUE;
    }
  };
}