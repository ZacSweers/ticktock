package dev.zacsweers.ticktock.android.lazyzonerules.startup;

import android.content.Context;
import androidx.startup.Initializer;
import dev.zacsweers.ticktock.android.lazyzonerules.AndroidLazyZoneRules;
import java.util.Collections;
import java.util.List;

/** androidx.startup {@link Initializer} for {@link AndroidLazyZoneRules}. */
public final class AndroidLazyZoneRulesInitializer implements Initializer<Class<AndroidLazyZoneRulesInitializer>> {

  @Override public Class<AndroidLazyZoneRulesInitializer> create(Context context) {
    AndroidLazyZoneRules.init(context);
    return AndroidLazyZoneRulesInitializer.class;
  }

  @Override public List<Class<? extends Initializer<?>>> dependencies() {
    // No dependencies on other libraries.
    return Collections.emptyList();
  }
}