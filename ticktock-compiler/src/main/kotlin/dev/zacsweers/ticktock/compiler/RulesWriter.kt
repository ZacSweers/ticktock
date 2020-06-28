package dev.zacsweers.ticktock.compiler

interface RulesWriter {
    fun writeZoneIds(packageName: String, version: String, zoneIds: Set<String>)
}
