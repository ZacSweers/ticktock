package dev.zacsweers.ticktock.android.tests;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import dev.zacsweers.ticktock.android.tzdb.AndroidTzdbZoneRules;
import dev.zacsweers.ticktock.runtime.EagerZoneRulesLoading;
import dev.zacsweers.ticktock.runtime.TickTockLogger;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public final class TzdbZoneRulesTest {

  @Test
  public void init() {
    AndroidTzdbZoneRules.init(ApplicationProvider.getApplicationContext());
    AtomicBoolean initialized = new AtomicBoolean();
    TickTockPlugins.setLogger(
        () ->
            (TickTockLogger)
                message -> {
                  initialized.compareAndSet(false, true);
                  System.out.println(message);
                });
    EagerZoneRulesLoading.cacheZones();
    assertThat(initialized.get()).isTrue();
  }
}
