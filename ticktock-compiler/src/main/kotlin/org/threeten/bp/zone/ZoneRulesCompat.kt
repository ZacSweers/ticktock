package org.threeten.bp.zone

import java.io.DataOutputStream
import java.io.File
import java.util.SortedMap

class ZoneRulesCompat(version: String, sourceFiles: List<File>, leapSecondsFile: File?, verbose: Boolean) {
    private val compiler: TzdbZoneRulesCompiler = TzdbZoneRulesCompiler(version, sourceFiles, leapSecondsFile, verbose)

    init {
        compiler.setDeduplicateMap(mutableMapOf())
    }

    fun compile(): SortedMap<String, ZoneRules> {
        compiler.compile()
        return compiler.zones
    }

    companion object {
        fun writeZoneRules(rules: ZoneRules, stream: DataOutputStream?) {
            (rules as StandardZoneRules).writeExternal(stream)
        }
    }
}
