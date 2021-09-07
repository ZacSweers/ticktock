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
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  kotlin("jvm") version "1.4.30"
  kotlin("kapt") version "1.4.30"
  id("org.jetbrains.dokka") version "1.5.0"
  id("com.vanniktech.maven.publish") version "0.17.0"
  id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.7.1"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.release.set(8)
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    @Suppress("SuspiciousCollectionReassignment")
    freeCompilerArgs += listOf("-progressive")
    jvmTarget = "1.8"
    // Gradle has ridiculous Kotlin handling
    languageVersion = "1.4"
  }
}

kotlin {
  explicitApi()
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
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
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

tasks.named<DokkaTask>("dokkaHtml") {
  outputDirectory.set(rootDir.resolve("../docs/0.x"))
  dokkaSourceSets.configureEach {
    skipDeprecated.set(true)
    externalDocumentationLink {
      url.set(URL("https://docs.gradle.org/${gradle.gradleVersion}/javadoc/index.html"))
    }
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.5.30")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
  implementation("de.undercouch:gradle-download-task:4.1.1")
}
