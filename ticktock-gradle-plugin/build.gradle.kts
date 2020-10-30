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
  kotlin("jvm") version "1.4.10"
  kotlin("kapt") version "1.4.10"
  id("org.jetbrains.dokka") apply false version "1.4.10"
  id("com.vanniktech.maven.publish") version "0.13.0"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
  exclusiveContent {
    forRepository {
      jcenter()
    }
    filter {
      // Required for Dokka
      includeModule("org.jetbrains.kotlinx", "kotlinx-html-jvm")
      includeGroup("org.jetbrains.dokka")
      includeModule("org.jetbrains", "markdown")
    }
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    freeCompilerArgs += listOf("-progressive")
    jvmTarget = "1.8"
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
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.10")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.4.10")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
  implementation("de.undercouch:gradle-download-task:4.1.1")
}
