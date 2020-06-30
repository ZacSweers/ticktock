package dev.zacsweers.ticktock.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.time.zone.ZoneRulesException;

/** An assets-based {@link ZoneDataLoader}. */
@TargetApi(Build.VERSION_CODES.O)
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
