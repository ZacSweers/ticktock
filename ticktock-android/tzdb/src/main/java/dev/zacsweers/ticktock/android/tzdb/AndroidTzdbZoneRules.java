package dev.zacsweers.ticktock.android.tzdb;

import android.annotation.SuppressLint;
import android.content.Context;
import dev.zacsweers.ticktock.runtime.android.AssetsZoneDataLoader;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import dev.zacsweers.ticktock.runtime.TzdbZoneDataProvider;
import java.util.function.Supplier;

/** Entry point for tzdb zone rules on Android. */
public final class AndroidTzdbZoneRules {

  /** Initializes the TickTockZoneRulesProvider using a {@link TzdbZoneDataProvider}. */
  @SuppressLint("NewApi")
  public static void init(Context context) {
    System.setProperty(
        "java.time.zone.DefaultZoneRulesProvider",
        "dev.zacsweers.ticktock.runtime.TickTockZoneRulesProvider"
    );

    Context applicationContext = context.getApplicationContext();
    Supplier<TzdbZoneDataProvider> supplier = () -> {
      ZoneDataLoader zoneDataLoader = AssetsZoneDataLoader.create(applicationContext);
      return new TzdbZoneDataProvider(zoneDataLoader);
    };
    TickTockPlugins.setZoneIdsProvider(() -> supplier.get());
    TickTockPlugins.setZoneDataProvider(() -> supplier.get());
  }
}
