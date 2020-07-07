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
