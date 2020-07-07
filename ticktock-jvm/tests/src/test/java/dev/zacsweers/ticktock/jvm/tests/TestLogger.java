package dev.zacsweers.ticktock.jvm.tests;

import dev.zacsweers.ticktock.runtime.TickTockLogger;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.truth.Truth.assertThat;

final class TestLogger implements TickTockLogger {

  static TestLogger createAndInstall() {
    TestLogger logger = new TestLogger();
    TickTockPlugins.setLogger(() -> logger);
    return logger;
  }

  private TestLogger() {

  }

  private final AtomicBoolean didLog = new AtomicBoolean();

  @Override public void log(String message) {
    didLog.set(true);
    SYSTEM.log(message);
  }

  void assertDidLog() {
    assertThat(didLog.get()).isTrue();
  }
}
