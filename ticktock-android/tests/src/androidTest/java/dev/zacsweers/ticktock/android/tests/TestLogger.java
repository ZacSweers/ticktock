/*
 * Copyright (C) 2020 Zac Sweers & Gabriel Ittner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.zacsweers.ticktock.android.tests;

import static com.google.common.truth.Truth.assertThat;

import android.util.Log;
import dev.zacsweers.ticktock.runtime.TickTockLogger;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import java.util.concurrent.atomic.AtomicBoolean;

final class TestLogger implements TickTockLogger {

  static TestLogger createAndInstall() {
    TestLogger logger = new TestLogger();
    TickTockPlugins.setLogger(() -> logger);
    return logger;
  }

  private TestLogger() {}

  private final AtomicBoolean didLog = new AtomicBoolean();

  @Override
  public void log(String message) {
    didLog.set(true);
    Log.d("TickTockTest", message);
  }

  void assertDidLog() {
    assertThat(didLog.get()).isTrue();
  }
}
