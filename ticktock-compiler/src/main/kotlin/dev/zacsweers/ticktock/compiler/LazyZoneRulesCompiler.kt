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
