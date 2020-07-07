/*
 * Copyright (c) 2020 Zac Sweers
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
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  kotlin("jvm") version "1.3.72"
  kotlin("kapt") version "1.3.72"
  id("com.vanniktech.maven.publish") version "0.12.0"
}

buildscript {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    jcenter()
  }
}

repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
  jcenter()
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    freeCompilerArgs = listOf("-progressive")
    jvmTarget = "1.8"
  }
}

tasks.withType<Test>().configureEach {
  beforeTest(closureOf<TestDescriptor> {
    logger.lifecycle("Running test: $this")
  })
}

sourceSets {
  getByName("test").resources.srcDirs("$buildDir/pluginUnderTestMetadata")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
  plugins {
    plugins.create("ticktock") {
      id = "dev.zacsweers.ticktock"
      implementationClass = "dev.zacsweers.ticktock.gradle.TickTockPlugin"
      description = project.findProperty("POM_DESCRIPTION").toString()
    }
  }
}

kotlinDslPluginOptions {
  experimentalWarning.set(false)
}

mavenPublish {
  nexus {
    stagingProfile = "dev.zacsweers"
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.3.72")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
  implementation("de.undercouch:gradle-download-task:4.0.4")
}
