package dev.zacsweers.ticktock.android.tzdb.startup;

import android.content.Context;
import androidx.startup.Initializer;
import dev.zacsweers.ticktock.android.tzdb.AndroidTzdbZoneRules;
import java.util.Collections;
import java.util.List;

class AndroidTzdbRulesInitializer implements Initializer<AndroidTzdbRulesInitializer> {

  @Override public AndroidTzdbRulesInitializer create(Context context) {
    AndroidTzdbZoneRules.init(context);
    return this;
  }

  @Override public List<Class<? extends Initializer<?>>> dependencies() {
    // No dependencies on other libraries.
    return Collections.emptyList();
  }
}