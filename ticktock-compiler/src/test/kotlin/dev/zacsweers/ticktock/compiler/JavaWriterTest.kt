/*
 * Copyright (C) 2020 Zac Sweers
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

import com.google.testing.compile.CompilationSubject
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjectSubject
import com.google.testing.compile.JavaFileObjects
import java.nio.file.Files
import java.nio.file.Path
import javax.tools.JavaFileObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class JavaWriterTest {

  @get:Rule
  val tmpFolder = TemporaryFolder()

  private lateinit var outputDir: Path
  private lateinit var javaWriter: JavaWriter

  @Before
  fun setup() {
    outputDir = tmpFolder.newFolder().toPath()
    javaWriter = JavaWriter(outputDir)
  }

  private fun generatedSource(version: String, vararg zoneIds: String): JavaFileObject {
    javaWriter.writeZoneIds("ticktock", version, setOf(*zoneIds))
    val output = outputDir.resolve("$SOURCE_NAME.java")
    val sourceString = Files.readAllBytes(output).decodeToString()
    return JavaFileObjects.forSourceString(SOURCE_NAME, sourceString)
  }

  @Test
  fun writeZoneIds() {
    val source = generatedSource("2010a", "Europe/Berlin", "UTC", "US/Pacific")
    val expected = JavaFileObjects.forSourceString(
        SOURCE_NAME,
        """package ticktock;

import dev.zacsweers.ticktock.runtime.ZoneIdsProvider;
import java.lang.Override;
import java.lang.String;
import java.util.Arrays;
import java.util.List;

final class GeneratedZoneIdsProvider implements ZoneIdsProvider {
  private static final String VERSION_ID = "2010a";

  private static final List<String> ZONE_IDS = Arrays.asList(
      "Europe/Berlin",
      "UTC",
      "US/Pacific");

  @Override
  public String getVersionId() {
    return VERSION_ID;
  }

  @Override
  public List<String> getZoneIds() {
    return ZONE_IDS;
  }
}""")
    val compilation = Compiler.javac().compile(source)
    CompilationSubject.assertThat(compilation).succeeded()
    JavaFileObjectSubject.assertThat(source).hasSourceEquivalentTo(expected)
  }

  companion object {
    private const val SOURCE_NAME = "ticktock/GeneratedZoneIdsProvider"
  }
}
