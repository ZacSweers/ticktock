package dev.zacsweers.ticktock.android.lazyzonerules;

import android.content.Context;
import dev.zacsweers.ticktock.android.AssetsZoneDataLoader;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import dev.zacsweers.ticktock.runtime.lazyzonerules.LazyZoneDataProvider;

public final class AndroidLazyZoneRules {
  public static void init(Context context) {
    System.setProperty(
        "java.time.zone.DefaultZoneRulesProvider",
        "dev.zacsweers.ticktock.runtime.TickTockZoneRulesProvider"
    );

    TickTockPlugins.setZoneIdsProvider(GeneratedZoneIdsProvider::new);

    Context applicationContext = context.getApplicationContext();
    TickTockPlugins.setZoneDataProvider(() -> {
      ZoneDataLoader zoneDataLoader = AssetsZoneDataLoader.create(applicationContext);
      return new LazyZoneDataProvider(zoneDataLoader);
    });
  }
}
