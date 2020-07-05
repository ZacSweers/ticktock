package dev.zacsweers.ticktock.android.lazyzonerules;

import android.content.Context;
import dev.zacsweers.ticktock.runtime.android.AssetsZoneDataLoader;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import dev.zacsweers.ticktock.runtime.LazyZoneDataProvider;

/** Entry point for lazy zone rules on Android. */
public final class AndroidLazyZoneRules {

  /** Initializes the TickTockZoneRulesProvider using a {@link LazyZoneDataProvider}. */
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
