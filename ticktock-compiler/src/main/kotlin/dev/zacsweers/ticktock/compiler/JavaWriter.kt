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

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import dev.zacsweers.ticktock.runtime.ZoneIdsProvider
import java.nio.file.Files
import java.nio.file.Path
import java.util.Arrays
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC

internal class JavaWriter(
  private val outputDir: Path
) : RulesWriter {

  override fun writeZoneIds(packageName: String, version: String, zoneIds: Set<String>) {
    val versionField = version(version)
    val zoneIdsField = regionId(zoneIds)
    val typeSpec = TypeSpec.classBuilder("GeneratedZoneIdsProvider")
        .addModifiers(FINAL)
        .addSuperinterface(ZoneIdsProvider::class.java)
        .addField(versionField)
        .addMethod(MethodSpec.methodBuilder("getVersionId")
            .addAnnotation(Override::class.java)
            .addModifiers(PUBLIC)
            .returns(versionField.type)
            .addStatement("return \$N", versionField)
            .build())
        .addField(zoneIdsField)
        .addMethod(MethodSpec.methodBuilder("getZoneIds")
            .addAnnotation(Override::class.java)
            .addModifiers(PUBLIC)
            .returns(zoneIdsField.type)
            .addStatement("return \$N", zoneIdsField)
            .build())
        .build()

    Files.createDirectories(outputDir)
    JavaFile.builder(packageName, typeSpec)
        .build()
        .writeTo(outputDir)
  }

  private fun version(version: String): FieldSpec {
    return FieldSpec.builder(String::class.java, "VERSION_ID", PRIVATE, STATIC, FINAL)
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

    val listType: TypeName = ParameterizedTypeName.get(List::class.java, String::class.java)
    return FieldSpec.builder(listType, "ZONE_IDS", PRIVATE, STATIC, FINAL)
        .initializer(initializer)
        .build()
  }
}
