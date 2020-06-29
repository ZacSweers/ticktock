package dev.zacsweers.ticktock.runtime;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.zone.ZoneRulesException;

/**
 * A resources-based {@link ZoneDataLoader} that expects rules to be under "tzdb/" in resources.
 */
public final class ResourcesZoneDataLoader implements ZoneDataLoader {
  @Override public InputStream openData(String path) {
    try {
      URL datUrl = ResourcesZoneDataLoader.class.getClassLoader()
          .getResource(path);
      if (datUrl == null) {
        throw new ZoneRulesException("Missing time-zone data: " + path);
      }
      return new DataInputStream(new BufferedInputStream(datUrl.openStream()));
    } catch (Exception ex) {
      throw new ZoneRulesException("Invalid binary time-zone data: " + path, ex);
    }
  }
}
