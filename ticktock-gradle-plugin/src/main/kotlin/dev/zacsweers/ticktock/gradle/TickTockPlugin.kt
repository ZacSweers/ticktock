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
package dev.zacsweers.ticktock.gradle

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.process.CommandLineArgumentProvider
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import java.io.File

private const val TICKTOCK_GROUP = "ticktock"

class TickTockPlugin : Plugin<Project> {

  companion object {
    const val INTERMEDIATES = "intermediates/ticktock"
  }

  override fun apply(project: Project) {
    project.setup()
  }

  private fun Project.setup() {
    // TODO how can we depend on the direct artifact as a default? Should we?
    val tickTockCompiler = configurations.maybeCreate("tickTockCompiler")
    tickTockCompiler.isVisible = false
    // Necessary with Clikt/MPP deps on the classpath of this
    // See https://github.com/gradle/gradle/issues/12126
    tickTockCompiler.attributes {
      attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
    }

    val threetenbp = configurations.maybeCreate("threetenbp")
    dependencies.add(threetenbp.name, "org.threeten:threetenbp:1.4.4")

    val extension = extensions.create<TickTockExtension>("tickTock")

    val tzdbVersion = extension.tzVersion
    val tzDbOutput = tzdbVersion.flatMap {
      layout.buildDirectory.file("$INTERMEDIATES/$it/download/$it.tar.gz")
    }
        .map { it.asFile }
    val downloadTzdb = tasks.register<Download>("downloadTzData") {
      group = TICKTOCK_GROUP
      src(tzdbVersion.map { "https://data.iana.org/time-zones/releases/tzdata$it.tar.gz" })
      dest(tzDbOutput)
    }

    val unzippedOutput = tzdbVersion.flatMap {
      layout.buildDirectory.dir("$INTERMEDIATES/$it/unpacked/$it")
    }
    val unzipTzData = tasks.register<Sync>("unzipTzdata") {
      group = TICKTOCK_GROUP
      from(tzDbOutput.map { tarTree(resources.gzip(it)) })
      into(unzippedOutput)
    }
    unzipTzData.dependsOn(downloadTzdb)

    // Set up lazy rules
    tasks.register<GenerateZoneRuleFilesTask>("generateLazyZoneRules") {
      classpath(tickTockCompiler)
      mainClass.set("dev.zacsweers.ticktock.compiler.LazyZoneRulesCompiler")
      tzVersion.set(tzdbVersion)
      inputDir.set(unzipTzData.map { it.destinationDir })
      argumentProviders.add(CommandLineArgumentProvider(::computeArguments))

      tzOutputDir.set(extension.tzOutputDir)
      codeOutputDir.set(extension.codeOutputDir)
      language.set(extension.language)
      packageName.set(extension.packageName)
    }

    val tzdatOutputDir = tzdbVersion.flatMap { layout.buildDirectory.dir("$INTERMEDIATES/$it/dat") }
    val generateTzdbDat = tasks.register<GenerateTzDatTask>("generateTzdbDat") {
      classpath(threetenbp)
      mainClass.set("org.threeten.bp.zone.TzdbZoneRulesCompiler")
      tzVersion.set(tzdbVersion)
      inputDir.set(unzipTzData.map { it.destinationDir.parentFile })
      outputDir.set(tzdatOutputDir)
      argumentProviders.add(CommandLineArgumentProvider(::computeArguments))
    }

    val syncTask = tasks.register<Sync>("syncTzDatToResources") {
      group = TICKTOCK_GROUP
      from(generateTzdbDat.map { it.outputDir })
      into(extension.tzOutputDir.map { it.dir("j\$/time/zone") })
      // The CLI outputs TZDB.dat but we want lowercase
      rename {
        it.replace("TZDB.dat", "tzdb.dat")
      }
    }

    generateTzdbDat.configure {
      finalizedBy(syncTask)
    }
  }

  private fun <T : Task> TaskProvider<out T>.dependsOn(
      vararg tasks: TaskProvider<out Task>
  ): TaskProvider<out T> = apply {
    if (tasks.isNotEmpty()) {
      configure { dependsOn(*tasks) }
    }
  }
}

/** A zone rules generation task for granular zone rules. */
@CacheableTask
abstract class GenerateZoneRuleFilesTask : JavaExec() {
  @get:Input
  abstract val tzVersion: Property<String>

  @get:Input
  abstract val language: Property<String>

  @get:Input
  abstract val packageName: Property<String>

  @get:PathSensitive(PathSensitivity.RELATIVE)
  @get:InputDirectory
  abstract val inputDir: Property<File>

  @get:OutputDirectory
  abstract val tzOutputDir: DirectoryProperty

  @get:OutputDirectory
  abstract val codeOutputDir: DirectoryProperty

  init {
    group = TICKTOCK_GROUP
  }

  fun computeArguments(): List<String> {
    return listOf(
        "--srcdir",
        inputDir.get().canonicalPath,
        "--codeoutdir",
        codeOutputDir.get().asFile.canonicalPath,
        "--tzdboutdir",
        tzOutputDir.get().asFile.canonicalPath,
        "--version",
        tzVersion.get(),
        "--language",
        language.get(),
        "--packagename",
        packageName.get()
    )
  }
}

/** A zone rules generation task for `tzdb.dat`. */
@CacheableTask
abstract class GenerateTzDatTask : JavaExec() {
  @get:Input
  abstract val tzVersion: Property<String>

  @get:PathSensitive(PathSensitivity.RELATIVE)
  @get:InputDirectory
  abstract val inputDir: Property<File>

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  init {
    group = TICKTOCK_GROUP
  }

  fun computeArguments(): List<String> {
    return listOf(
        "-srcdir",
        inputDir.get().canonicalPath,
        "-dstdir",
        outputDir.get().asFile.canonicalPath,
        "-version",
        tzVersion.get(),
        "-unpacked"
    )
  }
}
