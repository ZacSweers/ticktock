package dev.zacsweers.ticktock.lazyrules.runtime;

import dev.zacsweers.ticktock.runtime.ZoneRulesLoader;
import dev.zacsweers.ticktock.runtime.internal.StandardZoneRules;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;

/**
 * A resources-based {@link ZoneRulesLoader} that expects rules to be under "tzdb/" in resources.
 */
public final class ResourcesZoneRulesLoader implements ZoneRulesLoader {
  @Override public ZoneRules loadData(String zoneId) {
    String fileName = "tzdb/" + zoneId + ".dat";
    InputStream is = null;
    try {
      URL datUrl = LazyZoneRulesProvider.class.getClassLoader()
          .getResource(fileName);
      if (datUrl == null) {
        throw new ZoneRulesException("Missing time-zone data: " + fileName);
      }
      is = new DataInputStream(new BufferedInputStream(datUrl.openStream()));
      return loadData(is);
    } catch (Exception ex) {
      throw new ZoneRulesException("Invalid binary time-zone data: " + fileName, ex);
    } finally {
      close(is);
    }
  }

  private ZoneRules loadData(InputStream in) throws Exception {
    DataInputStream dis = new DataInputStream(in);
    if (dis.readByte() != 1) {
      throw new StreamCorruptedException("File format not recognised");
    }

    String groupId = dis.readUTF();
    if (!"TZDB-ZONE".equals(groupId)) {
      throw new StreamCorruptedException("File format not recognised");
    }
    return StandardZoneRules.readExternal(dis);
  }

  private void close(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
