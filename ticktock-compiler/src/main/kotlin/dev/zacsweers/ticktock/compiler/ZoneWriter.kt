/*
 * Copyright (C) 2020 Zac Sweers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.zacsweers.ticktock.compiler

import java.io.DataOutputStream
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.SortedMap
import org.threeten.bp.zone.ZoneRules
import org.threeten.bp.zone.ZoneRulesCompat.Companion.writeZoneRules

internal class ZoneWriter(private val outputDir: Path) {

  fun writeZones(zones: SortedMap<String, ZoneRules>) {
    if (Files.exists(outputDir)) {
      Files.walkFileTree(outputDir, deleteFilesRecursively)
    }
    for ((key, value) in zones) {
      writeZoneRulesFile(outputDir.resolve(fileName(key)), value)
    }
  }

  private fun writeZoneRulesFile(path: Path, rules: ZoneRules) {
    Files.createDirectories(path.parent)
    Files.createFile(path)
    DataOutputStream(Files.newOutputStream(path)).use { out ->
      out.writeByte(1)
      out.writeUTF("TZDB-ZONE")
      writeZoneRules(rules, out)
    }
  }

  private val deleteFilesRecursively: FileVisitor<Path> = object : SimpleFileVisitor<Path>() {
    override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
      Files.delete(file)
      return FileVisitResult.CONTINUE
    }

    override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
      Files.delete(dir)
      return FileVisitResult.CONTINUE
    }
  }

  companion object {
    fun fileName(key: String): String {
      return "tzdb/$key.dat"
    }
  }
}
