package dev.zacsweers.ticktock.android.tests;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import dev.zacsweers.ticktock.android.lazyzonerules.AndroidLazyZoneRules;
import dev.zacsweers.ticktock.runtime.EagerZoneRulesLoading;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class LazyZoneRulesTest {

  @Test
  public void init() {
    AndroidLazyZoneRules.init(ApplicationProvider.getApplicationContext());
    TestLogger logger = TestLogger.createAndInstall();
    EagerZoneRulesLoading.cacheZones();
    logger.assertDidLog();
  }
}
