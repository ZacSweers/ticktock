package dev.zacsweers.ticktock.jvm.tests;

import dev.zacsweers.ticktock.jvm.tzdb.JvmTzdbZoneRules;
import dev.zacsweers.ticktock.runtime.EagerZoneRulesLoading;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class TzdbZoneRulesTest {

  @Test
  public void init() {
    JvmTzdbZoneRules.init();
    TestLogger logger = TestLogger.createAndInstall();
    EagerZoneRulesLoading.cacheZones();
    logger.assertDidLog();
  }
}
