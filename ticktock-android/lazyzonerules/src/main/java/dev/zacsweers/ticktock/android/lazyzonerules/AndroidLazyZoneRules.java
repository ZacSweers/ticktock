package dev.zacsweers.ticktock.android.lazyzonerules;

import android.content.Context;
import dev.zacsweers.ticktock.android.AssetsZoneDataLoader;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;

public class AndroidLazyZoneRules {
  public static void init(Context context) {
    System.setProperty(
        "java.time.zone.DefaultZoneRulesProvider",
        "dev.zacsweers.ticktock.runtime.lazyzonerules.LazyZoneRulesProvider"
    );

    TickTockPlugins.setZoneIdsProvider(GeneratedZoneIdsProvider::new);

    Context applicationContext = context.getApplicationContext();
    TickTockPlugins.setZoneRulesLoader(() -> AssetsZoneDataLoader.create(applicationContext));
  }
}
