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
package dev.zacsweers.ticktock.sample;

import dev.zacsweers.ticktock.jvm.lazyzonerules.JvmLazyZoneRules;
import dev.zacsweers.ticktock.runtime.EagerZoneRulesLoading;
import java.time.Instant;
import java.time.ZoneId;

public final class LazyZoneRulesSample {

  public static void main(String[] args) {
    System.out.println("Setting DefaultZoneRulesProvider property");
    JvmLazyZoneRules.init();

    System.out.println("Loading default zone");
    ZoneId.systemDefault().getRules();
    // System.out.println("Default zone loading took $it milliseconds");

    System.out.println("Caching remaining zones");
    EagerZoneRulesLoading.cacheZones();
    // System.out.println("Zone caching took $it milliseconds");

    System.out.println(
        "Finished! It is "
            + Instant.now()
            + ". If you look at console output you should see that TickTockZoneRulesProvider was used.");
  }
}
