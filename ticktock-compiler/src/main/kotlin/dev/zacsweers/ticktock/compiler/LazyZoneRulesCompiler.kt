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
@file:JvmName("LazyZoneRulesCompiler")

package dev.zacsweers.ticktock.compiler

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.NullableOption
import com.github.ajalt.clikt.parameters.options.RawOption
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.enum
import org.threeten.bp.zone.ZoneRulesCompat
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale

internal class LazyZoneRulesCommand : CliktCommand(name = "ticktockc") {

  private val version: String by option(
    "--version",
    help = "Version of the time zone data, e.g. 2017b."
  ).default("")
    .validate { it.isNotBlank() }

  private val srcDir: Path by option(
    "--srcdir",
    help = "Directory containing the unpacked leapsecond and tzdb files."
  ).path(mustExist = true, canBeFile = false)
    .required()

  private val tzdbFileNames: List<String> by option(
    "--tzdbfiles",
    help = "Names of the tzdb files to process."
  ).split(",")
    .default(
      listOf(
        "africa",
        "antarctica",
        "asia",
        "australasia",
        "backward",
        "etcetera",
        "europe",
        "northamerica",
        "southamerica"
      )
    )

  private val leapSecondFileName: String by option(
    "--leapfile",
    help = "Name of the leapsecond file to process."
  ).default("leapseconds")

  private val codeOutputDir: Path by option(
    "--codeoutdir",
    help = "Output directory for the generated java code."
  ).path(canBeFile = false).required()

  private val tzdbOutputDir: Path by option(
    "--tzdboutdir",
    help = "Output directory for the generated tzdb files."
  ).path(canBeFile = false).required()

  private val verbose: Boolean by option(help = "Verbose output.").flag()

  private val language: Language by option(
    "--language",
    help = "Language output (java or kotlin)."
  ).enum<Language>().default(Language.JAVA)

  private val packageName: String by option(
    "--packagename",
    help = "Package name to output with."
  ).default("ticktock")

  private val tzdbFiles: List<File> by lazy {
    tzdbFileNames.asSequence()
      .map(srcDir::resolve)
      .filter { Files.exists(it) }
      .map(Path::toFile)
      .toList()
  }

  private fun leapSecondFile(): File? {
    val leapSecondsFile = srcDir.resolve(leapSecondFileName)
    if (!Files.exists(leapSecondsFile)) {
      println("Does not include leap seconds information.")
      return null
    }
    return leapSecondsFile.toFile()
  }

  private fun validate(): Boolean {
    var valid = true
    if (tzdbFiles.isEmpty()) {
      println("Did not find any timezone files.")
      valid = false
    }
    return valid
  }

  enum class Language {
    JAVA, KOTLIN
  }

  /**
   * Convert the option to a [Path].
   *
   * @param mustExist If true, fail if the given path does not exist
   * @param canBeFile If false, fail if the given path is a file
   * @param canBeDir If false, fail if the given path is a directory
   * @param mustBeWritable If true, fail if the given path is not writable
   * @param mustBeReadable If true, fail if the given path is not readable
   * @param canBeSymlink If false, fail if the given path is a symlink
   */
  private fun RawOption.path(
    mustExist: Boolean = false,
    canBeFile: Boolean = true,
    canBeDir: Boolean = true,
    mustBeWritable: Boolean = false,
    mustBeReadable: Boolean = false,
    canBeSymlink: Boolean = true
  ): NullableOption<Path, Path> {
    val name = pathType(canBeFile, canBeDir)
    return convert(
      metavar = name.uppercase(Locale.US),
      completionCandidates = CompletionCandidates.Path
    ) { transformContext ->
      convertToPath(
        path = transformContext,
        mustExist = mustExist,
        canBeFile = canBeFile,
        canBeDir = canBeDir,
        mustBeWritable = mustBeWritable,
        mustBeReadable = mustBeReadable,
        canBeSymlink = canBeSymlink
      ) { errorMessage -> fail(errorMessage) }
    }
  }

  private fun pathType(fileOkay: Boolean, folderOkay: Boolean): String = when {
    fileOkay && !folderOkay -> "File"
    !fileOkay && folderOkay -> "Directory"
    else -> "Path"
  }

  private fun convertToPath(
    path: String,
    mustExist: Boolean,
    canBeFile: Boolean,
    canBeDir: Boolean,
    mustBeWritable: Boolean,
    mustBeReadable: Boolean,
    canBeSymlink: Boolean,
    fail: (String) -> Unit
  ): Path {
    val name = pathType(canBeFile, canBeDir)
    return Paths.get(path).also {
      if (mustExist && !Files.exists(it)) fail("$name \"$it\" does not exist.")
      if (!canBeFile && Files.isRegularFile(it)) fail("$name \"$it\" is a file.")
      if (!canBeDir && Files.isDirectory(it)) fail("$name \"$it\" is a directory.")
      if (mustBeWritable && !Files.isWritable(it)) fail("$name \"$it\" is not writable.")
      if (mustBeReadable && !Files.isReadable(it)) fail("$name \"$it\" is not readable.")
      if (!canBeSymlink && Files.isSymbolicLink(it)) fail("$name \"$it\" is a symlink.")
    }
  }

  private fun rulesWriter(): RulesWriter {
    return if (language == Language.JAVA) {
      JavaWriter(codeOutputDir)
    } else {
      KotlinWriter(codeOutputDir)
    }
  }

  override fun run() {
    validate()
    val compiler = ZoneRulesCompat(
      version = version,
      sourceFiles = tzdbFiles,
      leapSecondsFile = leapSecondFile(),
      verbose = verbose
    )
    val rulesWriter = rulesWriter()
    val zoneWriter = ZoneWriter(tzdbOutputDir)
    try {
      val zones = compiler.compile()
      rulesWriter.writeZoneIds(packageName, version, zones.keys)
      zoneWriter.writeZones(zones)
    } catch (ex: Exception) {
      System.err.println("Failed: $ex")
      ex.printStackTrace()
    }
  }
}

fun main(argv: Array<String>) {
  LazyZoneRulesCommand().main(argv)
}
