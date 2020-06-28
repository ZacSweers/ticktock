/*
 * Copyright (c) 2020 Zac Sweers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.zacsweers.ticktock.compiler

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier.CONST
import com.squareup.kotlinpoet.KModifier.INTERNAL
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.joinToCode
import java.nio.file.Files
import java.nio.file.Path

internal class KotlinWriter(private val outputDir: Path) : RulesWriter {

    override fun writeZoneIds(
        packageName: String,
        version: String,
        zoneIds: Set<String>
    ) {
        val typeSpec = TypeSpec.objectBuilder("LazyZoneRules")
            .addModifiers(INTERNAL)
            .addProperty(version(version))
            .addProperty(regionId(zoneIds))
            .build()

        Files.createDirectories(outputDir)
        FileSpec.get(packageName, typeSpec)
            .writeTo(outputDir)
    }

    private fun version(version: String): PropertySpec {
        return PropertySpec.builder("VERSION", STRING, CONST)
            .initializer("%S", version)
            .build()
    }

    private fun regionId(allRegionIds: Set<String>): PropertySpec {
        val blocks = allRegionIds.map { CodeBlock.of("%S", it) }
        val joinedBlocks = blocks.joinToCode(",\n")
        val initializer = CodeBlock.builder()
            .add("listOf(\n⇥⇥")
            .add(joinedBlocks)
            .add("⇤⇤\n)")
            .build()

        val listType = LIST.parameterizedBy(STRING)
        return PropertySpec.builder("REGION_IDS", listType)
            .initializer(initializer)
            .build()
    }
}
