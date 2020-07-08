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

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import dev.zacsweers.ticktock.android.lazyzonerules.AndroidLazyZoneRules;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class LazyZoneRulesTest {

  @Test
  public void init() {
    AndroidLazyZoneRules.init(ApplicationProvider.getApplicationContext());
    TestLogger logger = TestLogger.createAndInstall();
    TestEagerZoneRules.cacheZonesAndAssertLoaded();
    logger.assertDidLog();
  }
}
