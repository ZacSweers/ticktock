package dev.zacsweers.ticktock.compiler

import org.threeten.bp.zone.ZoneRules
import org.threeten.bp.zone.ZoneRulesCompat.Companion.writeZoneRules
import java.io.DataOutputStream
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.SortedMap

internal class ZoneWriter(private val outputDir: Path) {

    fun writeZones(zones: SortedMap<String, ZoneRules>) {
        if (Files.exists(outputDir)) {
            Files.walkFileTree(outputDir, deleteFilesRecursively)
        }
        for ((key, value) in zones) {
            writeZoneRulesFile(outputDir.resolve("tzdb/$key.dat"), value)
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
}
