/*
 * Copyright (C) 2020 Zac Sweers & Gabriel Ittner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

pluginManagement {
  repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
    exclusiveContent {
      forRepository {
        maven {
          name = "JCenter"
          setUrl("https://jcenter.bintray.com/")
        }
      }
      filter {
        // Required for Dokka
        includeModule("org.jetbrains.kotlinx", "kotlinx-html-jvm")
        includeGroup("org.jetbrains.dokka")
        includeModule("org.jetbrains", "markdown")
      }
    }
  }
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "binary-compatibility-validator") {
        useModule("org.jetbrains.kotlinx:binary-compatibility-validator:${requested.version}")
      }
    }
  }
}

rootProject.name = "ticktock-root"
include(
  ":ticktock-compiler",
  ":ticktock-runtime",
  ":ticktock-android:lazyzonerules",
  ":ticktock-android:tests",
  ":ticktock-android:tzdb",
  ":ticktock-jvm:lazyzonerules",
  ":ticktock-jvm:tests",
  ":ticktock-jvm:tzdb",
  "sample"
)

includeBuild("ticktock-gradle-plugin") {
  dependencySubstitution {
    substitute(module("dev.zacsweers.ticktock:ticktock-gradle-plugin")).with(project(":"))
  }
}
