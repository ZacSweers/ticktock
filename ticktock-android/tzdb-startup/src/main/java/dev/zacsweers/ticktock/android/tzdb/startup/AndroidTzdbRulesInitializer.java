package dev.zacsweers.ticktock.android.tzdb.startup;

import android.content.Context;
import androidx.startup.Initializer;
import dev.zacsweers.ticktock.android.tzdb.AndroidTzdbZoneRules;
import java.util.Collections;
import java.util.List;

/** androidx.startup {@link Initializer} for {@link AndroidTzdbZoneRules}. */
public final class AndroidTzdbRulesInitializer implements Initializer<Class<AndroidTzdbRulesInitializer>> {

  @Override public Class<AndroidTzdbRulesInitializer> create(Context context) {
    AndroidTzdbZoneRules.init(context);
    return AndroidTzdbRulesInitializer.class;
  }

  @Override public List<Class<? extends Initializer<?>>> dependencies() {
    // No dependencies on other libraries.
    return Collections.emptyList();
  }
}