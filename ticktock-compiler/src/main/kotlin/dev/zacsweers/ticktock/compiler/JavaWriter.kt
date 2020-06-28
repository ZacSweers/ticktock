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

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import java.nio.file.Files
import java.nio.file.Path
import java.util.Arrays
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.STATIC

internal class JavaWriter(
  private val outputDir: Path
) : RulesWriter {

  override fun writeZoneIds(packageName: String, version: String, zoneIds: Set<String>) {
    val typeSpec = TypeSpec.classBuilder("LazyZoneRules")
      .addModifiers(FINAL)
      .addField(version(version))
      .addField(regionId(zoneIds))
      .build()

    Files.createDirectories(outputDir)
    JavaFile.builder(packageName, typeSpec)
      .build()
      .writeTo(outputDir)
  }

  private fun version(version: String): FieldSpec {
    return FieldSpec.builder(String::class.java, "VERSION", STATIC, FINAL)
      .initializer("\$S", version)
      .build()
  }

  private fun regionId(allRegionIds: Set<String>): FieldSpec {
    val blocks = allRegionIds.map { CodeBlock.of("\$S", it) }
    val joinedBlocks = CodeBlock.join(blocks, ",\n")
    val initializer = CodeBlock.builder()
      .add("\$T.asList(\n$>$>", Arrays::class.java)
      .add(joinedBlocks)
      .add("$<$<)")
      .build()

    val listType: TypeName = ParameterizedTypeName.get(MutableList::class.java, String::class.java)
    return FieldSpec.builder(listType, "REGION_IDS", STATIC, FINAL)
      .initializer(initializer)
      .build()
  }
}
