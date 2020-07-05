package dev.zacsweers.ticktock.android.tzdb;

import android.annotation.SuppressLint;
import android.content.Context;
import dev.zacsweers.ticktock.runtime.android.AssetsZoneDataLoader;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import dev.zacsweers.ticktock.runtime.TzdbZoneProvider;
import java.util.function.Supplier;


public final class AndroidTzdbZoneRules {
  @SuppressLint("NewApi")
  public static void init(Context context) {
    System.setProperty(
        "java.time.zone.DefaultZoneRulesProvider",
        "dev.zacsweers.ticktock.runtime.TickTockZoneRulesProvider"
    );

    Context applicationContext = context.getApplicationContext();
    Supplier<TzdbZoneProvider> supplier = () -> {
      ZoneDataLoader zoneDataLoader = AssetsZoneDataLoader.create(applicationContext);
      return new TzdbZoneProvider(zoneDataLoader);
    };
    TickTockPlugins.setZoneIdsProvider(() -> supplier.get());
    TickTockPlugins.setZoneDataProvider(() -> supplier.get());
  }
}
