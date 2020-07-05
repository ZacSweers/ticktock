package dev.zacsweers.ticktock.runtime.android;

import android.annotation.TargetApi;
import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import android.content.Context;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.time.zone.ZoneRulesException;

/** An assets-based {@link ZoneDataLoader}. */
@TargetApi(26)
public final class AssetsZoneDataLoader implements ZoneDataLoader {

  public static AssetsZoneDataLoader create(Context context) {
    return new AssetsZoneDataLoader(context.getApplicationContext());
  }

  private final Context context;

  private AssetsZoneDataLoader(Context context) {
    this.context = context;
  }

  @Override public DataInputStream openData(String path) {
    try {
      return new DataInputStream(new BufferedInputStream(context.getAssets()
          .open(path)));
    } catch (FileNotFoundException missingFileEx) {
      throw new ZoneRulesException("Invalid binary time-zone data: " + path, missingFileEx);
    } catch (Exception ex) {
      throw new ZoneRulesException("Invalid binary time-zone data: " + path, ex);
    }
  }
}
