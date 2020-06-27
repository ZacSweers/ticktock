package dev.zacsweers.ticktock.compiler;

import java.io.IOException;
import java.util.Set;

public interface RulesWriter {
  void writeZoneIds(String packageName, String version, Set<String> zoneIds) throws IOException;
}