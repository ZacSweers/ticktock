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

import com.google.devtools.common.options.OptionsParser
import org.threeten.bp.zone.ZoneRulesCompat
import java.nio.file.Path

fun main(args: Array<String>) {
    val parser =
        OptionsParser.newOptionsParser(
            CompilerOptions::class.java
        )
    parser.parseAndExitUponError(args)
    val options = parser.getOptions(CompilerOptions::class.java)
    if (options != null && options.validate()) {
        LazyZoneRulesCompiler(options).run()
    }
}

class LazyZoneRulesCompiler(o: CompilerOptions) {

    private val version: String = o.version
    private val compiler: ZoneRulesCompat = ZoneRulesCompat(o.version, o.tzdbFiles(), o.leapSecondFile(), o.verbose)
    private val rulesWriter: RulesWriter = rulesWriter(o.language, o.codeOutputDir)
    private val zoneWriter: ZoneWriter = ZoneWriter(o.tzdbOutputDir)
    private val packageName: String = o.packageName

    private fun rulesWriter(language: CompilerOptions.Language, codeOutputDir: Path): RulesWriter {
        return if (language == CompilerOptions.Language.JAVA) {
            JavaWriter(codeOutputDir)
        } else {
            KotlinWriter(codeOutputDir)
        }
    }

    fun run() {
        try {
            val zones = compiler.compile()
            rulesWriter.writeZoneIds(packageName, version, zones.keys)
            zoneWriter.writeZones(zones)
        } catch (ex: Exception) {
            println("Failed: $ex")
            ex.printStackTrace()
        }
    }
}
