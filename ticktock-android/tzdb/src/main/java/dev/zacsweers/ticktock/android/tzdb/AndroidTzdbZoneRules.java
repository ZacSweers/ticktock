package dev.zacsweers.ticktock.android.tzdb;

import android.content.Context;
import dev.zacsweers.ticktock.android.AssetsZoneDataLoader;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;

public class AndroidTzdbZoneRules {
  public static void init(Context context) {
    System.setProperty(
        "java.time.zone.DefaultZoneRulesProvider",
        "dev.zacsweers.ticktock.runtime.tzdb.TzdbZoneRulesProvider"
    );

    Context applicationContext = context.getApplicationContext();
    TickTockPlugins.setZoneRulesLoader(() -> AssetsZoneDataLoader.create(applicationContext));
  }
}
