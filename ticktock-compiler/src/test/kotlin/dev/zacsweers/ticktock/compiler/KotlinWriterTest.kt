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
package dev.zacsweers.ticktock.compiler

import com.google.common.collect.ImmutableList
import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.OK
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import java.nio.file.Files
import java.nio.file.Path
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class KotlinWriterTest {

  @get:Rule
  var tmpFolder = TemporaryFolder()

  private lateinit var outputDir: Path
  private lateinit var kotlinWriter: KotlinWriter

  @Before
  fun setup() {
    outputDir = tmpFolder.newFolder().toPath()
    kotlinWriter = KotlinWriter(outputDir)
  }

  private fun generatedSource(version: String, vararg zoneIds: String): String {
    kotlinWriter.writeZoneIds("ticktock", version, setOf(*zoneIds))
    val output = outputDir.resolve(SOURCE_NAME)
    return Files.readAllBytes(output).decodeToString()
  }

  @Test
  fun writeZoneIds() {
    val source = generatedSource("2010a", "Europe/Berlin", "UTC", "US/Pacific")
    @Language("kotlin")
    val expectedSource = """package ticktock

import dev.zacsweers.ticktock.runtime.ZoneIdsProvider
import kotlin.String
import kotlin.collections.List

internal object GeneratedZoneIdsProvider : ZoneIdsProvider {
  private const val VERSION_ID: String = "2010a"

  private val ZONE_IDS: List<String> = listOf(
          "Europe/Berlin",
          "UTC",
          "US/Pacific"
      )

  override fun getVersionId(): String = VERSION_ID

  override fun getZoneIds(): List<String> = ZONE_IDS
}
"""
    assertThat(source).isEqualTo(expectedSource)
    val expected = kotlinFile("KClass.kt", expectedSource)
    val compilation = KotlinCompilation()
    compilation.sources = ImmutableList.of(expected)
    compilation.messageOutputStream = System.out
    compilation.inheritClassPath = true
    compilation.verbose = false
    val result = compilation.compile()
    assertThat(result.exitCode).isEqualTo(OK)
  }

  companion object {
    private const val SOURCE_NAME = "ticktock/GeneratedZoneIdsProvider.kt"
    private fun kotlinFile(name: String, @Language("kotlin") source: String): SourceFile {
      return kotlin(name, source, false)
    }
  }
}
