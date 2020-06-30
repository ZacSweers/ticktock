package dev.zacsweers.ticktock.android.lazyzonerules.startup;

import android.content.Context;
import androidx.startup.Initializer;
import dev.zacsweers.ticktock.android.lazyzonerules.AndroidLazyZoneRules;
import java.util.Collections;
import java.util.List;

class AndroidLazyZoneRulesInitializer implements Initializer<AndroidLazyZoneRulesInitializer> {

  @Override public AndroidLazyZoneRulesInitializer create(Context context) {
    AndroidLazyZoneRules.init(context);
    return this;
  }

  @Override public List<Class<? extends Initializer<?>>> dependencies() {
    // No dependencies on other libraries.
    return Collections.emptyList();
  }
}