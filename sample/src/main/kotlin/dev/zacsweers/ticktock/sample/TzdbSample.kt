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
package dev.zacsweers.ticktock.sample

import dev.zacsweers.ticktock.jvm.tzdb.JvmTzdbZoneRules
import dev.zacsweers.ticktock.runtime.EagerZoneRulesLoading
import java.time.Instant
import java.time.ZoneId
import kotlin.system.measureTimeMillis

fun main() {
  println("Setting DefaultZoneRulesProvider property")
  JvmTzdbZoneRules.init()

  println("Loading default zone")
  measureTimeMillis {
    ZoneId.systemDefault().rules
  }.also {
    println("Default zone loading took $it milliseconds")
  }
  println("Caching remaining zones")
  measureTimeMillis {
    EagerZoneRulesLoading.cacheZones()
  }.also {
    println("Zone caching took $it milliseconds")
  }

  println("Finished! It is ${Instant.now()}. If you look at console output you should see that" +
      " TzdbZoneRulesProvider was used.")
}
