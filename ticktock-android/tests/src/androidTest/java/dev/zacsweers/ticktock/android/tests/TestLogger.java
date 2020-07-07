package dev.zacsweers.ticktock.android.tests;

import android.util.Log;
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
    Log.d("TickTockTest", message);
  }

  void assertDidLog() {
    assertThat(didLog.get()).isTrue();
  }
}
