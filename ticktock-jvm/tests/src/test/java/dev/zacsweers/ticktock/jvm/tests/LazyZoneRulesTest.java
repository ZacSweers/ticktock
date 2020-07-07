package dev.zacsweers.ticktock.jvm.tests;

import dev.zacsweers.ticktock.jvm.lazyzonerules.JvmLazyZoneRules;
import dev.zacsweers.ticktock.runtime.EagerZoneRulesLoading;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class LazyZoneRulesTest {

  @Test
  public void init() {
    JvmLazyZoneRules.init();
    TestLogger logger = TestLogger.createAndInstall();
    EagerZoneRulesLoading.cacheZones();
    logger.assertDidLog();
  }
}
